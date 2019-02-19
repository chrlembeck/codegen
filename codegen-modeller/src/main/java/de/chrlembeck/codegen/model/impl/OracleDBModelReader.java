package de.chrlembeck.codegen.model.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.model.Attribute;
import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.Entity;

public class OracleDBModelReader extends GenericDBModelReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleDBModelReader.class);

    public OracleDBModelReader(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
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
            }
        }
    }

    @Override
    protected void readCatalogs(final DBRootTreeNode parent, final DatabaseMetaData metaData)
            throws SQLException {
        final Catalog oracleCatalog = parent.createCatalog();
    }
}