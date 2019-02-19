package de.chrlembeck.codegen.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.InfoTreeNode;
import de.chrlembeck.codegen.model.Model;
import de.chrlembeck.codegen.model.Schema;

public class DBRootTreeNode extends DefaultMutableTreeNode implements InfoTreeNode, Model {

    private static final long serialVersionUID = 1889553261156392830L;

    public DBRootTreeNode() {
        setAllowsChildren(true);
    }

    @Override
    public String toString() {
        return "root";
    }

    @Override
    public String getInfoText() {
        return toString();
    }

    @Override
    public void add(final MutableTreeNode newChild) {
        if (!(newChild instanceof CatalogTreeNode)) {
            throw new IllegalArgumentException("DBRootTreeNodes can only contain CatalogTreeNodes.");
        }
        super.add(newChild);
    }

    public Collection<CatalogTreeNode> catalogTreeNodes() {
        final List<CatalogTreeNode> catalogTreeNodes = new ArrayList<>(getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            catalogTreeNodes.add((CatalogTreeNode) getChildAt(i));
        }
        return catalogTreeNodes;
    }

    @Override
    public Iterable<Catalog> getCatalogs() {
        return new ArrayList<Catalog>(catalogTreeNodes());
    }

    public Iterable<Schema> getSchemas() {
        final List<Schema> schemas = new ArrayList<>();
        for (final CatalogTreeNode catalog : catalogTreeNodes()) {
            for (final Schema schema : catalog.getSchemas()) {
                schemas.add(schema);
            }
        }
        return schemas;
    }

    public void addSchema(final SchemaTreeNode newChild) {
        super.add(newChild);
    }

    public CatalogTreeNode createCatalog() {
        final CatalogTreeNode catalog = new CatalogTreeNode();
        add(catalog);
        return catalog;
    }
}