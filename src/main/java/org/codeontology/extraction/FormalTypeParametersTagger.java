package org.codeontology.extraction;

import org.codeontology.Ontology;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;

public class FormalTypeParametersTagger {
    private GenericDeclarationEntity<?> genericDeclaration;

    public FormalTypeParametersTagger(GenericDeclarationEntity<?> genericDeclaration) {
        this.genericDeclaration = genericDeclaration;
    }

    public void tagFormalTypeParameters() {
        List<TypeVariableEntity> parameters = genericDeclaration.getFormalTypeParameters();
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            TypeVariableEntity typeVariable = parameters.get(i);
            typeVariable.setParent(genericDeclaration);
            typeVariable.setPosition(i);
            RDFLogger.getInstance().addTriple(genericDeclaration, Ontology.FORMAL_TYPE_PARAMETER_PROPERTY, typeVariable);
            typeVariable.extract();
        }
    }

    public static List<TypeVariableEntity> formalTypeParametersOf(GenericDeclarationEntity<?> genericDeclaration) {
        List<CtTypeReference<?>> parameters = genericDeclaration.getElement().getFormalTypeParameters();
        List<TypeVariableEntity> typeVariables = new ArrayList<>();

        for (CtTypeReference parameter : parameters) {
            Entity<?> entity = EntityFactory.getInstance().wrap(parameter);
            if (entity instanceof TypeVariableEntity) {
                typeVariables.add((TypeVariableEntity) entity);
            }
        }

        return typeVariables;
    }
}
