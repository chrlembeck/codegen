package de.chrlembeck.codegen.model.impl;

import java.util.Objects;

import de.chrlembeck.codegen.model.Column;
import de.chrlembeck.codegen.model.ReferenceMapping;

public class ReferenceMappingImpl implements ReferenceMapping {

    private Column foreignKeyColumn;

    private Column primaryKeyColumn;

    ReferenceMappingImpl(final Column primaryKeyColumn, final Column foreignKeyColumn) {
        this.primaryKeyColumn = Objects.requireNonNull(primaryKeyColumn);
        this.foreignKeyColumn = Objects.requireNonNull(foreignKeyColumn);
    }

    @Override
    public Column getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    @Override
    public Column getForeignKeyColumn() {
        return foreignKeyColumn;
    }
}
