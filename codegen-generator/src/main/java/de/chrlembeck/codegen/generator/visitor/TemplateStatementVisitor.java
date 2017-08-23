package de.chrlembeck.codegen.generator.visitor;

import java.util.List;

import de.chrlembeck.codegen.generator.lang.TemplateStatement;
import de.chrlembeck.codegen.generator.lang.UserCodeOrStatements;
import lang.CodeGenParser.TemplateStatementContext;
import lang.CodeGenParserBaseVisitor;

/**
 * Visitor für die Verarbeitung des Context-Baumes des ANTLR-Parsers. Dieser Visitor enthält die Konvertierungen von
 * Kontext-Objekten zu den TemplateStatements der CodeGen-Grammatik.
 * 
 * Christoph Lembeck
 */
public class TemplateStatementVisitor extends CodeGenParserBaseVisitor<TemplateStatement> {

    /**
     * Transformiert einen {@link TemplateStatementContext} des Parsers in ein vom Generator verarbeitbares
     * {@link TemplateStatement}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public TemplateStatement visitTemplateStatement(final TemplateStatementContext ctx) {
        final String name = ctx.Identifier().getText();
        final String type = ctx.classOrInterfaceType().getText();
        final UserCodeOrStatementsVisitor visitor = new UserCodeOrStatementsVisitor();
        final List<UserCodeOrStatements<?>> cos = ctx.userCodeOrStatements().accept(visitor);
        return new TemplateStatement(ctx, name, type, cos);
    }
}