package de.chrlembeck.codegen.generator.lang;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.Generator;
import de.chrlembeck.codegen.generator.GeneratorException;
import lang.CodeGenParser.ExecuteStatementContext;

/**
 * Execute Statements innerhalb von Template-Definitionen bewirken den Sprung in ein anderes auszuführenden Template.
 * Nach Ausführung des gewünschten Templates wird die Laufzeitmgebund wieder in den Zustand vor der Ausführung
 * zurückgesetzt und mit dem Statement nach diesem ExecuteStatement fortgesetzt.
 * 
 * @author Christoph Lembeck
 */
public class ExecuteStatement extends AbstractTemplateMember<ExecuteStatementContext>
        implements UserCodeOrStatements<ExecuteStatementContext> {

    /**
     * Der Logger für die Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteStatement.class);

    /**
     * Name des auszuführenden Templates.
     */
    private String templateName;

    /**
     * Expression, deren Auswertung an das auszuführende Template übergeben wird.
     */
    private Expression valueExpression;

    /**
     * Falls true, wird in der valueExpression eine Collection erwartet, deren Inhalte nacheinander an das auszuführende
     * Template übergeben werden. Bei false wird der Ausdruck aus der valueExpression direkt an das Template übergeben.
     */
    private boolean forEach;

    /**
     * Expression, deren Auswertung im Fall foreach=true zwischen die Auswertungen des Templates für die einzelnen
     * Elemente der valueExpression geschrieben werden soll.
     */
    private Expression separatorExpression;

    /**
     * Prefix zur Bestimmung der Template-Datei, aus der das auszuführende Template gelesen werden soll. Das Prefix muss
     * mit einem der Prefixe aus den Import-Statements der Template-Datei übereinstimmen.
     */
    private String prefix;

    /**
     * Erstellt das ExecuteStatement mit den übergebenen Daten.
     * 
     * @param ctx
     *            ANTLR-Kontext, wie er vom Parser beim Lesen des Dokumentes erzeugt wurde.
     * @param prefix
     *            Prefix zur Bestimmung der Template-Datei, aus der das auszuführende Template gelesen werden soll. Das
     *            Prefix muss mit einem der Prefixe aus den Import-Statements der Template-Datei übereinstimmen.
     * @param templateName
     *            Name des auszuführenden Templates.
     * @param valueExpression
     *            Expression, deren Auswertung an das auszuführende Template übergeben wird.
     * @param forEach
     *            Falls true, wird in der valueExpression eine Collection erwartet, deren Inhalte nacheinander an das
     *            auszuführende Template übergeben werden. Bei false wird der Ausdruck aus der valueExpression direkt an
     *            das Template übergeben.
     * @param separatorExpression
     *            Expression, deren Auswertung im Fall foreach=true zwischen die Auswertungen des Templates für die
     *            einzelnen Elemente der valueExpression geschrieben werden soll.
     */
    public ExecuteStatement(final ExecuteStatementContext ctx, final String prefix, final String templateName,
            final Expression valueExpression, final boolean forEach,
            final Expression separatorExpression) {
        super(ctx);
        this.prefix = prefix;
        this.templateName = templateName;
        this.valueExpression = valueExpression;
        this.forEach = forEach;
        this.separatorExpression = separatorExpression;
    }

    /**
     * Gibt die Expression, deren Auswertung im Fall foreach=true zwischen die Auswertungen des Templates für die
     * einzelnen Elemente der valueExpression geschrieben werden soll, zurück.
     * 
     * @return Expression, deren Auswertung im Fall foreach=true zwischen die Auswertungen des Templates für die
     *         einzelnen Elemente der valueExpression geschrieben werden soll.
     */
    public Expression getSeparatorExpression() {
        return separatorExpression;
    }

    /**
     * Gibt den Namen des auszuführenden Templates aus.
     * 
     * @return Name des auszuführenden Templates.
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Gibt die Expression, deren Auswertung an das auszuführende Template übergeben wird, zurück.
     * 
     * @return Expression, deren Auswertung an das auszuführende Template übergeben wird.
     */
    public Expression getValueExpression() {
        return valueExpression;
    }

    /**
     * Gibt den Prefix der auszufürenden Template-Datei zurück.
     * 
     * @return Prefix zur Bestimmung der Template-Datei, aus der das auszuführende Template gelesen werden soll. Das
     *         Prefix muss mit einem der Prefixe aus den Import-Statements der Template-Datei übereinstimmen.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Prüft, ob es sich um eine FOR oder eine FOREACH-Anweisung handelt.
     * 
     * @return Falls true, wird in der valueExpression eine Collection erwartet, deren Inhalte nacheinander an das
     *         auszuführende Template übergeben werden. Bei false wird der Ausdruck aus der valueExpression direkt an
     *         das Template übergeben.
     */
    public boolean isForEach() {
        return forEach;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ExecuteStatement[prefix=" + prefix + ", templateName=" + templateName + ", forEach=" + forEach
                + ", valueExpression=" + valueExpression + ", separatorExpression=" + separatorExpression + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Generator generator, final Object model, final Environment environment)
            throws IOException {
        URI resourceIdentifier;
        if (prefix == null) {
            resourceIdentifier = getTemplateFile().getResourceIdentifier();
        } else {
            try {
                resourceIdentifier = getTemplateFile().resolveImportPrefix(prefix);
            } catch (final URISyntaxException e) {
                throw new GeneratorException("Ungültige URI im Import-Statement.", this, environment, e);
            }
        }

        final Object source = valueExpression.evaluate(model, environment).getObject();
        if (forEach) {
            if (source instanceof Iterable) {
                final Iterable<?> iterable = (Iterable<?>) source;
                final Iterator<?> iterator = iterable.iterator();
                while (iterator.hasNext()) {
                    final Object item = iterator.next();
                    generator.generate(resourceIdentifier, templateName, item);
                    if (iterator.hasNext() && separatorExpression != null) {
                        final Writer writer = generator.getCurrentWriter();
                        if (writer == null) {
                            LOGGER.info("Kein Writer zur Ausgabe gefunden. " + this + ": " + this.getContext());
                            throw new GeneratorException("Kein Writer zur Ausgabe gefunden.", this, environment);
                        } else {
                            writer.write(String.valueOf(separatorExpression.evaluate(model, environment).getObject()));
                        }
                    }
                }
            } else {
                throw new RuntimeException("don't know what to do with collection " + model.getClass().getName());
            }
        } else {
            generator.generate(resourceIdentifier, templateName, source);
        }
    }
}