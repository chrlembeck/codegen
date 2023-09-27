package de.chrlembeck.codegen.model;

public interface Schema {

    Iterable<Table> getTables();

    String getSchemaName();

    String getPackageName();

    void setSchemaName(String newSchemaName);

    void setPackageName(String newPackageName);

    Catalog getCatalog();

    void addTable(Table table);
}