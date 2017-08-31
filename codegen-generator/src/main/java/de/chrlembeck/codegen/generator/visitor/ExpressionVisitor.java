package de.chrlembeck.codegen.generator.visitor;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.Token;

import de.chrlembeck.codegen.generator.ParserException;
import de.chrlembeck.codegen.generator.lang.AndExpression;
import de.chrlembeck.codegen.generator.lang.ArrayAccessExpression;
import de.chrlembeck.codegen.generator.lang.AttributeExpression;
import de.chrlembeck.codegen.generator.lang.CastExpression;
import de.chrlembeck.codegen.generator.lang.ClassOrPrimitiveType;
import de.chrlembeck.codegen.generator.lang.CompareExpression;
import de.chrlembeck.codegen.generator.lang.ConditionalAndExpression;
import de.chrlembeck.codegen.generator.lang.ConditionalExpression;
import de.chrlembeck.codegen.generator.lang.ConditionalOrExpression;
import de.chrlembeck.codegen.generator.lang.EqualsExpression;
import de.chrlembeck.codegen.generator.lang.Expression;
import de.chrlembeck.codegen.generator.lang.InstanceofExpression;
import de.chrlembeck.codegen.generator.lang.MethodCallExpression;
import de.chrlembeck.codegen.generator.lang.MultDivModExpression;
import de.chrlembeck.codegen.generator.lang.NegationExpression;
import de.chrlembeck.codegen.generator.lang.OrExpression;
import de.chrlembeck.codegen.generator.lang.PlusMinusExpression;
import de.chrlembeck.codegen.generator.lang.ShiftExpression;
import de.chrlembeck.codegen.generator.lang.SignExpression;
import de.chrlembeck.codegen.generator.lang.XorExpression;
import lang.CodeGenLexer;
import lang.CodeGenParser.ExpressionAndContext;
import lang.CodeGenParser.ExpressionArrayAccessContext;
import lang.CodeGenParser.ExpressionAttributeContext;
import lang.CodeGenParser.ExpressionCastContext;
import lang.CodeGenParser.ExpressionCompareContext;
import lang.CodeGenParser.ExpressionConditionalAndContext;
import lang.CodeGenParser.ExpressionConditionalContext;
import lang.CodeGenParser.ExpressionConditionalOrContext;
import lang.CodeGenParser.ExpressionEqualsContext;
import lang.CodeGenParser.ExpressionInstanceofContext;
import lang.CodeGenParser.ExpressionMethodCallContext;
import lang.CodeGenParser.ExpressionMultDivModContext;
import lang.CodeGenParser.ExpressionNegContext;
import lang.CodeGenParser.ExpressionOrContext;
import lang.CodeGenParser.ExpressionPlusMinusContext;
import lang.CodeGenParser.ExpressionPrimaryContext;
import lang.CodeGenParser.ExpressionShiftContext;
import lang.CodeGenParser.ExpressionSignContext;
import lang.CodeGenParser.ExpressionSuperContext;
import lang.CodeGenParser.ExpressionThisContext;
import lang.CodeGenParser.ExpressionXorContext;
import lang.CodeGenParserBaseVisitor;

/**
 * Visitor für die Verarbeitung des Context-Baumes des ANTLR-Parsers. Dieser Visitor enthält alle Konvertierungen von
 * Kontext-Objekten zu den Expression-Typen der CodeGen-Grammatik.
 * 
 * Christoph Lembeck
 */
@SuppressWarnings("PMD.GodClass")
public class ExpressionVisitor extends CodeGenParserBaseVisitor<Expression> {

    /**
     * Transformiert einen {@link ExpressionPrimaryContext} des Parsers in ein vom Generator verarbeitbares
     * {@link Expression}-Objekt.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionPrimary(final ExpressionPrimaryContext ctx) {
        return ctx.accept(new PrimaryVisitor());
    }

    /**
     * Transformiert einen {@link ExpressionAttributeContext} des Parsers in eine vom Generator verarbeitbare
     * {@link AttributeExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionAttribute(final ExpressionAttributeContext ctx) {
        final Expression expression = ctx.expression().accept(this);
        final String identifier = ctx.Identifier().toString();
        return new AttributeExpression(ctx, expression, identifier);
    }

    @Override
    @SuppressWarnings("PMD.CommentRequired")
    public Expression visitExpressionThis(final ExpressionThisContext ctx) {
        // TODO implement
        throw new RuntimeException("not yet implemented");
    }

    @Override
    @SuppressWarnings("PMD.CommentRequired")
    public Expression visitExpressionSuper(final ExpressionSuperContext ctx) {
        // TODO implement
        throw new RuntimeException("not yet implemented");
    }

    /**
     * Transformiert einen {@link ExpressionArrayAccessContext} des Parsers in eine vom Generator verarbeitbare
     * {@link ArrayAccessExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionArrayAccess(final ExpressionArrayAccessContext ctx) {
        final Expression source = ctx.expression(0).accept(this);
        final Expression index = ctx.expression(1).accept(this);
        return new ArrayAccessExpression(ctx, source, index);
    }

    /**
     * Transformiert einen {@link ExpressionMethodCallContext} des Parsers in eine vom Generator verarbeitbare
     * {@link MethodCallExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionMethodCall(final ExpressionMethodCallContext ctx) {
        final Expression methodExpression = ctx.expression().accept(this);
        @SuppressWarnings("unchecked")
        List<Expression> arguments = Collections.EMPTY_LIST;
        if (ctx.expressionList() != null) {
            arguments = ctx.expressionList().accept(new ExpressionListVisitor());
        }
        return new MethodCallExpression(ctx, methodExpression, arguments);
    }

    /**
     * Transformiert einen {@link ExpressionCastContext} des Parsers in eine vom Generator verarbeitbare
     * {@link CastExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionCast(final ExpressionCastContext ctx) {
        final ClassOrPrimitiveType typeType = ctx.classOrPrimitiveType().accept(new ClassOrInterfaceTypeVisitor());
        final Expression expression = ctx.expression().accept(this);
        return new CastExpression(ctx, typeType, expression);
    }

    /**
     * Transformiert einen {@link ExpressionSignContext} des Parsers in eine vom Generator verarbeitbare
     * {@link SignExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionSign(final ExpressionSignContext ctx) {
        final Expression expr = ctx.expression().accept(this);
        final Token operator = ctx.operator;
        if (operator != null && operator.getType() == CodeGenLexer.ADD) {
            // PLUS
            return new SignExpression(ctx, SignExpression.Operator.PLUS, expr);
        } else if (operator != null && operator.getType() == CodeGenLexer.SUB) {
            // MINUS
            return new SignExpression(ctx, SignExpression.Operator.MINUS, expr);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Transformiert einen {@link ExpressionNegContext} des Parsers in eine vom Generator verarbeitbare
     * {@link NegationExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionNeg(final ExpressionNegContext ctx) {
        final Expression expr = ctx.expression().accept(this);
        final Token operator = ctx.operator;
        if (operator != null && operator.getType() == CodeGenLexer.BANG) {
            // Boolean negation
            return new NegationExpression(ctx, NegationExpression.Operator.BOOLEAN_NEGATION, expr);
        } else if (operator != null && operator.getType() == CodeGenLexer.TILDE) {
            // Numeric negation
            return new NegationExpression(ctx, NegationExpression.Operator.NUMERIC_NEGATION, expr);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Transformiert einen {@link ExpressionMultDivModContext} des Parsers in eine vom Generator verarbeitbare
     * {@link MultDivModExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionMultDivMod(final ExpressionMultDivModContext ctx) {
        final Expression left = ctx.expression(0).accept(this);
        final Expression right = ctx.expression(1).accept(this);
        if (ctx.operator.getType() == CodeGenLexer.MUL) {
            return new MultDivModExpression(ctx, left, right, MultDivModExpression.Operator.MULT);
        } else if (ctx.operator.getType() == CodeGenLexer.DIV) {
            return new MultDivModExpression(ctx, left, right, MultDivModExpression.Operator.DIV);
        } else if (ctx.operator.getType() == CodeGenLexer.MOD) {
            return new MultDivModExpression(ctx, left, right, MultDivModExpression.Operator.MOD);
        } else {
            throw new RuntimeException("unknown operator");
        }
    }

    /**
     * Transformiert einen {@link ExpressionPlusMinusContext} des Parsers in eine vom Generator verarbeitbare
     * {@link PlusMinusExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionPlusMinus(final ExpressionPlusMinusContext ctx) {
        final Expression left = ctx.expression(0).accept(this);
        final Expression right = ctx.expression(1).accept(this);
        if (ctx.operator.getType() == CodeGenLexer.ADD) {
            return new PlusMinusExpression(ctx, left, right, PlusMinusExpression.Operator.PLUS);
        } else if (ctx.operator.getType() == CodeGenLexer.SUB) {
            return new PlusMinusExpression(ctx, left, right, PlusMinusExpression.Operator.MINUS);
        } else {
            throw new RuntimeException("unknown operator");
        }
    }

    /**
     * Transformiert einen {@link ExpressionShiftContext} des Parsers in eine vom Generator verarbeitbare
     * {@link ShiftExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionShift(final ExpressionShiftContext ctx) {
        final Expression left = ctx.expression(0).accept(this);
        final Expression right = ctx.expression(1).accept(this);

        if (isLeftShift(ctx.op)) {
            return new ShiftExpression(ctx, left, right, ShiftExpression.Operator.LEFT_SHIFT);
        }
        if (isSignedRightShift(ctx.op)) {
            return new ShiftExpression(ctx, left, right, ShiftExpression.Operator.SIGNED_RIGHT_SHIFT);
        }
        if (isUnsignedRightShift(ctx.op)) {
            return new ShiftExpression(ctx, left, right, ShiftExpression.Operator.UNSIGNED_RIGHT_SHIFT);
        } else {
            throw new ParserException("Unerwarteter Shift-Operator: " + ctx.op, ctx);
        }
    }

    /**
     * Transformiert einen {@link ExpressionCompareContext} des Parsers in eine vom Generator verarbeitbare
     * {@link CompareExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionCompare(final ExpressionCompareContext ctx) {
        CompareExpression.Operator operator;
        switch (ctx.operator.getType()) {
            case CodeGenLexer.LT:
                operator = CompareExpression.Operator.LESS_THAN;
                break;
            case CodeGenLexer.LE:
                operator = CompareExpression.Operator.LESS_OR_EQUAL;
                break;
            case CodeGenLexer.GE:
                operator = CompareExpression.Operator.GREATER_OR_EQUAL;
                break;
            case CodeGenLexer.GT:
                operator = CompareExpression.Operator.GREATER_THAN;
                break;
            default:
                throw new ParserException("Unerwarteter Vergleichsoperator: " + ctx.operator.getText(), ctx);
        }
        final Expression left = ctx.expression(0).accept(this);
        final Expression right = ctx.expression(1).accept(this);
        return new CompareExpression(ctx, left, right, operator);
    }

    /**
     * Transformiert einen {@link ExpressionInstanceofContext} des Parsers in eine vom Generator verarbeitbare
     * {@link InstanceofExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionInstanceof(final ExpressionInstanceofContext ctx) {
        final Expression expression = ctx.expression().accept(this);
        final ClassOrPrimitiveType typeType = ctx.classOrPrimitiveType().accept(new ClassOrInterfaceTypeVisitor());
        return new InstanceofExpression(ctx, expression, typeType);
    }

    /**
     * Transformiert einen {@link ExpressionEqualsContext} des Parsers in eine vom Generator verarbeitbare
     * {@link EqualsExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionEquals(final ExpressionEqualsContext ctx) {
        final Expression left = ctx.expression(0).accept(this);
        final Expression right = ctx.expression(1).accept(this);
        if (ctx.operator.getType() == CodeGenLexer.EQUAL) {
            return new EqualsExpression(ctx, left, right, EqualsExpression.Operator.EQUAL);
        } else if (ctx.operator.getType() == CodeGenLexer.NOTEQUAL) {
            return new EqualsExpression(ctx, left, right, EqualsExpression.Operator.NOT_EQUAL);
        } else {
            throw new IllegalStateException("Unexpected operator: " + ctx.operator);
        }
    }

    /**
     * Transformiert einen {@link ExpressionAndContext} des Parsers in eine vom Generator verarbeitbare
     * {@link AndExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionAnd(final ExpressionAndContext ctx) {
        final Expression left = ctx.expression(0).accept(this);
        final Expression right = ctx.expression(1).accept(this);
        return new AndExpression(ctx, left, right);
    }

    /**
     * Transformiert einen {@link ExpressionXorContext} des Parsers in eine vom Generator verarbeitbare
     * {@link XorExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionXor(final ExpressionXorContext ctx) {
        final Expression left = ctx.expression(0).accept(this);
        final Expression right = ctx.expression(1).accept(this);
        return new XorExpression(ctx, left, right);
    }

    /**
     * Transformiert einen {@link ExpressionOrContext} des Parsers in eine vom Generator verarbeitbare
     * {@link OrExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionOr(final ExpressionOrContext ctx) {
        final Expression left = ctx.expression(0).accept(this);
        final Expression right = ctx.expression(1).accept(this);
        return new OrExpression(ctx, left, right);
    }

    /**
     * Transformiert einen {@link ExpressionConditionalAndContext} des Parsers in eine vom Generator verarbeitbare
     * {@link ConditionalAndExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionConditionalAnd(final ExpressionConditionalAndContext ctx) {
        final Expression left = ctx.expression(0).accept(this);
        final Expression right = ctx.expression(1).accept(this);
        return new ConditionalAndExpression(ctx, left, right);
    }

    /**
     * Transformiert einen {@link ExpressionConditionalOrContext} des Parsers in eine vom Generator verarbeitbare
     * {@link ConditionalOrExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionConditionalOr(final ExpressionConditionalOrContext ctx) {
        final Expression left = ctx.expression(0).accept(this);
        final Expression right = ctx.expression(1).accept(this);
        return new ConditionalOrExpression(ctx, left, right);
    }

    /**
     * Transformiert einen {@link ExpressionConditionalContext} des Parsers in eine vom Generator verarbeitbare
     * {@link ConditionalExpression}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Expression visitExpressionConditional(final ExpressionConditionalContext ctx) {
        final Expression condition = ctx.condition.accept(this);
        final Expression ifExpression = ctx.ifExpression.accept(this);
        final Expression elseExpression = ctx.elseExpression.accept(this);
        return new ConditionalExpression(ctx, condition, ifExpression, elseExpression);
    }

    /**
     * Überprüft, ob die übergebene Liste von Token genau die passenden Elemente für die Darstellung des
     * Left-Shift-Operators enthält.
     * 
     * @param tokens
     *            Liste der zu prüfenden Token.
     * @return true, falls die Liste genau nur die Token für den Left-Shift-Operator enthält, sonst false.
     */
    private static boolean isLeftShift(final List<Token> tokens) {
        return tokens.size() == 2
                && tokens.get(0).getType() == CodeGenLexer.LT
                && tokens.get(1).getType() == CodeGenLexer.LT;
    }

    /**
     * Überprüft, ob die übergebene Liste von Token genau die passenden Elemente für die Darstellung des
     * Signed-Right-Shift-Operators enthält.
     * 
     * @param tokens
     *            Liste der zu prüfenden Token.
     * @return true, falls die Liste genau nur die Token für den Signed-Right-Operator enthält, sonst false.
     */
    private static boolean isSignedRightShift(final List<Token> tokens) {
        return tokens.size() == 2
                && tokens.get(0).getType() == CodeGenLexer.GT
                && tokens.get(1).getType() == CodeGenLexer.GT;
    }

    /**
     * Überprüft, ob die übergebene Liste von Token genau die passenden Elemente für die Darstellung des
     * Unsigned-Right-Shift-Operators enthält.
     * 
     * @param tokens
     *            Liste der zu prüfenden Token.
     * @return true, falls die Liste genau nur die Token für den Unsigned-Right-Shift-Operator enthält, sonst false.
     */
    private static boolean isUnsignedRightShift(final List<Token> tokens) {
        return tokens.size() == 3
                && tokens.get(0).getType() == CodeGenLexer.GT
                && tokens.get(1).getType() == CodeGenLexer.GT
                && tokens.get(2).getType() == CodeGenLexer.GT;
    }
}