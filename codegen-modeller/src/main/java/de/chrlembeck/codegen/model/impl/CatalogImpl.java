package de.chrlembeck.codegen.model.impl;

import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CatalogImpl implements Catalog, Serializable {

    private String catalogName;

    private List<Schema> schemas = new ArrayList<>();

    @Override
    public Iterable<Schema> getSchemas() {
        return schemas;
    }

    @Override
    public void setCatalogName(String newCatalogName) {
        this.catalogName = newCatalogName;
    }

    @Override
    public String getCatalogName() {
        return catalogName;
    }

    @Override
    public void addSchema(Schema schema) {
        schemas.add(schema);
    }
}