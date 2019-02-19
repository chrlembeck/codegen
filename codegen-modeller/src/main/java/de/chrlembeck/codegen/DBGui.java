package de.chrlembeck.codegen;

import java.awt.BorderLayout;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import org.apache.derby.jdbc.EmbeddedDataSource;
import de.chrlembeck.codegen.model.InfoTreeNode;
import de.chrlembeck.codegen.model.impl.DBRootTreeNode;
import de.chrlembeck.codegen.model.impl.GenericDBModelReader;

public class DBGui extends JFrame {

    private static final long serialVersionUID = -847306738692040261L;

    private JTextArea taInfo;

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(DBGui::new);
    }

    public DBGui() {
        super("DBGui");
        setLayout(new BorderLayout());
        try {
            final DBRootTreeNode model = (DBRootTreeNode) new GenericDBModelReader(getDataSource()).readModel(null);
            final JTree tree = new JTree(new DefaultTreeModel(model));
            final JScrollPane spTree = new JScrollPane(tree);
            taInfo = new JTextArea(2, 80);
            final JScrollPane spMain = new JScrollPane(taInfo);
            final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spTree, spMain);
            spMain.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            splitPane.setDividerLocation(200);
            add(splitPane, BorderLayout.CENTER);

            taInfo.setEditable(false);
            tree.addTreeSelectionListener(this::treeSelectionChanged);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            pack();
            setVisible(true);
        } catch (final SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void treeSelectionChanged(final TreeSelectionEvent event) {
        final InfoTreeNode currentNode = (InfoTreeNode) event.getPath().getLastPathComponent();
        taInfo.setText(currentNode.getInfoText());

    }

    public final DataSource getDataSource() throws SQLException {
        final Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/dbconnection.properties"));
        } catch (final IOException e) {
            throw new SQLException(e.getMessage(), e);
        }
        /*
        final SQLServerDataSource ds = new SQLServerDataSource();

        ds.setUser(props.getProperty("userName"));
        ds.setPassword(props.getProperty("password"));
        ds.setServerName(props.getProperty("serverName"));
        ds.setInstanceName(props.getProperty("instanceName"));
        ds.setDatabaseName(props.getProperty("databaseName"));
        ds.setPortNumber(Integer.parseInt(props.getProperty("portNumber")));

        final EmbeddedDataSource ds2 = new EmbeddedDataSource();
        ds2.setDatabaseName("testDB");
        ds2.setCreateDatabase("create");
        ds2.setUser("testuser");
        ExampleDataModelCreator.createModel(ds2);

        return ds2;*/
        return null; // TODO über User Settings realisieren
    }

}