package org.codeontology.extraction;

import com.hp.hpl.jena.rdf.model.Literal;
import org.codeontology.CodeOntology;
import org.codeontology.Ontology;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtReference;

public abstract class NamedElementEntity<E extends CtNamedElement> extends CodeElementEntity<E> {

    private CtReference reference;

    protected NamedElementEntity(E element) {
        setElement(element);
    }

    protected NamedElementEntity(CtReference reference) {
        setReference(reference);
    }

    @SuppressWarnings("unchecked")
    private void setReference(CtReference reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        }

        this.reference = reference;
        if (reference.getDeclaration() != null && getElement() == null) {
            setElement((E) reference.getDeclaration());
        }
    }

    @Override
    protected void setElement(E element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        super.setElement(element);
        if (reference == null) {
            try {
                this.reference = element.getReference();
            } catch (ClassCastException e) {
                // leave reference null
            }
        }
    }

    public CtReference getReference() {
        return reference;
    }

    public String getName() {
        return getReference().getSimpleName();
    }

    public void tagName() {
        Literal name = getModel().createTypedLiteral(getName());
        getLogger().addTriple(this, Ontology.NAME_PROPERTY, name);
    }

    @Override
    public void follow() {
        if (!isDeclarationAvailable() && !CodeOntology.isJarExplorationEnabled()
                && EntityRegister.getInstance().add(this)) {
            extract();
        }
    }

    public boolean isDeclarationAvailable() {
        return getElement() != null;
    }
}
