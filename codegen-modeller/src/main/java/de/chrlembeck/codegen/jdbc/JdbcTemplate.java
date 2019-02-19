package de.chrlembeck.codegen.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class JdbcTemplate {

    private DataSource datasource;

    public JdbcTemplate(final DataSource datasource) {
        this.datasource = datasource;
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public int update(final String sql) throws JdbcException {
        try (Connection conn = datasource.getConnection(); Statement statement = conn.createStatement()) {
            return statement.executeUpdate(sql);
        } catch (final SQLException sqle) {
            throw new JdbcException(sqle);
        }
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) throws JdbcException {
        try (final Connection conn = datasource.getConnection();
                final Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                final T row = rowMapper.mapRow(resultSet, result.size());
                result.add(row);
            }
            return result;
        } catch (final SQLException sqle) {
            throw new JdbcException(sqle);
        }
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) throws JdbcException {
        try (final Connection conn = datasource.getConnection();
                final Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                final T result = rowMapper.mapRow(resultSet, 0);
                if (resultSet.next()) {
                    throw new JdbcException("ResultSet has more than one row.");
                } else {
                    return result;
                }
            } else {
                return null;
            }
        } catch (final SQLException sqle) {
            throw new JdbcException(sqle);
        }
    }
}