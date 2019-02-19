package de.chrlembeck.codegen.test;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import de.chrlembeck.codegen.diagram.DiagramCreator;
import de.chrlembeck.codegen.model.Model;
import de.chrlembeck.codegen.model.impl.OracleDBModelReader;

public class ReadOracleTableModelTest {

    public static void main(final String[] args) throws Exception {
        final ReadOracleTableModelTest test = new ReadOracleTableModelTest();
        final DataSource dataSource = test.getDataSource();
        final OracleDBModelReader reader = new OracleDBModelReader(dataSource);
        // final Model model = reader.readModel("LEC");
        final Model model = reader.readModel("RT_CALCSERVER_TEST_TRUNK");

        System.out.println(model);

        final DiagramCreator dc = new DiagramCreator();
        final StringWriter writer = new StringWriter();
        dc.createGraphvizDiagram(model, writer);
        System.out.println(writer.toString());
    }

    public DataSource getDataSource() throws SQLException {
        final Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/oracle_local.properties"));
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
}