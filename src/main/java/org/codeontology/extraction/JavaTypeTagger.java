package org.codeontology.extraction;

import org.codeontology.Ontology;

public class JavaTypeTagger {

    private TypedElementEntity<?> typedElement;
    private TypeEntity<?> type;

    public JavaTypeTagger(TypedElementEntity<?> typedElement) {
        this.typedElement = typedElement;
        this.type = typedElement.getJavaType();
    }

    private void tagJavaType() {
        RDFLogger.getInstance().addTriple(typedElement, Ontology.JAVA_TYPE_PROPERTY, type);
        type.follow();
    }

    public void tagJavaType(Entity<?> parent) {
        type.setParent(parent);
        tagJavaType();
    }
}
