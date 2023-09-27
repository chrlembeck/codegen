package de.chrlembeck.codegen.model;

public interface Model {

    Iterable<Catalog> getCatalogs();

    void addCatalog(Catalog catalog);
}