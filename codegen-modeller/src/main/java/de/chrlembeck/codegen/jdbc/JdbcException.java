package de.chrlembeck.codegen.jdbc;

public class JdbcException extends RuntimeException {

    private static final long serialVersionUID = 9085666105103055010L;

    public JdbcException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JdbcException(final Throwable cause) {
        super(cause);
    }

    public JdbcException(final String message) {
        super(message);
    }
}