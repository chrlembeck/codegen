package de.chrlembeck.codegen.generator;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Exception, die ein Problem beim Parsen einer Template-Datei beschreibt.
 * 
 * @author Christoph Lembeck
 */
public class ParserException extends CodeGenException {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -4227233427536403214L;

    /**
     * Position innerhalb der Template-Datei, an der das Problem aufgetreten ist.
     */
    private Position position;

    /**
     * Erzeugt eine neue Exception unter Angabe einer Fehlermeldung und des aktuellen Kontexts der ParserRule, an der
     * das Problem aufgetreten ist.
     * 
     * @param message
     *            Eine das Problem näher beschreibende Fehlermeldung.
     * @param context
     *            Kontext der ParserRule, an der das Problem aufgetreten ist.
     */
    public ParserException(final String message, final ParserRuleContext context) {
        super(message);
        this.position = new Position(context.getStart());
    }

    /**
     * Erzeugt eine neue Exception unter Angabe einer Fehlermeldung und des aktuellen Kontexts der ParserRule, an der
     * das Problem aufgetreten ist.
     * 
     * @param message
     *            Eine das Problem näher beschreibende Fehlermeldung.
     * @param context
     *            Kontext der ParserRule, an der das Problem aufgetreten ist.
     * @param cause
     *            Auslösender Grund der Exception.
     */
    public ParserException(final String message, final ParserRuleContext context, final Throwable cause) {
        super(message, cause);
        this.position = new Position(context.getStart());
    }

    /**
     * Erzeugt eine neue Exception unter Angabe einer das Problem beschreibenden Fehlermeldung und der Position
     * innerhalb der Template-Datei, an der das Problem aufgetreten ist.
     * 
     * @param message
     *            Eine das Problem näher beschreibende Fehlermeldung.
     * @param position
     *            Position innerhalb der Template-Datei, an der das Problem aufgetreten ist.
     */
    public ParserException(final String message, final Position position) {
        super(message);
        this.position = position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return super.getMessage() + "(" + getStartPosition().toString() + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Position getStartPosition() {
        return position;
    }
}