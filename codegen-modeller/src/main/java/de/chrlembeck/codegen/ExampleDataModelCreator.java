package de.chrlembeck.codegen;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import de.chrlembeck.codegen.jdbc.JdbcTemplate;
import de.chrlembeck.codegen.jdbc.RowMapper;

public class ExampleDataModelCreator {

    public static void createModel(final DataSource datasource) {
        final JdbcTemplate jdbc = new JdbcTemplate(datasource);

        if (tableExists(jdbc, "SCHEMA1", "PERSON")) {
            jdbc.update("DROP TABLE schema1.person");
        }
        if (tableExists(jdbc, "SCHEMA2", "TEST")) {
            jdbc.update("DROP TABLE schema2.test");
        }

        if (schemaExists(jdbc, "SCHEMA1")) {
            jdbc.update("DROP SCHEMA schema1 RESTRICT");
        }
        if (schemaExists(jdbc, "SCHEMA2")) {
            jdbc.update("DROP SCHEMA schema2 RESTRICT");
        }
        jdbc.update("CREATE SCHEMA schema1");
        jdbc.update("CREATE SCHEMA schema2");
        jdbc.update("CREATE TABLE schema1.Person(personID int not null primary key, name varchar(32) not null)");
        jdbc.update("CREATE TABLE schema2.Test(testID int not null primary key, name varchar(32) not null)");

        try {
            final ResultSet rs = datasource.getConnection().getMetaData().getSchemas();
            while (rs.next()) {
                System.out.println(rs.getObject(1) + " " + rs.getObject(2));
            }
        } catch (final Exception e) {

        }

    }

    static class BooleanMapper implements RowMapper<Boolean> {

        @Override
        public Boolean mapRow(final ResultSet resultSet, final int rowIndex) throws SQLException {
            return resultSet.getBoolean(1);
        }
    }

    public static boolean schemaExists(final JdbcTemplate jdbc, final String schemaname) {
        return jdbc.queryForObject(
                "select exists(SELECT 1 from sys.sysschemas WHERE schemaname='" + schemaname
                        + "') FROM SYSIBM.SYSDUMMY1",
                new BooleanMapper()).booleanValue();
    }

    private static boolean tableExists(final JdbcTemplate jdbc, final String schemaName, final String tableName) {
        return jdbc.queryForObject(
                "select exists(SELECT 1 from sys.systables t, sys.sysschemas s WHERE s.schemaid = t.schemaid AND s.schemaname='"
                        + schemaName + "' AND t.tablename='" + tableName + "') FROM SYSIBM.SYSDUMMY1",
                new BooleanMapper()).booleanValue();
    }
}
