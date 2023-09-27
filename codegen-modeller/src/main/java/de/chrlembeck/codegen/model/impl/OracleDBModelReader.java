package de.chrlembeck.codegen.model.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import de.chrlembeck.codegen.model.Column;
import de.chrlembeck.codegen.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.Table;

public class OracleDBModelReader extends GenericDBModelReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleDBModelReader.class);

    public OracleDBModelReader(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void readColumns(final Table table, final DatabaseMetaData metaData) throws SQLException {
        try (ResultSet rs = metaData.getColumns(table.getSchema().getCatalog().getCatalogName(),
                table.getSchema().getSchemaName(), table.getTableName(), null)) {
            while (rs.next()) {
                final Column column = new ColumnImpl();
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
                table.addColumn(column);
            }
        }
    }

    @Override
    protected void readCatalogs(final Model parent, final DatabaseMetaData metaData)
            throws SQLException {
        final Catalog oracleCatalog = new CatalogImpl();
        parent.addCatalog(oracleCatalog);
    }
}