package de.chrlembeck.codegen.model.info;

public interface ElementSetter<ClassType, PropertyType> {

    void setElement(ClassType object, int index, PropertyType element);
}