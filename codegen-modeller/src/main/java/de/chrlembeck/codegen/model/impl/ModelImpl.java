package de.chrlembeck.codegen.model.impl;

import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.Model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelImpl implements Model, Serializable {

    private List<Catalog> catalogs = new ArrayList<>();

    @Override
    public Iterable<Catalog> getCatalogs() {
        return catalogs;
    }

    @Override
    public void addCatalog(Catalog catalog) {
        catalogs.add(catalog);
    }
}
