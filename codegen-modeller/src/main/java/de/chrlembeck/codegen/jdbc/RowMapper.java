package de.chrlembeck.codegen.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {

    T mapRow(ResultSet resultSet, int rowIndex) throws SQLException;
}