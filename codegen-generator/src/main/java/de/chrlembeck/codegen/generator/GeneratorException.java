package de.chrlembeck.codegen.generator;

import java.util.List;
import java.util.Objects;

import de.chrlembeck.codegen.generator.lang.AbstractTemplateMember;
import de.chrlembeck.codegen.generator.lang.Executable;

/**
 * Exception-Klasse für alle Probleme, die bei der Verarbeitung oder der Ausführung eines Templates auftreten können.
 *
 * @author Christoph Lembeck
 */
public class GeneratorException extends AbstractCodeGenException {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -4227233427536403214L;

    /**
     * Aktuelle Umgebung, in der das Problem bei der Ausführung aufgetreten ist.
     */
    private Environment environment;

    /**
     * Element innerhalb der Template-Datei, bei dem das Problem aufgetreten ist.
     */
    private AbstractTemplateMember<?> templateMember;

    /**
     * Erzeugt eine neue Exception aus der Fehlermeldung, dem problematischen Element der Template-Datei und der
     * Ausführungsumgebung.
     * 
     * @param message
     *            Fehlermeldung, die den Fehler oder die Art des Problem genauer beschreibt.
     * @param templateMember
     *            Element, bei dessen Ausführung das Problem aufgetreten ist.
     * @param environment
     *            Ausführungsumgebung zum Zeitpunkt des Auftretens des Problems.
     */
    public GeneratorException(final String message, final AbstractTemplateMember<?> templateMember,
            final Environment environment) {
        this(message, templateMember, environment, null);
    }

    /**
     * Erzeugt eine neue Exception aus der Fehlermeldung, dem problematischen Element der Template-Datei, der
     * Ausführungsumgebung und der Exception, die das Problem ursprünglich ausgelöst hat.
     * 
     * @param message
     *            Fehlermeldung, die den Fehler oder die Art des Problem genauer beschreibt.
     * @param templateMember
     *            Element, bei dessen Ausführung das Problem aufgetreten ist.
     * @param environment
     *            Ausführungsumgebung zum Zeitpunkt des Auftretens des Problems.
     * @param cause
     *            Ursprüngliche Exception, die zu diesem Problem geführt hat.
     */
    public GeneratorException(final String message, final AbstractTemplateMember<?> templateMember,
            final Environment environment, final Throwable cause) {
        super(message, cause);
        this.environment = Objects.requireNonNull(environment);
        this.templateMember = Objects.requireNonNull(templateMember);
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
        return templateMember.getStartPosition();
    }

    /**
     * Gibt den Stacktrace der Template-Ausführung zum Zeitpunkt des Auftreens des Problems aus.
     * 
     * @return Liste der verschachtelten Aufrufe von Template-Elementen, die letztendlich zu dem Problem geführt haben.
     *         Das erste Element der Liste ist das Element, bei dem die Ausführung begann. Das letzte Element der Liste
     *         ist das Element, dass dem Auftreten des Fehlers am nächsten ist.
     */
    public List<Executable<?>> getGeneratorStack() {
        return environment.getGeneratorStack();
    }

    /**
     * Gibt eine Text-Repräsentation des Aufruf-Stacks der verschachtelten Template-Aufrufe bis zum Auftreten des
     * Problems zurück.
     * 
     * @return Text-Repreäsentation des Aufruf-Stacks.
     */
    public String printGeneratorStack() {
        final StringBuilder builder = new StringBuilder();
        final List<Executable<?>> stack = getGeneratorStack();
        for (int i = stack.size() - 1; i >= 0; i--) {
            final Executable<?> executable = stack.get(i);
            builder.append(executable.getTemplateFile().getResourceIdentifier());
            builder.append(' ');
            builder.append(executable.getClass().getName());
            builder.append(' ');
            builder.append(executable.getStartPosition().toShortString());
        }
        return builder.toString();
    }
}