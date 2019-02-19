package de.chrlembeck.codegen.model;

public interface ReferenceMapping {

    Attribute getPrimaryKeyColumn();

    Attribute getForeignKeyColumn();
}