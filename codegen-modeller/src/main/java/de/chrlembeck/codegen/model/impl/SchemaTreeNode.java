package de.chrlembeck.codegen.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.Entity;
import de.chrlembeck.codegen.model.InfoTreeNode;
import de.chrlembeck.codegen.model.Schema;

public class SchemaTreeNode extends DefaultMutableTreeNode implements InfoTreeNode, Schema {

    private static final long serialVersionUID = 2711660189768047534L;

    private String schemaName;

    private String packageName;

    public SchemaTreeNode() {
        this(null);
    }

    SchemaTreeNode(final String schemaName) {
        setAllowsChildren(true);
        this.schemaName = schemaName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void setPackageName(final String packageName) {
        this.packageName = packageName;
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
        return schemaName;
    }

    @Override
    public String getSchemaName() {
        return schemaName;
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

    @Override
    public Iterable<Entity> getEntities() {
        final List<Entity> entities = new ArrayList<>();
        for (final Entity entity : tableTreeNodes()) {
            entities.add(entity);
        }
        return entities;
    }

    @Override
    public void setSchemaName(final String newSchemaName) {
        this.schemaName = newSchemaName;
    }

    @Override
    public Entity createEntity() {
        final TableTreeNode entity = new TableTreeNode();
        add(entity);
        return entity;
    }

    @Override
    public Catalog getCatalog() {
        return getParent();
    }
}