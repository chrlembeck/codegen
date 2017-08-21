package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import lang.CodeGenParser.LiteralContext;

/**
 * Repräsentiert die Boolean Literale True und False innerhalb der Template-Grammatik.
 * 
 * @author Christoph Lembeck
 */
public final class BooleanLiteral extends AbstractTemplateMember<LiteralContext> implements Literal {

    /**
     * Speichert den enthaltenen Boolean Wert TRUE oder FALSE.
     */
    private final Boolean value;

    /**
     * Legt ein neues BooleanLiteral mit dem Kontext des Parsers und dem vom lexer gefundenen Text an.
     * 
     * @param ctx
     *            Kontext des ANTLR-Parsers beim Parsen der Template-Datei.
     * @param text
     *            "true" für TRUE und "false" für FALSE. Alle anderen Eingaben führen zu einer IllegalArgumentException.
     * @throws IllegalArgumentException
     *             Falls der text nicht "true" oder "false" ist.
     */
    public BooleanLiteral(final LiteralContext ctx, final String text) {
        super(ctx);
        if ("true".equals(text)) {
            value = Boolean.TRUE;
        } else if ("false".equals(text)) {
            value = Boolean.FALSE;
        } else {
            throw new IllegalArgumentException("unknown boolean literal (" + text + ")");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Wertet den Ausdruck zu dem true oder false aus. Der Typ des Ergebnisses ist {@code boolean.class}.
     */
    @Override
    public ObjectWithType<Boolean> evaluate(final Object model, final Environment environment) {
        return new ObjectWithType<Boolean>(value, boolean.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallSource findCallSource(final Object model, final Environment environment) {
        return new ObjectCallSource(evaluate(model, environment));
    }
}