package de.chrlembeck.codegen.model;

public interface ReferenceMapping {

    Column getPrimaryKeyColumn();

    Column getForeignKeyColumn();
}