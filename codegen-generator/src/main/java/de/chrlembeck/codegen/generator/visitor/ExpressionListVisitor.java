package de.chrlembeck.codegen.generator.visitor;

import java.util.List;
import java.util.stream.Collectors;

import de.chrlembeck.codegen.generator.lang.Expression;
import lang.CodeGenParser.ExpressionListContext;
import lang.CodeGenParserBaseVisitor;

/**
 * Visitor für die Verarbeitung des Context-Baumes des ANTLR-Parsers. Dieser Visitor enthält alle Konvertierungen von
 * Kontext-Objekten zu Listen von Expressions der CodeGen-Grammatik.
 * 
 * Christoph Lembeck
 */
public class ExpressionListVisitor extends CodeGenParserBaseVisitor<List<Expression>> {

    /**
     * Transformiert einen {@link ExpressionListContext} des Parsers in eine vom Generator verarbeitbare Liste von
     * {@link Expression}s.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public List<Expression> visitExpressionList(final ExpressionListContext ctx) {
        final ExpressionVisitor eVisitor = new ExpressionVisitor();
        return ctx.expression().stream().map(e -> e.accept(eVisitor)).collect(Collectors.toList());
    }
}