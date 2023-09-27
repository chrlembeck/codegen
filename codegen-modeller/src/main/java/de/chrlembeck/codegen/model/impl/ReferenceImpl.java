package de.chrlembeck.codegen.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.chrlembeck.codegen.model.Column;
import de.chrlembeck.codegen.model.Table;
import de.chrlembeck.codegen.model.Reference;
import de.chrlembeck.codegen.model.ReferenceMapping;

public class ReferenceImpl implements Reference {

    private Table primaryKeyTable;

    private Table foreignKeyTable;

    private List<ReferenceMapping> referenceMappings = new ArrayList<>(1);

    private String foreignKeyName;

    ReferenceImpl(final Table primaryKeyTable, final Column[] attributes, final Table foreignKeyTable,
            final Column[] foreignKeyAttributes, final String foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
        this.primaryKeyTable = primaryKeyTable;
        this.foreignKeyTable = foreignKeyTable;
        for (int i = 0; i < attributes.length; i++) {
            final Column primaryKeyAttribute = attributes[i];
            final Column foreignKeyAttribute = foreignKeyAttributes[i];
            referenceMappings.add(new ReferenceMappingImpl(primaryKeyAttribute, foreignKeyAttribute));
        }
    }

    @Override
    public Table getPrimaryKeyTable() {
        return primaryKeyTable;
    }

    @Override
    public Table getForeignKeyTable() {
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


    public static Reference createReference(final String foreignKeyName, final Column[] primaryKeyColumns,
            final Column[] foreignKeyColumns) {
        Objects.requireNonNull(primaryKeyColumns);
        Objects.requireNonNull(foreignKeyColumns);
        if (primaryKeyColumns.length != foreignKeyColumns.length) {
            throw new IllegalArgumentException();
        }
        final Table primaryKeyTable = primaryKeyColumns[0].getTable();
        for (final Column column : primaryKeyColumns) {
            if (column.getTable() != primaryKeyTable) {
                throw new IllegalArgumentException();
            }
        }
        final Table foreignKeyTable = foreignKeyColumns[0].getTable();
        for (final Column column : foreignKeyColumns) {
            if (column.getTable() != foreignKeyTable) {
                throw new IllegalArgumentException();
            }
        }
        final ReferenceImpl reference = new ReferenceImpl(primaryKeyTable, primaryKeyColumns, foreignKeyTable, foreignKeyColumns,
                foreignKeyName);
        return reference;
    }
}