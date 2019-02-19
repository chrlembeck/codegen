package de.chrlembeck.codegen.test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import de.chrlembeck.codegen.jdbc.JdbcTemplate;
import de.chrlembeck.codegen.model.Model;
import de.chrlembeck.codegen.model.impl.GenericDBModelReader;

public class OracleTest {

    public static void main(final String[] args) throws SQLException {

        // Oracle Views, Procedures, Indexes etc. auslesen:
        // https://blogs.oracle.com/mandalika/oracle-rdbms:-extracting-the-table,-index-view-definitions-ddl-and-indexed-columns

        final OracleTest test = new OracleTest();
        final DataSource dataSource = test.getDataSource();
        final JdbcTemplate template = new JdbcTemplate(dataSource);
        System.out.println(template.query("select current_timestamp from dual", (r, i) -> r.getTimestamp(1)));

        final ResultSet rs = dataSource.getConnection().getMetaData().getSchemas("OTBC_CALCSERVER_TEST_TRUNK", null);
        System.out.println("columnCount=" + rs.getMetaData().getColumnCount());
        System.out.println("columnName=" + rs.getMetaData().getColumnName(1));
        System.out.println("schemaName=" + rs.getMetaData().getSchemaName(1));
        System.out.println("catalogName=" + rs.getMetaData().getCatalogName(1));

        while (rs.next()) {
            System.out.println(rs.getString(1));// + ": " + rs.getString(1));
        }
        rs.close();

        final GenericDBModelReader modelReader = new GenericDBModelReader(dataSource);
        final Model model = modelReader.readModel("OTB");
        System.out.println(model);
    }

    public DataSource getDataSource() throws SQLException {
        final Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/oracle_dev.properties"));
        } catch (final IOException e) {
            throw new SQLException(e.getMessage(), e);
        }
/*
        final OracleDataSource ds = new OracleDataSource();
        ds.setDriverType("thin");
        ds.setServerName(props.getProperty("serverName"));
        ds.setDatabaseName(props.getProperty("databaseName"));
        ds.setPortNumber(Integer.parseInt(props.getProperty("portNumber")));
        ds.setUser(props.getProperty("userName"));
        ds.setPassword(props.getProperty("password"));
        return ds;
        */
        throw new RuntimeException("no datasource");
    }

    private java.sql.Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }
}
