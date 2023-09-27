package de.chrlembeck.codegen.model;

import java.util.List;

public interface Reference {

    Table getPrimaryKeyTable();

    Table getForeignKeyTable();

    List<ReferenceMapping> getReferenceMappings();

    String getForeignKeyName();
}