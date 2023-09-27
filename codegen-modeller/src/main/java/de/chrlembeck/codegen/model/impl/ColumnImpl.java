package de.chrlembeck.codegen.model.impl;

import de.chrlembeck.codegen.model.Column;
import de.chrlembeck.codegen.model.Table;
import java.io.Serializable;

public class ColumnImpl implements Column, Serializable {

    private Table table;

    /**
     * COLUMN_NAME String =&gt; column name
     */
    private String columnName;

    /**
     * DATA_TYPE int =&gt; SQL type from java.sql.Types
     */
    private int dataType;

    /**
     * TYPE_NAME String =&gt; Data source dependent type name, for a UDT the type name is fully qualified
     */
    private String typeName;

    /**
     * COLUMN_SIZE int =&gt; column size.
     */
    private int columnSize;

    /**
     * DECIMAL_DIGITS int =&gt; the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is
     * not applicable.
     */
    private int decimalDigits;

    /**
     * NUM_PREC_RADIX int =&gt; Radix (typically either 10 or 2)
     */
    private int numPrecRadix;

    /**
     * <pre>
     * NULLABLE int =&gt; is NULL allowed.
     * columnNoNulls - might not allow NULL values
     * columnNullable - definitely allows NULL values
     * columnNullableUnknown - nullability unknown
     * </pre>
     */
    private int nullable;

    /**
     * REMARKS String =&gt; comment describing column (may be null)
     */
    private String remarks;

    /**
     * COLUMN_DEF String =&gt; default value for the column, which should be interpreted as a string when the value is
     * enclosed in single quotes (may be null)
     */
    private String columnDef;

    /**
     * CHAR_OCTET_LENGTH int =&gt; for char types the maximum number of bytes in the column
     */
    private int charOctetLength;

    /**
     * ORDINAL_POSITION int =&gt; index of column in table (starting at 1)
     */
    private int ordinalPosition;

    /**
     * <pre>
     * IS_NULLABLE String =&gt; ISO rules are used to determine the nullability for a column.
     *  YES --- if the column can include NULLs
     *  NO --- if the column cannot include NULLs
     *  empty string --- if the nullability for the column is unknown
     * </pre>
     */
    private String isNullable;

    /**
     * SCOPE_CATALOG String =&gt; catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't
     * REF)
     */
    private String scopeCatalog;

    /**
     * SCOPE_SCHEMA String =&gt; schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't
     * REF)
     */
    private String scopeSchema;

    /**
     * SCOPE_TABLE String =&gt; table name that this the scope of a reference attribute (null if the DATA_TYPE isn't
     * REF)
     */
    private String scopeTable;

    /**
     * SOURCE_DATA_TYPE short =&gt; source type of a distinct type or user-generated Ref type, SQL type from
     * java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
     */
    private String sourceDataType;

    /**
     * <pre>
     * IS_AUTOINCREMENT String =&gt; Indicates whether this column is auto incremented
     *  YES --- if the column is auto incremented
     *  NO --- if the column is not auto incremented
     *  empty string --- if it cannot be determined whether the column is auto incremented
     * </pre>
     */
    private String isAutoincrement;

    /**
     * <pre>
     * IS_GENERATEDCOLUMN String =&gt; Indicates whether this is a generated column
     *  YES --- if this a generated column
     *  NO --- if this not a generated column
     *  empty string --- if it cannot be determined whether this is a generated column
     * </pre>
     */
    private String isGeneratedColumn;

    private String javaName;

    private String javaType;

    private Boolean primaryKeyColumn;


    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public String getColumnName() {
        return columnName;
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
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    @Override
    public String getJavaType() {
        return javaType;
    }

    @Override
    public Boolean isPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    @Override
    public void setPrimaryKeyColumn(Boolean primaryKeyColumn) {
        this.primaryKeyColumn = primaryKeyColumn;
    }

    @Override
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public void setCharOctetLength(int charOctetLength) {
        this.charOctetLength = charOctetLength;
    }

    @Override
    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    @Override
    public void setColumnDef(String columnDef) {
        this.columnDef = columnDef;
    }

    @Override
    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    @Override
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    @Override
    public void setIsAutoincrement(String isAutoincrement) {
        this.isAutoincrement = isAutoincrement;
    }

    @Override
    public void setIsGeneratedColumn(String isGeneratedColumn) {
        this.isGeneratedColumn = isGeneratedColumn;
    }

    @Override
    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }

    @Override
    public void setNullable(int nullable) {
        this.nullable = nullable;
    }

    @Override
    public void setNumPrecRadix(int numPrecRadix) {
        this.numPrecRadix = numPrecRadix;
    }

    @Override
    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    @Override
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public void setScopeCatalog(String scopeCatalog) {
        this.scopeCatalog = scopeCatalog;
    }

    @Override
    public void setScopeSchema(String scopeSchema) {
        this.scopeSchema = scopeSchema;
    }

    @Override
    public void setScopeTable(String scopeTable) {
        this.scopeTable = scopeTable;
    }

    @Override
    public void setSourceDataType(String sourceDataType) {
        this.sourceDataType = sourceDataType;
    }

    @Override
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String getColumnDef() {
        return columnDef;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public int getColumnSize() {
        return columnSize;
    }

    @Override
    public int getDataType() {
        return dataType;
    }

    @Override
    public int getDecimalDigits() {
        return decimalDigits;
    }

    @Override
    public int getNumPrecRadix() {
        return numPrecRadix;
    }

    @Override
    public int getCharOctetLength() {
        return charOctetLength;
    }

    @Override
    public int getNullable() {
        return nullable;
    }

    @Override
    public String getIsNullable() {
        return isNullable;
    }
}
