package de.chrlembeck.codegen.model;

public interface Entity {

    Schema getSchema();

    String getTableName();

    String getJavaName();

    void setJavaName(String newJavaName);

    Attribute createAttribute();

    Iterable<Attribute> getAttributes();

    void setTableName(String tableName);

    void setTableType(String tableType);

    public PrimaryKey getPrimaryKey();

    void setPrimaryKey(PrimaryKey key);

    Iterable<Reference> getReferences();

    Attribute getAttributeByColumnName(String string);

    int getAttributeCount();

    Reference createReference(String foreignKeyName, Attribute[] attributes, Attribute[] foreignKeyAttributes);
}