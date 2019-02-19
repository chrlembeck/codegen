package de.chrlembeck.codegen.model.impl;

import java.util.ArrayList;
import java.util.List;

import de.chrlembeck.codegen.model.Attribute;
import de.chrlembeck.codegen.model.Entity;
import de.chrlembeck.codegen.model.Reference;
import de.chrlembeck.codegen.model.ReferenceMapping;

public class ReferenceImpl implements Reference {

    private Entity primaryKeyTable;

    private Entity foreignKeyTable;

    private List<ReferenceMapping> referenceMappings = new ArrayList<>(1);

    private String foreignKeyName;

    ReferenceImpl(final Entity primaryKeyTable, final Attribute[] attributes, final Entity foreignKeyTable,
            final Attribute[] foreignKeyAttributes, final String foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
        this.primaryKeyTable = primaryKeyTable;
        this.foreignKeyTable = foreignKeyTable;
        for (int i = 0; i < attributes.length; i++) {
            final Attribute primaryKeyAttribute = attributes[i];
            final Attribute foreignKeyAttribute = foreignKeyAttributes[i];
            referenceMappings.add(new ReferenceMappingImpl(primaryKeyAttribute, foreignKeyAttribute));
        }
    }

    @Override
    public Entity getPrimaryKeyTable() {
        return primaryKeyTable;
    }

    @Override
    public Entity getForeignKeyTable() {
        return foreignKeyTable;
    }

    @Override
    public List<ReferenceMapping> getReferenceMappings() {
        return referenceMappings;
    }

    @Override
    public String getForeignKeyName() {
        return foreignKeyName;
    }
}