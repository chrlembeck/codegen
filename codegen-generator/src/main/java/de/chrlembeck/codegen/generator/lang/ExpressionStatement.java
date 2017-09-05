package de.chrlembeck.codegen.generator.lang;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.Generator;
import de.chrlembeck.codegen.generator.GeneratorException;
import de.chrlembeck.codegen.grammar.CodeGenParser.ExpressionStatementContext;

/**
 * Ein Expression Statment kann inerhalb einer Template-Definition dazu verwandt werden, einzelne, java-ähnliche
 * Ausdrücke der Template-Sprache auszuführen und das Ergebnis als Teil der generierten Dateien zu verwenden.
 *
 * @author Christoph Lembeck
 */
public class ExpressionStatement extends AbstractTemplateMember<ExpressionStatementContext>
        implements UserCodeOrStatements<ExpressionStatementContext> {

    /**
     * Der Logger für die Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionStatement.class);

    /**
     * Expression, die beim Generieren ausgewertet und in die Ausgabe kopiert werden soll.
     */
    private Expression expression;

    /**
     * Erstellt das ExpressionStatement mit den übergebenen Daten.
     * 
     * @param ctx
     *            ANTLR-Kontext, wie er vom Parser beim Lesen des Dokumentes erzeugt wurde.
     * @param expression
     *            Expression, die beim Generieren ausgewertet und in die Ausgabe kopiert werden soll.
     */
    public ExpressionStatement(final ExpressionStatementContext ctx, final Expression expression) {
        super(ctx);
        this.expression = expression;
    }

    /**
     * Gibt die enthaltene Expression zurück.
     * 
     * @return Expression, die beim Generieren ausgewertet und in die Ausgabe kopiert werden soll.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ExpressionStatement[" + expression + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Generator generator, final Object model, final Environment environment)
            throws IOException {
        final ObjectWithType<?> objectWithType = expression.evaluate(model, environment);
        final Object object = objectWithType.getObject();
        final String text = String.valueOf(object);
        final Writer currentWriter = generator.getCurrentWriter();
        if (currentWriter == null) {
            LOGGER.info("Kein Writer zur Ausgabe gefunden. " + this + ": " + this.getContext());
            throw new GeneratorException("Kein Writer zur Ausgabe gefunden.", this, environment);
        } else {
            currentWriter.append(text);
        }
    }
}