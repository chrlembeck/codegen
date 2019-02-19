package de.chrlembeck.codegen.model.impl;

import java.util.Objects;

import de.chrlembeck.codegen.model.Attribute;
import de.chrlembeck.codegen.model.ReferenceMapping;

public class ReferenceMappingImpl implements ReferenceMapping {

    private Attribute foreignKeyColumn;

    private Attribute primaryKeyColumn;

    ReferenceMappingImpl(final Attribute primaryKeyColumn, final Attribute foreignKeyColumn) {
        this.primaryKeyColumn = Objects.requireNonNull(primaryKeyColumn);
        this.foreignKeyColumn = Objects.requireNonNull(foreignKeyColumn);
    }

    @Override
    public Attribute getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    @Override
    public Attribute getForeignKeyColumn() {
        return foreignKeyColumn;
    }
}
