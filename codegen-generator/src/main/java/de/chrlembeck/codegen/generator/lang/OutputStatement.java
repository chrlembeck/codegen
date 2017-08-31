package de.chrlembeck.codegen.generator.lang;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.Generator;
import lang.CodeGenParser.OutputStatementContext;

/**
 * Enthält ein OutputStatement der Template-Datei. Output-Statements definieren einen Ausgabekanal, in den die Elemente,
 * die in dem Statement enthalten sind und alle, die davon aufgerufen werden, ihre Ausgaben schreiben können.
 * 
 * @author Christoph Lembeck
 */
public class OutputStatement extends AbstractTemplateMember<OutputStatementContext>
        implements UserCodeOrStatements<OutputStatementContext> {

    /**
     * Liste von Code-Blöcken und Statements, die in den Ausgabe-Channel geschrieben werden sollen.
     */
    private List<UserCodeOrStatements<?>> codeOrStatements;

    /**
     * Expression, aus der sich der Channel-Name für die Ausgabe ermitteln lässt.
     */
    private Expression nameExpression;

    /**
     * Erstellt das Statement mit den übergebenen Daten.
     * 
     * @param ctx
     *            ANTLR-Kontext, wie er vom Parser beim Lesen des Dokumentes erzeugt wurde.
     * @param nameExpression
     *            Expression, aus der sich der Channel-Name für die Ausgabe ermitteln lässt.
     * @param cos
     *            Liste von Code-Blöcken und Statements, die in den Ausgabe-Channel geschrieben werden sollen.
     */
    public OutputStatement(final OutputStatementContext ctx, final Expression nameExpression,
            final List<UserCodeOrStatements<?>> cos) {
        super(ctx);
        this.nameExpression = nameExpression;
        this.codeOrStatements = cos;
        this.codeOrStatements.forEach(codeOrStatement -> codeOrStatement.setParent(this));
    }

    /**
     * Gibt die Liste von Code-Blöcken und Statements aus, die in den Ausgabe-Channel geschrieben werden sollen.
     * 
     * @return Liste von Code-Blöcken und Statements, die in den Ausgabe-Channel geschrieben werden sollen.
     */
    public List<UserCodeOrStatements<?>> getCodeOrStatements() {
        return codeOrStatements;
    }

    /**
     * Gibt die Expression zurück, aus der sich der Name des Ausgabechannels entnehmenlässt.
     * 
     * @return Exression für den Namen des Ausgabe-Channels.
     */
    public Expression getNameExpression() {
        return nameExpression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "OutputStatement[nameExpression=" + nameExpression + ", codeOrStatements=" + codeOrStatements + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Generator generator, final Object model, final Environment environment)
            throws IOException {
        final ObjectWithType<?> nameExp = nameExpression.evaluate(model, environment);
        if (!(String.class.isAssignableFrom(nameExp.getType()))) {
            throw new RuntimeException(
                    "String expression expected but " + nameExp.getType().getName() + " found instead. ("
                            + nameExpression.getStartPosition() + ")");
        }
        final String channelName = (String) nameExp.getObject();
        try (final Writer writer = generator.getWriter(channelName)) {
            final Writer oldWriter = generator.getCurrentWriter();
            generator.setCurrentWriter(writer);
            for (final UserCodeOrStatements<?> cos : codeOrStatements) {
                environment.execute(cos, generator, model);
            }
            generator.setCurrentWriter(oldWriter);
        }
    }
}