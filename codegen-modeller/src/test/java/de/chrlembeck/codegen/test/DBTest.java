package de.chrlembeck.codegen.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;


public class DBTest {

    public DataSource getDataSource() throws SQLException {
        final Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/dbconnection.properties"));
        } catch (final IOException e) {
            throw new SQLException(e.getMessage(), e);
        }

        /**
        final SQLServerDataSource ds = new SQLServerDataSource();
        ds.setUser(props.getProperty("userName"));
        ds.setPassword(props.getProperty("password"));
        ds.setServerName(props.getProperty("serverName"));
        ds.setInstanceName(props.getProperty("instanceName"));
        ds.setDatabaseName(props.getProperty("databaseName"));
        ds.setPortNumber(Integer.parseInt(props.getProperty("portNumber")));
        return ds;
         */
        throw new RuntimeException("no datasource");
    }

    private java.sql.Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /*
     * Display the driver properties, database details
     */

    public void displayDbProperties() {
        java.sql.DatabaseMetaData metaData = null;
        try (final Connection con = this.getConnection()) {
            if (con == null) {
                System.out.println("Error: No active Connection");
            } else {
                metaData = con.getMetaData();
                System.out.println("Driver Information");
                System.out.println("\tDriver Name: " + metaData.getDriverName());
                System.out.println("\tDriver Version: " + metaData.getDriverVersion());
                System.out.println("\nDatabase Information ");
                System.out.println("\tDatabase Name: " + metaData.getDatabaseProductName());
                System.out.println("\tDatabase Version: " + metaData.getDatabaseProductVersion());
                System.out.println("Avalilable Catalogs ");

                try (ResultSet resultSet = metaData.getCatalogs()) {
                    while (resultSet.next()) {
                        System.out.println("\tcatalog: " + resultSet.getString(1));
                    }
                    resultSet.close();
                }
                con.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        metaData = null;
    }

    public static void main(final String[] args) throws Exception {
        final DBTest myDbTest = new DBTest();
        myDbTest.displayDbProperties();

        final List<Catalog> catalogs = myDbTest.getCatalogs();

        for (final Catalog catalog : catalogs) {
            System.out.println(catalog.getName());
            for (final Schema schema : catalog.getSchemas()) {
                System.out.println("   " + schema.getName());
                for (final Table table : schema.getTables()) {
                    System.out.println("      " + table.getName() + " (" + table.getType() + ")");
                }
            }
        }

    }

    public List<Catalog> getCatalogs() throws SQLException {
        final Connection con = getConnection();
        ResultSet rs = con.getMetaData().getCatalogs();
        final List<Catalog> catalogs = new ArrayList<Catalog>();
        while (rs.next()) {
            final String catalogName = rs.getString(1);
            catalogs.add(new Catalog(catalogName));
        }
        rs.close();
        for (final Catalog catalog : catalogs) {
            rs = con.getMetaData().getSchemas(catalog.getName(), null);
            while (rs.next()) {
                final String schemaName = rs.getString(1);
                catalog.addSchema(new Schema(schemaName));
            }
            rs.close();
        }
        for (final Catalog catalog : catalogs) {
            for (final Schema schema : catalog.getSchemas()) {
                rs = con.getMetaData().getTables(catalog.getName(), schema.getName(), null, null);
                while (rs.next()) {
                    final String tableName = rs.getString(3);
                    final String tableType = rs.getString(4);
                    schema.addTable(new Table(tableName, tableType));
                }
            }
            rs.close();
        }
        return catalogs;
    }

    static class Catalog {

        private String name;

        private List<Schema> schemas = new ArrayList<Schema>();

        public Catalog(final String catalogName) {
            this.name = catalogName;
        }

        public String getName() {
            return name;
        }

        public void addSchema(final Schema schema) {
            schemas.add(schema);
        }

        public List<Schema> getSchemas() {
            return schemas;
        }
    }

    static class Schema {

        private String name;

        private List<Table> tables = new ArrayList<>();

        public Schema(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void addTable(final Table table) {
            tables.add(table);
        }

        public List<Table> getTables() {
            return tables;
        }
    }

    static class Table {

        private String name;

        private String type;

        public Table(final String name, final String type) {
            this.name = name;
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }
}