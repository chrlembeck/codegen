package de.chrlembeck.codegen.model.gui;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.treetable.TreeTableNode;

import de.chrlembeck.util.collections.ArrayEnumeration;

public class ModelTreeTableNode implements TreeTableNode {

    private ModelTreeNodeUtil helper;

    private TreeTableNode[] children;

    private String name;

    private TreeTableNode parent;

    private Class<?> type;

    private Object userObject;

    public ModelTreeTableNode(final ModelTreeTableNode parent, final String name, final Class<?> type,
            final Object userObject) {
        this.parent = parent;
        this.helper = parent == null ? null : parent.helper;
        this.name = name;
        this.type = type;
        this.userObject = userObject;
    }

    public void setHelper(final ModelTreeNodeUtil helper) {
        this.helper = helper;
    }

    @Override
    public Enumeration<? extends TreeTableNode> children() {
        lazyInit();
        return new ArrayEnumeration<>(children);
    }

    @Override
    public boolean getAllowsChildren() {
        return helper.allowsChildren(userObject);
    }

    @Override
    public TreeTableNode getChildAt(final int childIndex) {
        lazyInit();
        return children[childIndex];
    }

    @Override
    public int getChildCount() {
        lazyInit();
        return children.length;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getIndex(final TreeNode node) {
        lazyInit();
        for (int i = 0; i < children.length; i++) {
            if (children[i] == node) {
                return i;
            }
        }
        return -1;
    }

    public String getName() {
        return name;
    }

    @Override
    public TreeTableNode getParent() {
        return parent;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public Object getUserObject() {
        return userObject;
    }

    @Override
    public Object getValueAt(final int column) {
        switch (column) {
            case 0:
                return name;
            case 1:
                return helper.getVisibleTypeName(type, userObject == null ? null : userObject.getClass());
            case 2:
                return helper.toVisibleString(userObject);
            default:
                return null;
        }
    }

    @Override
    public boolean isEditable(final int column) {
        return false;
    }

    @Override
    public boolean isLeaf() {
        lazyInit();
        return children.length == 0;
    }

    private void lazyInit() {
        if (children == null) {
            children = helper.getChildren(this);
        }
    }

    @Override
    public void setUserObject(final Object userObject) {
        this.userObject = userObject;
    }

    @Override
    public void setValueAt(final Object aValue, final int column) {
    }
}