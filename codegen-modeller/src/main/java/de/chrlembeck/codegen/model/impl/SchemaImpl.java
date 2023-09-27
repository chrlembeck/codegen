package de.chrlembeck.codegen.model.impl;

import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.Table;
import de.chrlembeck.codegen.model.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SchemaImpl implements Schema, Serializable {

    private Catalog catalog;

    private String schemaName;

    private String packageName;

    private List<Table> tables = new ArrayList<>();

    @Override
    public Iterable<Table> getTables() {
        return tables;
    }

    @Override
    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void setSchemaName(String newSchemaName) {
        this.schemaName = newSchemaName;
    }

    @Override
    public void setPackageName(String newPackageName) {
        this.packageName = newPackageName;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public Catalog getCatalog() {
        return catalog;
    }

    @Override
    public void addTable(Table table) {
        tables.add(table);
    }
}