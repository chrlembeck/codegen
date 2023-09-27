package de.chrlembeck.codegen.model.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.sql.DataSource;

import de.chrlembeck.codegen.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.util.console.ConsoleTable;

public class GenericDBModelReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericDBModelReader.class);

    private DataSource dataSource;

    public GenericDBModelReader(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Model readModel(final String schemaPattern) throws SQLException {
        LOGGER.debug("reading model.");
        try (final Connection con = dataSource.getConnection()) {
            final DatabaseMetaData metaData = con.getMetaData();
            final Model model = new ModelImpl();
            readCatalogs(model, metaData);
            for (final Catalog catalog : model.getCatalogs()) {
                LOGGER.info("reading catalog: '" + catalog.getCatalogName() + "'.");
                readSchemas(catalog, metaData, schemaPattern);
                for (final Schema schema : catalog.getSchemas()) {
                    LOGGER.info("reading schema: '" + schema.getSchemaName() + "'.");
                    readTables(schema, metaData);
                    for (final Table table : schema.getTables()) {
                        LOGGER.info("reading table: '" + table.getTableName() + "'.");
                        readColumns(table, metaData);
                        LOGGER.info("reading primary keys");
                        readPrimaryKeys(table, metaData);
                    }
                }
            }

            for (final Catalog catalog : model.getCatalogs()) {
                for (final Schema schema : catalog.getSchemas()) {
                    for (final Table entity : schema.getTables()) {
                        LOGGER.info("reading references for: " + entity.getSchema().getSchemaName() + "."
                                + entity.getTableName());
                        readReferences(model, entity, metaData);
                    }
                }
            }

            return model;
        }
    }

    private void readReferences(final Model model, final Table entity, final DatabaseMetaData metaData)
            throws SQLException {
        try (ResultSet rs = metaData.getCrossReference(entity.getSchema().getCatalog().getCatalogName(),
                entity.getSchema().getSchemaName(), entity.getTableName(), null, null,
                null)) {
            // final ConsoleTable table = extractData(rs);
            // System.out.println(table.toString());

            String lastfkName = null;
            String fkName = null;
            final List<Column> pkAttributes = new ArrayList<>();
            final List<Column> fkAttributes = new ArrayList<>();
            while (rs.next()) {
                final String pkCatalogName = rs.getString(1);
                final String pkSchemaName = rs.getString(2);
                final String pkTableName = rs.getString(3);
                final String pkColumnName = rs.getString(4);

                final String fkCatalogName = rs.getString(5);
                final String fkSchemaName = rs.getString(6);
                final String fkTableName = rs.getString(7);
                final String fkColumnName = rs.getString(8);
                fkName = rs.getString(12);
                if (lastfkName == null) {
                    lastfkName = fkName;
                }
                if (!fkName.equals(lastfkName)) {
                    Reference reference = ReferenceImpl.createReference(fkName, pkAttributes.toArray(new Column[pkAttributes.size()]),
                            fkAttributes.toArray(new Column[fkAttributes.size()]));
                    entity.addReference(reference);
                    lastfkName = fkName;
                    pkAttributes.clear();
                    fkAttributes.clear();
                }
                pkAttributes.add(getAttributeByNames(model, pkCatalogName, pkSchemaName, pkTableName, pkColumnName));
                fkAttributes.add(getAttributeByNames(model, fkCatalogName, fkSchemaName, fkTableName, fkColumnName));

            }
            if (!pkAttributes.isEmpty()) {
                Reference reference = ReferenceImpl.createReference(fkName, pkAttributes.toArray(new Column[pkAttributes.size()]),
                        fkAttributes.toArray(new Column[fkAttributes.size()]));
                entity.addReference(reference);
            }
        }
    }

    protected void readColumns(final Table table, final DatabaseMetaData metaData) throws SQLException {
        try (ResultSet rs = metaData.getColumns(table.getSchema().getCatalog().getCatalogName(),
                table.getSchema().getSchemaName(), table.getTableName(), null)) {
            while (rs.next()) {
                Column column = new ColumnImpl();
                column.setColumnName(rs.getString(4));
                column.setDataType(rs.getInt(5));
                column.setTypeName(rs.getString(6));
                column.setColumnSize(rs.getInt(7));
                column.setDecimalDigits(rs.getInt(9));
                column.setNumPrecRadix(rs.getInt(10));
                column.setNullable(rs.getInt(11));
                column.setRemarks(rs.getString(12));
                column.setColumnDef(rs.getString(13));
                column.setCharOctetLength(rs.getInt(16));
                column.setOrdinalPosition(rs.getInt(17));
                column.setIsNullable(rs.getString(18));
                column.setScopeCatalog(rs.getString(19));
                column.setScopeSchema(rs.getString(20));
                column.setScopeTable(rs.getString(21));
                column.setSourceDataType(rs.getString(22));
                column.setIsAutoincrement(rs.getString(23));
                column.setIsGeneratedColumn(rs.getString(24));
                table.addColumn(column);
            }
        }
    }

    protected void readPrimaryKeys(final Table entity, final DatabaseMetaData metaData) throws SQLException {
        try (ResultSet rs = metaData.getPrimaryKeys(entity.getSchema().getCatalog().getCatalogName(),
                entity.getSchema().getSchemaName(), entity.getTableName())) {
            if (rs.next()) {
                final Map<Short, Column> keyMap = new TreeMap<>();
                final PrimaryKey key = new PrimaryKeyImpl();
                key.setPrimaryKeyName(rs.getString(6));
                do {
                    final Column attribute = entity.getColumnByName(rs.getString(4));
                    attribute.setPrimaryKeyColumn(Boolean.TRUE);
                    keyMap.put(rs.getShort(5), attribute);
                } while (rs.next());
                final List<Column> keys = new ArrayList<>(keyMap.values());
                key.setKeys(keys);
                entity.setPrimaryKey(key);
            }
        }
    }

    protected void readTables(final Schema schema, final DatabaseMetaData metaData) throws SQLException {
        try (ResultSet rs = metaData.getTables(schema.getCatalog().getCatalogName(),
                schema.getSchemaName(), null, null)) {
            while (rs.next()) {
                final String tableName = rs.getString(3);
                final String tableType = rs.getString(4);
                final Table table = new TableImpl();
                table.setTableName(tableName);
                table.setTableType(tableType);
                schema.addTable(table);
            }
        }
    }

    protected void readCatalogs(final Model model, final DatabaseMetaData metaData)
            throws SQLException {
        try (ResultSet rs = metaData.getCatalogs()) {
            while (rs.next()) {
                final String catalogName = rs.getString(1);
                final Catalog catalog = new CatalogImpl();
                catalog.setCatalogName(catalogName);
                model.addCatalog(catalog);
            }
        }
    }

    protected void readSchemas(final Catalog catalog, final DatabaseMetaData metaData,
            final String schemaPattern)
            throws SQLException {
        try (ResultSet rs = metaData.getSchemas(catalog.getCatalogName(), schemaPattern)) {
            while (rs.next()) {
                final String schemaName = rs.getString(1);
                final Schema schema = new SchemaImpl();
                schema.setSchemaName(schemaName);
                catalog.addSchema(schema);
            }
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    public static ConsoleTable extractData(final ResultSet rs) throws SQLException {
        final ResultSetMetaData metaData = rs.getMetaData();
        final int columnCount = metaData.getColumnCount();
        final ConsoleTable table = new ConsoleTable(columnCount);
        final String[] labels = new String[columnCount];
        for (int colIdx = 0; colIdx < columnCount; colIdx++) {
            labels[colIdx] = metaData.getColumnLabel(colIdx + 1);
        }
        table.setColumnNames(labels);
        while (rs.next()) {
            final String[] row = new String[columnCount];
            for (int colIdx = 0; colIdx < columnCount; colIdx++) {
                row[colIdx] = rs.getString(colIdx + 1);
            }
            table.addRow(row);
        }
        return table;
    }

    public static Column getAttributeByNames(final Model model, final String catalogName, final String schemaName,
            final String tableName, final String attributeName) {
        for (final Catalog catalog : model.getCatalogs()) {
            if (Objects.equals(catalog.getCatalogName(), catalogName)) {
                for (final Schema schema : catalog.getSchemas()) {
                    if (Objects.equals(schema.getSchemaName(), schemaName)) {
                        for (final Table entity : schema.getTables()) {
                            if (Objects.equals(entity.getTableName(), tableName)) {
                                for (final Column attribute : entity.getColumns()) {
                                    if (Objects.equals(attribute.getColumnName(), attributeName)) {
                                        return attribute;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}