package de.chrlembeck.codegen.model;

public interface Model {

    Iterable<Catalog> getCatalogs();

    Catalog createCatalog();
}