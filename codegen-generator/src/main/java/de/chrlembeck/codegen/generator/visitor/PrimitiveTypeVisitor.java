package de.chrlembeck.codegen.generator.visitor;

import de.chrlembeck.codegen.generator.ParserException;
import de.chrlembeck.codegen.grammar.CodeGenLexer;
import de.chrlembeck.codegen.grammar.CodeGenParser.PrimitiveTypeContext;
import de.chrlembeck.codegen.grammar.CodeGenParserBaseVisitor;

/**
 * Visitor für die Verarbeitung des Context-Baumes des ANTLR-Parsers. Dieser Visitor enthält die Konvertierungen von
 * Kontext-Objekten zu den PrimitiveType-Objekten der CodeGen-Grammatik.
 * 
 * Christoph Lembeck
 */
public class PrimitiveTypeVisitor extends CodeGenParserBaseVisitor<Class<?>> {

    /**
     * Transformiert einen {@link PrimitiveTypeContext} des Parsers in ein vom Generator verarbeitbares
     * {@link Class}-Objekt.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public Class<?> visitPrimitiveType(final PrimitiveTypeContext ctx) {
        switch (ctx.type.getType()) {
            case CodeGenLexer.BOOLEAN:
                return boolean.class;
            case CodeGenLexer.BYTE:
                return byte.class;
            case CodeGenLexer.CHAR:
                return char.class;
            case CodeGenLexer.SHORT:
                return short.class;
            case CodeGenLexer.INT:
                return int.class;
            case CodeGenLexer.FLOAT:
                return float.class;
            case CodeGenLexer.LONG:
                return long.class;
            case CodeGenLexer.DOUBLE:
                return double.class;
            /// CLOVER:OFF
            default:
                throw new ParserException("Unerwarteter primitiver typ: " + ctx.type, ctx);
                /// CLOVER:ON
        }
    }
}