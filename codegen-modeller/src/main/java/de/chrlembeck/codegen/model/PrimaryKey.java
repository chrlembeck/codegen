package de.chrlembeck.codegen.model;

import java.util.List;

public interface PrimaryKey {

    public String getPrimaryKeyName();

    public List<Column> keys();

    public void setPrimaryKeyName(String primaryKeyName);

    public void setKeys(List<Column> keys);
}