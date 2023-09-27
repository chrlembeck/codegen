package de.chrlembeck.codegen.model.impl;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.Table;
import de.chrlembeck.codegen.model.InfoTreeNode;
import de.chrlembeck.codegen.model.Schema;

public class SchemaTreeNode extends DefaultMutableTreeNode implements InfoTreeNode {

    @Serial
    private static final long serialVersionUID = 2711660189768047534L;

    private final Schema schema;

    SchemaTreeNode(Schema schema) {
        setAllowsChildren(true);
        this.schema = schema;
    }

    public Schema getSchema() {
        return schema;
    }

    @Override
    public CatalogTreeNode getParent() {
        return (CatalogTreeNode) super.getParent();
    }

    @Override
    public void add(final MutableTreeNode newChild) {
        if (!(newChild instanceof TableTreeNode)) {
            throw new IllegalArgumentException("SchemaTreeNodes can only contain TableTreeNodes.");
        }
        super.add(newChild);
    }

    public void add(final CatalogTreeNode newChild) {
        super.add(newChild);
    }

    @Override
    public String toString() {
        return schema.getSchemaName();
    }

    @Override
    public String getInfoText() {
        return toString();
    }

    public Iterable<TableTreeNode> tableTreeNodes() {
        final List<TableTreeNode> tableTreeNodes = new ArrayList<>(getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            tableTreeNodes.add((TableTreeNode) getChildAt(i));
        }
        return tableTreeNodes;
    }
}