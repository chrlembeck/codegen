package de.chrlembeck.codegen.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.chrlembeck.codegen.model.Attribute;
import de.chrlembeck.codegen.model.Entity;
import de.chrlembeck.codegen.model.InfoTreeNode;
import de.chrlembeck.codegen.model.PrimaryKey;
import de.chrlembeck.codegen.model.Reference;
import de.chrlembeck.codegen.model.Schema;

public class TableTreeNode extends DefaultMutableTreeNode implements InfoTreeNode, Entity {

    private static final long serialVersionUID = 2491493671980654918L;

    private String tableName;

    private String tableType;

    private String javaName;

    private PrimaryKey primaryKey;

    private List<Reference> references = new ArrayList<>(1);

    public TableTreeNode() {
        this(null, null);
    }

    TableTreeNode(final String tableName,
            final String tableType) {
        setAllowsChildren(true);
        this.tableName = tableName;
        this.tableType = tableType;
    }

    @Override
    public void add(final MutableTreeNode newChild) {
        if (!(newChild instanceof ColumnTreeNode)) {
            throw new IllegalArgumentException("TableTreeNodes can only contain ColumnTreeNodes.");
        }
        super.add(newChild);
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    public String getTableType() {
        return tableType;
    }

    @Override
    public SchemaTreeNode getParent() {
        return (SchemaTreeNode) super.getParent();
    }

    @Override
    public String toString() {
        return tableName + " (" + tableType + ")";
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
            sb.append(getChildAt(i).getColumnName());
            if (i < getChildCount() - 1) {
                sb.append(", ");
            }
        }
        sb.append(" FROM ");
        sb.append(getTableName());
    }

    @Override
    public Schema getSchema() {
        return getParent();
    }

    public List<ColumnTreeNode> columnTreeNodes() {
        final List<ColumnTreeNode> columnTreeNodes = new ArrayList<>(getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            columnTreeNodes.add(getChildAt(i));
        }
        return columnTreeNodes;
    }

    @Override
    public String getJavaName() {
        return javaName;
    }

    @Override
    public void setJavaName(final String javaName) {
        this.javaName = javaName;
    }

    @Override
    public Attribute createAttribute() {
        final ColumnTreeNode attribute = new ColumnTreeNode();
        add(attribute);
        return attribute;
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        return new ArrayList<>(columnTreeNodes());
    }

    @Override
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void setTableType(final String tableType) {
        this.tableType = tableType;
    }

    @Override
    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    @Override
    public void setPrimaryKey(final PrimaryKey key) {
        this.primaryKey = key;
    }

    @Override
    public Attribute getAttributeByColumnName(final String columnName) {
        return columnTreeNodes().stream().filter(c -> c.getColumnName().equals(columnName)).findAny().orElse(null);
    }

    @Override
    public int getAttributeCount() {
        return columnTreeNodes().size();
    }

    @Override
    public Reference createReference(final String foreignKeyName, final Attribute[] attributes,
            final Attribute[] foreignKeyAttributes) {
        Objects.requireNonNull(attributes);
        Objects.requireNonNull(foreignKeyAttributes);
        if (attributes.length != foreignKeyAttributes.length) {
            throw new IllegalArgumentException();
        }
        for (final Attribute attribute : attributes) {
            if (attribute.getEntity() != this) {
                throw new IllegalArgumentException();
            }
        }
        final Entity foreignKeyTable = foreignKeyAttributes[0].getEntity();
        for (final Attribute attribute : foreignKeyAttributes) {
            if (attribute.getEntity() != foreignKeyTable) {
                throw new IllegalArgumentException();
            }
        }
        final ReferenceImpl reference = new ReferenceImpl(this, attributes, foreignKeyTable, foreignKeyAttributes,
                foreignKeyName);

        references.add(reference);
        return reference;
    }

    @Override
    public Iterable<Reference> getReferences() {
        return references;
    }
}