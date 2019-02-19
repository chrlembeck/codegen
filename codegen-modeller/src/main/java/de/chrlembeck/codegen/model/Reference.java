package de.chrlembeck.codegen.model;

import java.util.List;

public interface Reference {

    Entity getPrimaryKeyTable();

    Entity getForeignKeyTable();

    List<ReferenceMapping> getReferenceMappings();

    String getForeignKeyName();
}