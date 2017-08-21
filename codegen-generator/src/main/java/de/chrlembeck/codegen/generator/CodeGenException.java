package de.chrlembeck.codegen.generator;

/**
 * Oberklasse für alle im Laufe der Generierung auftretenden Exceptions. Dies können sowohl durch den Lexer erzeugte
 * Exceptions, so wie Exceptions während des Parsens oder der eigentlichen Code-Generierung sein.
 * 
 * @author Christoph Lembeck
 */
public abstract class CodeGenException extends RuntimeException {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 6666072716171288581L;

    /**
     * Erzeugt eine neue CodeGenException unter Angabe einer Fehlermeldung und einer Exception, die ursprünglich für den
     * Fehler verantwortlich ist.
     * 
     * @param message
     *            Fehlermeldung, die das Problem genauer beschreibt.
     * @param cause
     *            Ursprüngliche Exception, die zu diesem Fehler geführt hat.
     */
    public CodeGenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Erzeugt eine neue CodeGenException unter Angabe einer das Problem beschreibenden Fehlermeldung.
     * 
     * @param message
     *            Fehlermeldung, die das Problem genauer beschreibt.
     */
    public CodeGenException(final String message) {
        super(message);
    }

    /**
     * Gibt die Position innerhalb der Tamplate-Datei zurück, an der der Fehler aufgetreten ist.
     * 
     * @return Position in der Template-Datei, an der der Fehler aufgetreten ist.
     */
    public abstract Position getStartPosition();
}