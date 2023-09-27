package de.chrlembeck.codegen.model.impl;

import java.util.List;

import de.chrlembeck.codegen.model.Column;
import de.chrlembeck.codegen.model.PrimaryKey;

public class PrimaryKeyImpl implements PrimaryKey {

    private String primaryKeyName;

    private List<Column> keys;

    PrimaryKeyImpl() {
    }

    @Override
    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    @Override
    public List<Column> keys() {
        return keys;
    }

    @Override
    public void setPrimaryKeyName(final String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }

    @Override
    public void setKeys(final List<Column> keys) {
        this.keys = keys;
    }

}
