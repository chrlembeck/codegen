package de.chrlembeck.codegen.generator.visitor;

import java.lang.reflect.Array;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.chrlembeck.codegen.generator.lang.ClassOrInterfaceType;
import de.chrlembeck.codegen.generator.lang.PrimitiveType;
import de.chrlembeck.codegen.generator.lang.ClassOrPrimitiveType;
import lang.CodeGenParser.TypeClassOrInterfaceContext;
import lang.CodeGenParser.TypePrimitiveContext;
import lang.CodeGenParserBaseVisitor;

/**
 * Visitor für die Verarbeitung des Context-Baumes des ANTLR-Parsers. Dieser Visitor enthält alle Konvertierungen von
 * Kontext-Objekten zu den TypeType-Typen der CodeGen-Grammatik.
 * 
 * Christoph Lembeck
 */
public class ClassOrInterfaceTypeVisitor extends CodeGenParserBaseVisitor<ClassOrPrimitiveType> {

    /**
     * Transformiert einen {@link TypeClassOrInterfaceContext} des Parsers in ein vom Generator verarbeitbares
     * {@link ClassOrInterfaceType}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public ClassOrPrimitiveType visitTypeClassOrInterface(final TypeClassOrInterfaceContext ctx) {
        final StringBuilder name = new StringBuilder();
        for (final TerminalNode id : ctx.classOrInterfaceType().Identifier()) {
            if (name.length() > 0) {
                name.append('.');
            }
            name.append(id.getText());
        }
        return new ClassOrInterfaceType(ctx, name.toString(), ctx.arr.size());
    }

    /**
     * Transformiert einen {@link TypePrimitiveContext} des Parsers in ein vom Generator verarbeitbares
     * {@link PrimitiveType}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public ClassOrPrimitiveType visitTypePrimitive(final TypePrimitiveContext ctx) {
        Class<?> classRef = ctx.primitiveType().accept(new PrimitiveTypeVisitor());
        if (ctx.arr != null) {
            for (@SuppressWarnings("unused")
            final Token token : ctx.arr) {
                final Object array = Array.newInstance(classRef, 0);
                classRef = array.getClass();
            }
        }
        return new PrimitiveType(ctx, classRef);
    }
}