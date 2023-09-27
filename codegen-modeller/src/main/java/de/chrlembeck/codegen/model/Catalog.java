package de.chrlembeck.codegen.model;

public interface Catalog {

    Iterable<Schema> getSchemas();

    void setCatalogName(String newCatalogName);

    String getCatalogName();

    void addSchema(Schema schema);
}