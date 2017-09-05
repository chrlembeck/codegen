package de.chrlembeck.codegen.generator.visitor;

import de.chrlembeck.codegen.generator.lang.BooleanLiteral;
import de.chrlembeck.codegen.generator.lang.CharacterLiteral;
import de.chrlembeck.codegen.generator.lang.FloatingPointLiteral;
import de.chrlembeck.codegen.generator.lang.IntegerLiteral;
import de.chrlembeck.codegen.generator.lang.Literal;
import de.chrlembeck.codegen.generator.lang.NullLiteral;
import de.chrlembeck.codegen.generator.lang.StringLiteral;
import lang.CodeGenParser.LiteralContext;
import lang.CodeGenParserBaseVisitor;

/**
 * Visitor für die Verarbeitung des Context-Baumes des ANTLR-Parsers. Dieser Visitor enthält alle Konvertierungen von
 * Kontext-Objekten zu den Literalen der CodeGen-Grammatik.
 * 
 * Christoph Lembeck
 */
public class LiteralVisitor extends CodeGenParserBaseVisitor<Literal> {

    /**
     * Transformiert einen {@link LiteralContext} des Parsers in ein vom Generator verarbeitbares {@link Literal}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Literal visitLiteral(final LiteralContext ctx) {
        if (notNull(ctx.IntegerLiteral())) {
            return new IntegerLiteral(ctx, ctx.IntegerLiteral().getText());
        } else if (notNull(ctx.FloatingPointLiteral())) {
            return new FloatingPointLiteral(ctx, ctx.FloatingPointLiteral().getText());
        } else if (notNull(ctx.CharacterLiteral())) {
            final String literal = ctx.CharacterLiteral().getText();
            return new CharacterLiteral(ctx, literal.substring(1, literal.length() - 1));
        } else if (notNull(ctx.StringLiteral())) {
            return new StringLiteral(ctx, ctx.StringLiteral().getText());
        } else if (notNull(ctx.BooleanLiteral())) {
            return new BooleanLiteral(ctx, ctx.BooleanLiteral().getText());
            /// CLOVER:OFF
        } else if (ctx.getChild(0).getText().equals("null")) {
            /// CLOVER:ON
            return new NullLiteral(ctx);
        } else {
            /// CLOVER:OFF
            throw new RuntimeException("not implemented");
            /// CLOVER:ON
        }
    }

    /**
     * Prüft, ob der übergebene Wert ungleich null ist.
     * 
     * @param value
     *            Wert, der mit null verglichen werden soll.
     * @return {@code false}, falls der Wert {@code null} ist, sonst {@code true}.
     */
    public static boolean notNull(final Object value) {
        return value != null;
    }
}