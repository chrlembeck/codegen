package de.chrlembeck.codegen.model.impl;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.InfoTreeNode;

public class CatalogTreeNode extends DefaultMutableTreeNode implements InfoTreeNode {

    @Serial
    private static final long serialVersionUID = -7250955422184293044L;

    private Catalog catalog;

    CatalogTreeNode(Catalog catalog) {
        setAllowsChildren(true);
        this.catalog = catalog;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    @Override
    public void add(final MutableTreeNode newChild) {
        if (!(newChild instanceof SchemaTreeNode)) {
            throw new IllegalArgumentException("CatalogTreeNodes can only contain SchemaTreeNodes.");
        }
        super.add(newChild);
    }

    @Override
    public String toString() {
        return getCatalog().getCatalogName();
    }

    @Override
    public String getInfoText() {
        return toString();
    }

    @Override
    public DBRootTreeNode getParent() {
        return (DBRootTreeNode) super.getParent();
    }

    public Iterable<SchemaTreeNode> schemaTreeNodes() {
        final List<SchemaTreeNode> schemaTreeNodes = new ArrayList<>(getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            schemaTreeNodes.add((SchemaTreeNode) getChildAt(i));
        }
        return schemaTreeNodes;
    }
}