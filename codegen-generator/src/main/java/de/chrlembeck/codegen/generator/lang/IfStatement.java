package de.chrlembeck.codegen.generator.lang;

import java.io.IOException;
import java.util.List;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.Generator;
import de.chrlembeck.codegen.grammar.CodeGenParser.IfStatementContext;

/**
 * If-Statement einer Template-Datei. Über das IfStatement kann eine bedingte Ausführung von Codeblöcken gesteuert
 * werden.
 * 
 * @author Christoph Lembeck
 */
public class IfStatement extends AbstractTemplateMember<IfStatementContext>
        implements UserCodeOrStatements<IfStatementContext> {

    /**
     * Boolsche Expression, über die ermittelt werden kann, ob der if- oder der else-Block ausgeführt werden soll.
     */
    private Expression condition;

    /**
     * Expression, die ausgewertet wird, wenn die {@link #condition} zur {@code true} ausgewertet wurde.
     */
    private List<UserCodeOrStatements<?>> ifBlock;

    /**
     * Expression, die ausgewertet wird, wenn die {@link #condition} zu {@code false} ausgewertet wurde.
     */
    private List<UserCodeOrStatements<?>> elseBlock;

    /**
     * Erstellt das IfStatement mit den übergebenen Daten.
     * 
     * @param ctx
     *            ANTLR-Kontext, wie er vom Parser beim Lesen des Dokumentes erzeugt wurde.
     * @param condition
     *            Boolsche Expression, über die ermittelt werden kann, ob der if- oder der else-Block ausgeführt werden
     *            soll.
     * @param ifBlock
     *            Expression, die ausgewertet wird, wenn die {@link #condition} zur {@code true} ausgewertet wurde.
     * @param elseBlock
     *            Expression, die ausgewertet wird, wenn die {@link #condition} zu {@code false} ausgewertet wurde.
     */
    public IfStatement(final IfStatementContext ctx, final Expression condition,
            final List<UserCodeOrStatements<?>> ifBlock,
            final List<UserCodeOrStatements<?>> elseBlock) {
        super(ctx);
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
        ifBlock.forEach(codeOrStatement -> codeOrStatement.setParent(this));
        if (elseBlock != null) {
            elseBlock.forEach(codeOrStatement -> codeOrStatement.setParent(this));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Generator generator, final Object model, final Environment environment)
            throws IOException {
        final ObjectWithType<?> cond = condition.evaluate(model, environment);
        final Boolean value = (Boolean) cond.getObject();
        if (value != null && value.booleanValue()) {
            for (final UserCodeOrStatements<?> ucost : ifBlock) {
                environment.execute(ucost, generator, model);
            }
        } else {
            if (elseBlock != null) {
                for (final UserCodeOrStatements<?> ucost : elseBlock) {
                    environment.execute(ucost, generator, model);
                }
            }
        }
    }
}