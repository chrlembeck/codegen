package de.chrlembeck.codegen.test;

import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.derby.jdbc.EmbeddedDataSource;

public class DerbyTest {

    public static void main(final String[] args) throws Exception {
        final EmbeddedDataSource dataSource = new EmbeddedDataSource();
        dataSource.setDatabaseName("testDB");
        dataSource.setCreateDatabase("create");
        dataSource.setUser("testuser");

        final Connection con = dataSource.getConnection();
        con.createStatement()
                .executeUpdate("CREATE TABLE test (id int not null primary key, name varchar(20) not null)");

        final ResultSet rs = con.createStatement().executeQuery("SELECT * FROM test");

        System.out.println(rs.getMetaData().getColumnName(1));

    }
}
