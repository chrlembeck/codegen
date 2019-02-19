package de.chrlembeck.codegen.model;

public interface Attribute {

    Entity getEntity();

    String getColumnName();

    String getJavaName();

    void setJavaName(String newJavaName);

    void setJavaType(String javaType);

    String getJavaType();

    /**
     * Gibt true zurück, falls das Attribut Teil eines Primärschlüssels ist.
     * 
     * @return true, falls das Attribut Teil eines Primärschlüssels ist, false, falls nicht und null, falls diese Angabe
     *         unbekannt ist.
     */
    Boolean isPrimaryKeyColumn();

    void setPrimaryKeyColumn(final Boolean primaryKeyColumn);

    void setColumnName(String columnName);

    void setCharOctetLength(final int charOctetLength);

    void setColumnSize(final int columnSize);

    void setColumnDef(final String columnDef);

    void setDecimalDigits(final int decimalDigits);

    void setDataType(final int dataType);

    void setIsAutoincrement(final String isAutoincrement);

    void setIsGeneratedColumn(final String isGeneratedColumn);

    void setIsNullable(final String isNullable);

    void setNullable(final int nullable);

    void setNumPrecRadix(final int numPrecRadix);

    void setOrdinalPosition(final int ordinalPosition);

    void setRemarks(final String remarks);

    void setScopeCatalog(final String scopeCatalog);

    void setScopeSchema(final String scopeSchema);

    void setScopeTable(final String scopeTable);

    void setSourceDataType(final String sourceDataType);

    void setTypeName(final String typeName);

    String getColumnDef();

    String getTypeName();

    int getColumnSize();

    int getDataType();

    int getDecimalDigits();

    int getNumPrecRadix();

    public int getCharOctetLength();

    public int getNullable();

    public String getIsNullable();
}