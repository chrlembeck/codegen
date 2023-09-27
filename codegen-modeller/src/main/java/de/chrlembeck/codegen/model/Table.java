package de.chrlembeck.codegen.model;

public interface Table {

    Schema getSchema();

    String getTableName();

    String getTableType();

    String getJavaName();

    void setJavaName(String newJavaName);

    Iterable<Column> getColumns();

    void setTableName(String tableName);

    void setTableType(String tableType);

    public PrimaryKey getPrimaryKey();

    void setPrimaryKey(PrimaryKey key);

    Iterable<Reference> getReferences();

    Column getColumnByName(String string);

    int getColumnCount();

    void addReference(Reference reference);

    void addColumn(Column column);
}