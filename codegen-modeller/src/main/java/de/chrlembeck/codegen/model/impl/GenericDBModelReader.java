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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.model.Attribute;
import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.Entity;
import de.chrlembeck.codegen.model.Model;
import de.chrlembeck.codegen.model.PrimaryKey;
import de.chrlembeck.codegen.model.Schema;
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
            final DBRootTreeNode model = new DBRootTreeNode();
            readCatalogs(model, metaData);
            for (final Catalog catalog : model.catalogTreeNodes()) {
                LOGGER.info("reading catalog: '" + catalog.getCatalogName() + "'.");
                readSchemas(catalog, metaData, schemaPattern);
                for (final Schema schema : catalog.getSchemas()) {
                    LOGGER.info("reading schema: '" + schema.getSchemaName() + "'.");
                    readTables(schema, metaData);
                    for (final Entity entity : schema.getEntities()) {
                        LOGGER.info("reading table: '" + entity.getTableName() + "'.");
                        readColumns(entity, metaData);
                        LOGGER.info("reading primary keys");
                        readPrimaryKeys(entity, metaData);
                    }
                }
            }

            for (final Catalog catalog : model.catalogTreeNodes()) {
                for (final Schema schema : catalog.getSchemas()) {
                    for (final Entity entity : schema.getEntities()) {
                        LOGGER.info("reading references for: " + entity.getSchema().getSchemaName() + "."
                                + entity.getTableName());
                        readReferences(model, entity, metaData);
                    }
                }
            }

            return model;
        }
    }

    private void readReferences(final Model model, final Entity entity, final DatabaseMetaData metaData)
            throws SQLException {
        try (ResultSet rs = metaData.getCrossReference(entity.getSchema().getCatalog().getCatalogName(),
                entity.getSchema().getSchemaName(), entity.getTableName(), null, null,
                null)) {
            // final ConsoleTable table = extractData(rs);
            // System.out.println(table.toString());

            String lastfkName = null;
            String fkName = null;
            final List<Attribute> pkAttributes = new ArrayList<>();
            final List<Attribute> fkAttributes = new ArrayList<>();
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
                    entity.createReference(fkName, pkAttributes.toArray(new Attribute[pkAttributes.size()]),
                            fkAttributes.toArray(new Attribute[fkAttributes.size()]));
                    lastfkName = fkName;
                    pkAttributes.clear();
                    fkAttributes.clear();
                }
                pkAttributes.add(getAttributeByNames(model, pkCatalogName, pkSchemaName, pkTableName, pkColumnName));
                fkAttributes.add(getAttributeByNames(model, fkCatalogName, fkSchemaName, fkTableName, fkColumnName));

            }
            if (!pkAttributes.isEmpty()) {
                entity.createReference(fkName, pkAttributes.toArray(new Attribute[pkAttributes.size()]),
                        fkAttributes.toArray(new Attribute[fkAttributes.size()]));
            }
        }
    }

    protected void readColumns(final Entity tableTreeNode, final DatabaseMetaData metaData) throws SQLException {
        try (ResultSet rs = metaData.getColumns(tableTreeNode.getSchema().getCatalog().getCatalogName(),
                tableTreeNode.getSchema().getSchemaName(), tableTreeNode.getTableName(), null)) {
            while (rs.next()) {
                final Attribute columnTreeNode = tableTreeNode.createAttribute();
                columnTreeNode.setColumnName(rs.getString(4));
                columnTreeNode.setDataType(rs.getInt(5));
                columnTreeNode.setTypeName(rs.getString(6));
                columnTreeNode.setColumnSize(rs.getInt(7));
                columnTreeNode.setDecimalDigits(rs.getInt(9));
                columnTreeNode.setNumPrecRadix(rs.getInt(10));
                columnTreeNode.setNullable(rs.getInt(11));
                columnTreeNode.setRemarks(rs.getString(12));
                columnTreeNode.setColumnDef(rs.getString(13));
                columnTreeNode.setCharOctetLength(rs.getInt(16));
                columnTreeNode.setOrdinalPosition(rs.getInt(17));
                columnTreeNode.setIsNullable(rs.getString(18));
                columnTreeNode.setScopeCatalog(rs.getString(19));
                columnTreeNode.setScopeSchema(rs.getString(20));
                columnTreeNode.setScopeTable(rs.getString(21));
                columnTreeNode.setSourceDataType(rs.getString(22));
                columnTreeNode.setIsAutoincrement(rs.getString(23));
                columnTreeNode.setIsGeneratedColumn(rs.getString(24));
            }
        }
    }

    protected void readPrimaryKeys(final Entity entity, final DatabaseMetaData metaData) throws SQLException {
        try (ResultSet rs = metaData.getPrimaryKeys(entity.getSchema().getCatalog().getCatalogName(),
                entity.getSchema().getSchemaName(), entity.getTableName())) {
            if (rs.next()) {
                final Map<Short, Attribute> keyMap = new TreeMap<>();
                final PrimaryKey key = new PrimaryKeyImpl();
                key.setPrimaryKeyName(rs.getString(6));
                do {
                    final Attribute attribute = entity.getAttributeByColumnName(rs.getString(4));
                    attribute.setPrimaryKeyColumn(Boolean.TRUE);
                    keyMap.put(rs.getShort(5), attribute);
                } while (rs.next());
                final List<Attribute> keys = new ArrayList<>(keyMap.values());
                key.setKeys(keys);
                entity.setPrimaryKey(key);
            }
        }
    }

    protected void readTables(final Schema schemaTreeNode, final DatabaseMetaData metaData) throws SQLException {
        try (ResultSet rs = metaData.getTables(schemaTreeNode.getCatalog().getCatalogName(),
                schemaTreeNode.getSchemaName(), null, null)) {
            while (rs.next()) {
                final String tableName = rs.getString(3);
                final String tableType = rs.getString(4);
                final Entity tableTreeNode = schemaTreeNode.createEntity();
                tableTreeNode.setTableName(tableName);
                tableTreeNode.setTableType(tableType);
            }
        }
    }

    protected void readCatalogs(final DBRootTreeNode parent, final DatabaseMetaData metaData)
            throws SQLException {
        try (ResultSet rs = metaData.getCatalogs()) {
            while (rs.next()) {
                final String catalogName = rs.getString(1);
                final CatalogTreeNode catalog = new CatalogTreeNode(catalogName);
                parent.add(catalog);
            }
        }
    }

    protected void readSchemas(final Catalog catalogTreeNode, final DatabaseMetaData metaData,
            final String schemaPattern)
            throws SQLException {
        try (ResultSet rs = metaData.getSchemas(catalogTreeNode.getCatalogName(), schemaPattern)) {
            while (rs.next()) {
                final String schemaName = rs.getString(1);
                final Schema schemaTreeNode = catalogTreeNode.createSchema();
                schemaTreeNode.setSchemaName(schemaName);
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

    public static Attribute getAttributeByNames(final Model model, final String catalogName, final String schemaName,
            final String tableName, final String attributeName) {
        for (final Catalog catalog : model.getCatalogs()) {
            if (Objects.equals(catalog.getCatalogName(), catalogName)) {
                for (final Schema schema : catalog.getSchemas()) {
                    if (Objects.equals(schema.getSchemaName(), schemaName)) {
                        for (final Entity entity : schema.getEntities()) {
                            if (Objects.equals(entity.getTableName(), tableName)) {
                                for (final Attribute attribute : entity.getAttributes()) {
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