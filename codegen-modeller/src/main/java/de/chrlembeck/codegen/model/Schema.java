package de.chrlembeck.codegen.model;

public interface Schema {

    Iterable<Entity> getEntities();

    String getSchemaName();

    String getPackageName();

    void setSchemaName(String newSchemaName);

    void setPackageName(String newPackageName);

    Entity createEntity();

    Catalog getCatalog();
}