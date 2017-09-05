package de.chrlembeck.codegen.generator.visitor;

import de.chrlembeck.codegen.generator.lang.ClassOrPrimitiveType;
import de.chrlembeck.codegen.generator.lang.Expression;
import de.chrlembeck.codegen.generator.lang.Identifier;
import de.chrlembeck.codegen.generator.lang.Literal;
import de.chrlembeck.codegen.generator.lang.Primary;
import de.chrlembeck.codegen.generator.lang.SuperReference;
import de.chrlembeck.codegen.generator.lang.ThisReference;
import de.chrlembeck.codegen.generator.lang.TypeRef;
import de.chrlembeck.codegen.generator.lang.VoidRef;
import de.chrlembeck.codegen.grammar.CodeGenParser.PrimaryIdentifierContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.PrimaryLiteralContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.PrimarySubExpressionContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.PrimarySuperReferenceContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.PrimaryThisReferenceContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.PrimaryTypeClassContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.PrimaryVoidClassContext;
import de.chrlembeck.codegen.grammar.CodeGenParserBaseVisitor;

/**
 * Visitor für die Verarbeitung des Context-Baumes des ANTLR-Parsers. Dieser Visitor enthält alle Konvertierungen von
 * Kontext-Objekten zu den Primary-Typen der CodeGen-Grammatik.
 * 
 * Christoph Lembeck
 */
public class PrimaryVisitor extends CodeGenParserBaseVisitor<Expression> {

    /**
     * Transformiert einen {@link PrimaryLiteralContext} des Parsers in ein vom Generator verarbeitbares
     * {@link Literal}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Primary visitPrimaryLiteral(final PrimaryLiteralContext ctx) {
        return ctx.accept(new LiteralVisitor());
    }

    /**
     * Transformiert einen {@link PrimaryIdentifierContext} des Parsers in ein vom Generator verarbeitbaren
     * {@link Identifier}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Primary visitPrimaryIdentifier(final PrimaryIdentifierContext ctx) {
        return new Identifier(ctx);
    }

    /**
     * Transformiert einen {@link PrimarySubExpressionContext} des Parsers in eine vom Generator verarbeitbare
     * {@link Expression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitPrimarySubExpression(final PrimarySubExpressionContext ctx) {
        return ctx.expression().accept(new ExpressionVisitor());
    }

    /**
     * Transformiert einen {@link PrimarySuperReferenceContext} des Parsers in eine vom Generator verarbeitbare
     * {@link SuperReference}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Primary visitPrimarySuperReference(final PrimarySuperReferenceContext ctx) {
        return new SuperReference(ctx);
    }

    /**
     * Transformiert einen {@link PrimaryThisReferenceContext} des Parsers in eine vom Generator verarbeitbare
     * {@link ThisReference}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Primary visitPrimaryThisReference(final PrimaryThisReferenceContext ctx) {
        return new ThisReference(ctx);
    }

    /**
     * Transformiert einen {@link PrimaryTypeClassContext} des Parsers in eine vom Generator verarbeitbare
     * {@link TypeRef}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Primary visitPrimaryTypeClass(final PrimaryTypeClassContext ctx) {
        final ClassOrPrimitiveType typeType = ctx.classOrPrimitiveType().accept(new ClassOrInterfaceTypeVisitor());
        return new TypeRef(ctx, typeType);
    }

    /**
     * Transformiert einen {@link PrimaryVoidClassContext} des Parsers in eine vom Generator verarbeitbare
     * {@link VoidRef}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Primary visitPrimaryVoidClass(final PrimaryVoidClassContext ctx) {
        return new VoidRef(ctx);
    }

}