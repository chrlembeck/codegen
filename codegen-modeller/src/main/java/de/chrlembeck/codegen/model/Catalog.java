package de.chrlembeck.codegen.model;

public interface Catalog {

    Iterable<Schema> getSchemas();

    void setCatalogName(String newCatalogName);

    Schema createSchema();

    String getCatalogName();
}