package org.codeontology.extraction.declaration;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.extraction.ReflectionFactory;
import org.codeontology.extraction.support.FormalTypeParametersTagger;
import org.codeontology.extraction.support.GenericDeclarationEntity;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

public class MethodEntity extends ExecutableEntity<CtMethod<?>> implements GenericDeclarationEntity<CtMethod<?>> {
    public MethodEntity(CtMethod<?> method) {
        super(method);
    }

    public MethodEntity(CtExecutableReference<?> reference) {
        super(reference);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.METHOD_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
        tagReturns();
        if (isDeclarationAvailable()) {
            tagOverrides();
            tagFormalTypeParameters();
        }
    }

    public void tagOverrides() {
        try {
            CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference()).getOverridingExecutable();
            if (reference != null) {
                ExecutableEntity overridingMethod = getFactory().wrap(reference);
                getLogger().addTriple(this, Ontology.OVERRIDES_PROPERTY, overridingMethod);
                overridingMethod.follow();
            }
        } catch (Exception | Error e) {
            // could not get an overriding executable
        }
    }

    public void tagReturns() {
        getLogger().addTriple(this, Ontology.RETURNS_PROPERTY, getReturnType());
    }

    private TypeEntity getReturnType() {
        TypeEntity<?> returnType = getGenericReturnType();
        if (returnType != null) {
            return returnType;
        }

        CtTypeReference<?> reference = ((CtExecutableReference<?>) getReference()).getType();
        returnType = getFactory().wrap(reference);
        returnType.setParent(this);
        returnType.follow();

        return returnType;
    }

    private TypeEntity getGenericReturnType() {
        TypeEntity<?> result = null;
        if (!isDeclarationAvailable()) {
            try {
                CtExecutableReference<?> reference = ((CtExecutableReference<?>) getReference());
                Method method = (Method) ReflectionFactory.getInstance().createActualExecutable(reference);
                Type returnType = method.getGenericReturnType();

                if (returnType instanceof GenericArrayType ||
                    returnType instanceof TypeVariable<?> ) {

                    result = getFactory().wrap(returnType);
                    result.setParent(this);
                }

            } catch (Throwable t) {
                return null;
            }
        }

        return result;
    }

    @Override
    public List<TypeVariableEntity> getFormalTypeParameters() {
        return FormalTypeParametersTagger.formalTypeParametersOf(this);
    }

    @Override
    public void tagFormalTypeParameters() {
        new FormalTypeParametersTagger(this).tagFormalTypeParameters();
    }
}
