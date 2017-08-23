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
        if (ctx.IntegerLiteral() != null) {
            return new IntegerLiteral(ctx, ctx.IntegerLiteral().getText());
        } else if (ctx.FloatingPointLiteral() != null) {
            return new FloatingPointLiteral(ctx, ctx.FloatingPointLiteral().getText());
        } else if (ctx.CharacterLiteral() != null) {
            final String literal = ctx.CharacterLiteral().getText();
            return new CharacterLiteral(ctx, literal.substring(1, literal.length() - 1));
        } else if (ctx.StringLiteral() != null) {
            return new StringLiteral(ctx, ctx.StringLiteral().getText());
        } else if (ctx.BooleanLiteral() != null) {
            return new BooleanLiteral(ctx, ctx.BooleanLiteral().getText());
        } else if (ctx.getChild(0).getText().equals("null")) {
            return new NullLiteral(ctx);
        } else {
            throw new RuntimeException("not implemented");
        }
    }
}