package de.chrlembeck.codegen.model.impl;

import de.chrlembeck.codegen.model.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TableImpl implements Table, Serializable {

    private Schema schema;

    private String tableName;

    private String javaName;

    private String tableType;

    private PrimaryKey primaryKey;

    private List<Reference> references = new ArrayList<>();

    private List<Column> columns = new ArrayList<>();

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getJavaName() {
        return javaName;
    }

    @Override
    public void setJavaName(String newJavaName) {
        this.javaName = newJavaName;
    }

    @Override
    public Iterable<Column> getColumns() {
        return columns;
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    @Override
    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    @Override
    public void setPrimaryKey(PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public Iterable<Reference> getReferences() {
        return references;
    }

    @Override
    public Column getColumnByName(String columnName) {
        return columns.stream()
                .filter(col -> columnName.equals(col.getColumnName()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public void addReference(Reference reference) {
        references.add(reference);
    }

    public String getTableType() {
        return tableType;
    }

    @Override
    public void addColumn(Column column) {
        columns.add(column);
    }
}