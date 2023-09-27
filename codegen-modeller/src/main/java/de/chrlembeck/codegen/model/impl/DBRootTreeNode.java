package de.chrlembeck.codegen.model.impl;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import de.chrlembeck.codegen.model.InfoTreeNode;
import de.chrlembeck.codegen.model.Model;

public class DBRootTreeNode extends DefaultMutableTreeNode implements InfoTreeNode {

    @Serial
    private static final long serialVersionUID = 1889553261156392830L;

    public DBRootTreeNode(Model model) {
        setAllowsChildren(true);
        setUserObject(model);
    }

    public Model getModel() {
        return (Model)getUserObject();
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
}