package de.chrlembeck.codegen.gui.datamodel;

import java.awt.BorderLayout;
import java.awt.Point;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

public class TreeTableTest extends JFrame {

    private static final long serialVersionUID = -6241646671051893240L;

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(TreeTableTest::new);
    }

    public TreeTableTest() {
        super("TreeTableTest");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final DefaultTreeTableModel model = new DefaultTreeTableModel();
        model.setColumnIdentifiers(Arrays.asList("Name", "Typ", "Inhalt"));
        final Object dataModel = createModel();
        final TreeTableNode root = new ModelTreeNodeUtil().createRootNode("model", dataModel);
        model.setRoot(root);

        final JXTreeTable treeTable = new JXTreeTable(model);
        treeTable.setRootVisible(true);

        final JScrollPane scrollPane = new JScrollPane(treeTable);
        add(scrollPane, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    public Object createModel() {
        return Arrays.asList("Hallo Welt", Integer.valueOf(3), new float[] { 3.14f, 2.79f, -34f },
                Arrays.asList("one", "two"), new Point(3, 4), new int[][] { { 1, 2 }, { 3, 4 } });
    }
}