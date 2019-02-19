package de.chrlembeck.codegen.model.impl;

import java.util.List;

import de.chrlembeck.codegen.model.Attribute;
import de.chrlembeck.codegen.model.PrimaryKey;

public class PrimaryKeyImpl implements PrimaryKey {

    private String primaryKeyName;

    private List<Attribute> keys;

    PrimaryKeyImpl() {
    }

    @Override
    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    @Override
    public List<Attribute> keys() {
        return keys;
    }

    @Override
    public void setPrimaryKeyName(final String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }

    @Override
    public void setKeys(final List<Attribute> keys) {
        this.keys = keys;
    }

}
