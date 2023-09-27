package de.chrlembeck.codegen.model.impl;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.chrlembeck.codegen.model.Column;
import de.chrlembeck.codegen.model.Table;
import de.chrlembeck.codegen.model.InfoTreeNode;
import de.chrlembeck.codegen.model.PrimaryKey;
import de.chrlembeck.codegen.model.Reference;
import de.chrlembeck.codegen.model.Schema;

public class TableTreeNode extends DefaultMutableTreeNode implements InfoTreeNode {

    @Serial
    private static final long serialVersionUID = 2491493671980654918L;

    private Table table;

    TableTreeNode(Table table) {
        setAllowsChildren(true);
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    @Override
    public void add(final MutableTreeNode newChild) {
        if (!(newChild instanceof ColumnTreeNode)) {
            throw new IllegalArgumentException("TableTreeNodes can only contain ColumnTreeNodes.");
        }
        super.add(newChild);
    }

    @Override
    public SchemaTreeNode getParent() {
        return (SchemaTreeNode) super.getParent();
    }

    @Override
    public String toString() {
        return getTable().getTableName() + " (" + getTable().getTableType() + ")";
    }

    @Override
    public String getInfoText() {
        final StringBuilder sb = new StringBuilder();
        createSelectAll(sb);
        sb.append('\n');
        return sb.toString();
    }

    @Override
    public ColumnTreeNode getChildAt(final int index) {
        return (ColumnTreeNode) super.getChildAt(index);
    }

    private void createSelectAll(final StringBuilder sb) {
        sb.append("SELECT ");
        for (int i = 0; i < getChildCount(); i++) {
            sb.append(getChildAt(i).getColumn().getColumnName());
            if (i < getChildCount() - 1) {
                sb.append(", ");
            }
        }
        sb.append(" FROM ");
        sb.append(getTable().getTableName());
    }

    public List<ColumnTreeNode> columnTreeNodes() {
        final List<ColumnTreeNode> columnTreeNodes = new ArrayList<>(getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            columnTreeNodes.add(getChildAt(i));
        }
        return columnTreeNodes;
    }
}