package de.chrlembeck.codegen.generator.lang;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.Generator;
import de.chrlembeck.codegen.generator.GeneratorException;
import lang.CodeGenParser.ForStatementContext;

/**
 * For-Statement einer Template-Datei. For Statements können für das Durchlaufen von Collections oder Arrays und die für
 * jeden enthaltene Element wiederholte Verarbeitung der in dem Statement enthaltenen Codeblöcke und Statements
 * verwendet werden.
 * 
 * @author Christoph Lembeck
 */
public class ForStatement extends AbstractTemplateMember<ForStatementContext>
        implements UserCodeOrStatements<ForStatementContext> {

    /**
     * Der Logger für die Klasse.
     */
    private static Logger LOGGER = LoggerFactory.getLogger(ForStatement.class);

    /**
     * Name der im Code-Block sictbaren Variable, aus der das aktuelle Element des Schleifendurchlaufs ausgelesen werden
     * kann.
     */
    private String varName;

    /**
     * Expression, aus der die Collection ausgewertet werden kann, über die die Schleife iterieren soll.
     */
    private Expression collectionExpression;

    /**
     * Name der im Code-Block sichtbaren Variable, aus der zusatzinformationen über den Schleifendurchlauf ausgelesen
     * werden können.
     */
    private String counterName;

    /**
     * Expression, deren Auswertung zwischen die Ergebnisse der einzelnen Schleifendurchläufe kopiert wird.
     */
    private Expression separatorExpression;

    /**
     * Schleifenrumpf, der den Inhalt eines jeden Schleifendurchlaufs generiert.
     */
    private List<UserCodeOrStatements<?>> loopBody;

    /**
     * Erstellt das ForStatement mit den übergebenen Daten.
     * 
     * @param ctx
     *            ANTLR-Kontext, wie er vom Parser beim Lesen des Dokumentes erzeugt wurde.
     * @param varName
     *            Name der im Code-Block sictbaren Variable, aus der das aktuelle Element des Schleifendurchlaufs
     *            ausgelesen werden kann.
     * @param collectionExpression
     *            Expression, aus der die Collection ausgewertet werden kann, über die die Schleife iterieren soll.
     * @param counterName
     *            Name der im Code-Block sichtbaren Variable, aus der zusatzinformationen über den Schleifendurchlauf
     *            ausgelesen werden können.
     * @param separatorExpression
     *            Expression, deren Auswertung zwischen die Ergebnisse der einzelnen Schleifendurchläufe kopiert wird.
     * @param loopBody
     *            Schleifenrumpf, der den Inhalt eines jeden Schleifendurchlaufs generiert.
     */
    public ForStatement(final ForStatementContext ctx, final String varName, final Expression collectionExpression,
            final String counterName, final Expression separatorExpression,
            final List<UserCodeOrStatements<?>> loopBody) {
        super(ctx);
        this.varName = varName;
        this.collectionExpression = collectionExpression;
        this.counterName = counterName;
        this.separatorExpression = separatorExpression;
        this.loopBody = loopBody;
        this.loopBody.forEach(st -> st.setParent(this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Generator generator, final Object model, final Environment environment)
            throws IOException {
        final ObjectWithType<?> source = collectionExpression.evaluate(model, environment);
        if (Iterable.class.isAssignableFrom(source.getType())) {
            final Iterable<?> iterable = (Iterable<?>) source.getObject();
            final Iterator<?> iterator = iterable.iterator();
            long index = 0;
            Object nextItem = iterator.hasNext() ? iterator.next() : null;
            while (nextItem != null) {
                // neuen Frame auf dem Stack anlegen, damit die Variablen der Schleife nicht nach außen sichtbar werden
                final Object item = nextItem;
                nextItem = iterator.hasNext() ? iterator.next() : null;
                environment.createFrame(true);
                environment.addVariable(varName, item, item.getClass());
                if (counterName != null) {
                    environment.addVariable(counterName, new Counter(index, index == 0, nextItem == null),
                            Counter.class);
                }
                for (final UserCodeOrStatements<?> cos : loopBody) {
                    environment.execute(cos, generator, model);
                }
                if (separatorExpression != null && nextItem != null) {
                    final Writer writer = generator.getCurrentWriter();
                    if (writer == null) {
                        LOGGER.info("Kein Writer zur Ausgabe gefunden. " + this + ": " + this.getContext());
                        throw new GeneratorException("Kein Writer zur Ausgabe gefunden.", this, environment);
                    } else {
                        writer.write(String.valueOf(separatorExpression.evaluate(model, environment).getObject()));
                    }
                }
                index++;
                // Frame wieder vom Stack entfernen, damit die Umgebung nach dem Schleifendurchlauf wieder aussieht wie
                // vorher
                environment.dropFrame();
            }
        } else {
            throw new RuntimeException("don't know what to do with collection " + model.getClass().getName());
        }
    }
}