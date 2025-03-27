package de.chrlembeck.codegen.model.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import de.chrlembeck.codegen.model.Column;
import de.chrlembeck.codegen.model.InfoTreeNode;
import java.io.Serial;

public class ColumnTreeNode extends DefaultMutableTreeNode implements InfoTreeNode {

    @Serial
    private static final long serialVersionUID = 6809346090633542299L;

    private Column column;

    ColumnTreeNode(Column column) {
        this.column = column;
        setAllowsChildren(false);
    }

    public Column getColumn() {
        return column;
    }

    @Override
    public TableTreeNode getParent() {
        return (TableTreeNode) super.getParent();
    }

    @Override
    public String toString() {
        return column.getColumnName();
    }

    @Override
    public String getInfoText() {
        final StringBuilder sb = new StringBuilder();
        sb.append("columName= " + column.getColumnName() + "\n");
        sb.append("columnDef= " + column.getColumnDef() + "\n");
        sb.append("columnSize= " + column.getColumnSize() + "\n");
        sb.append("charOctetLength= " + column.getCharOctetLength() + "\n");
        sb.append("dataType= " + column.getDataType() + "\n");
        sb.append("decimalDigits= " + column.getDecimalDigits() + "\n");

        return sb.toString();
    }
}