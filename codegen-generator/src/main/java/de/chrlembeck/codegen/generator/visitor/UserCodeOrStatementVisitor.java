package de.chrlembeck.codegen.generator.visitor;

import java.util.List;

import de.chrlembeck.codegen.generator.lang.CommentStatement;
import de.chrlembeck.codegen.generator.lang.ExecuteStatement;
import de.chrlembeck.codegen.generator.lang.Expression;
import de.chrlembeck.codegen.generator.lang.ExpressionStatement;
import de.chrlembeck.codegen.generator.lang.ForStatement;
import de.chrlembeck.codegen.generator.lang.IfStatement;
import de.chrlembeck.codegen.generator.lang.OutputStatement;
import de.chrlembeck.codegen.generator.lang.UserCode;
import de.chrlembeck.codegen.generator.lang.UserCodeOrStatements;
import de.chrlembeck.codegen.grammar.CodeGenParser.CommentStatementContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.ExecuteStatementContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.ExpressionContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.ExpressionStatementContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.ForStatementContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.IfStatementContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.OutputStatementContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.StatementsContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.UserCodeContext;
import de.chrlembeck.codegen.grammar.CodeGenParserBaseVisitor;

/**
 * Visitor für die Verarbeitung des Context-Baumes des ANTLR-Parsers. Dieser Visitor enthält alle Konvertierungen von
 * Kontext-Objekten zu den UserCodeOrStatements-Typen der CodeGen-Grammatik.
 * 
 * Christoph Lembeck
 */
public class UserCodeOrStatementVisitor
        extends CodeGenParserBaseVisitor<UserCodeOrStatements<?>> {

    /**
     * Transformiert einen {@link UserCodeContext} des Parsers in ein vom Generator verarbeitbares
     * {@link UserCode}-Objekt.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public UserCodeOrStatements<?> visitUserCode(final UserCodeContext ctx) {
        final String code = ctx.AnyChar().getText();
        return new UserCode(ctx, code);
    }

    /**
     * Transformiert einen {@link StatementsContext} des Parsers in ein vom Generator verarbeitbares
     * {@link UserCodeOrStatements}-Objekt.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public UserCodeOrStatements<?> visitStatements(final StatementsContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * Transformiert einen {@link CommentStatementContext} des Parsers in ein vom Generator verarbeitbares
     * {@link CommentStatement}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public CommentStatement visitCommentStatement(final CommentStatementContext ctx) {
        return new CommentStatement(ctx);
    }

    /**
     * Transformiert einen {@link ExpressionStatementContext} des Parsers in ein vom Generator verarbeitbares
     * {@link ExpressionStatement}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public UserCodeOrStatements<?> visitExpressionStatement(final ExpressionStatementContext ctx) {
        final Expression expression = ctx.expression().accept(new ExpressionVisitor());
        return new ExpressionStatement(ctx, expression);
    }

    /**
     * Transformiert einen {@link IfStatementContext} des Parsers in ein vom Generator verarbeitbares
     * {@link IfStatement}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public UserCodeOrStatements<?> visitIfStatement(final IfStatementContext ctx) {
        final Expression expression = ctx.expression().accept(new ExpressionVisitor());
        final List<UserCodeOrStatements<?>> ifBlock = ctx.userCodeOrStatements(0)
                .accept(new UserCodeOrStatementsVisitor());
        final List<UserCodeOrStatements<?>> elseBlock = ctx.userCodeOrStatements().size() == 1 ? null
                : ctx.userCodeOrStatements(0).accept(new UserCodeOrStatementsVisitor());
        return new IfStatement(ctx, expression, ifBlock, elseBlock);
    }

    /**
     * Transformiert einen {@link OutputStatementContext} des Parsers in ein vom Generator verarbeitbares
     * {@link OutputStatement}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public UserCodeOrStatements<?> visitOutputStatement(final OutputStatementContext ctx) {
        final Expression nameExpression = ctx.expression().accept(new ExpressionVisitor());
        final List<UserCodeOrStatements<?>> cos = ctx.userCodeOrStatements().accept(new UserCodeOrStatementsVisitor());
        return new OutputStatement(ctx, nameExpression, cos);
    }

    /**
     * Transformiert einen {@link ForStatementContext} des Parsers in ein vom Generator verarbeitbares
     * {@link ForStatement}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public UserCodeOrStatements<?> visitForStatement(final ForStatementContext ctx) {
        final String varName = ctx.Identifier(0).getText();
        final Expression collectionExpression = ctx.expression(0).accept(new ExpressionVisitor());
        final String counterName = ctx.Identifier().size() > 1 ? ctx.Identifier(1).getText() : null;
        final Expression separatorExpression = ctx.expression().size() > 1
                ? ctx.expression(1).accept(new ExpressionVisitor())
                : null;
        final List<UserCodeOrStatements<?>> loopBody = ctx.userCodeOrStatements()
                .accept(new UserCodeOrStatementsVisitor());
        return new ForStatement(ctx, varName, collectionExpression, counterName, separatorExpression, loopBody);
    }

    /**
     * Transformiert einen {@link ExecuteStatementContext} des Parsers in ein vom Generator verarbeitbares
     * {@link ExecuteStatement}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public UserCodeOrStatements<?> visitExecuteStatement(final ExecuteStatementContext ctx) {
        final String prefix = ctx.prefix == null ? null : ctx.prefix.getText();
        final String templateName = ctx.templateName.getText();
        final List<ExpressionContext> expressions = ctx.expression();
        final Expression valueExpression = expressions.get(0).accept(new ExpressionVisitor());
        Expression separatorExpression = null;
        if (expressions.size() > 1) {
            separatorExpression = expressions.get(1).accept(new ExpressionVisitor());
        }
        final boolean forEach = ctx.FOREACH() != null;
        return new ExecuteStatement(ctx, prefix, templateName, valueExpression, forEach, separatorExpression);
    }
}