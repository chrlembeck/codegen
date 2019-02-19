package de.chrlembeck.codegen.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.InfoTreeNode;
import de.chrlembeck.codegen.model.Schema;

public class CatalogTreeNode extends DefaultMutableTreeNode implements InfoTreeNode, Catalog {

    private static final long serialVersionUID = -7250955422184293044L;

    private String catalogName;

    CatalogTreeNode() {
        this(null);
    }

    CatalogTreeNode(final String catalogName) {
        setAllowsChildren(true);
        this.catalogName = catalogName;
    }

    @Override
    public String getCatalogName() {
        return catalogName;
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
        return catalogName;
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

    @Override
    public Iterable<Schema> getSchemas() {
        final List<Schema> schemas = new ArrayList<>();
        for (final Schema schema : schemaTreeNodes()) {
            schemas.add(schema);
        }
        return schemas;
    }

    @Override
    public void setCatalogName(final String newCatalogName) {
        this.catalogName = newCatalogName;
    }

    @Override
    public Schema createSchema() {
        final SchemaTreeNode schema = new SchemaTreeNode();
        add(schema);
        return schema;
    }
}