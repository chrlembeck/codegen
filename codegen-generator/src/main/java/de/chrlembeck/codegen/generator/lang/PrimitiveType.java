package de.chrlembeck.codegen.generator.lang;

import lang.CodeGenParser.TypePrimitiveContext;

/**
 * Stellt einen primitiven Datentyp oder ein Array eined primitiven Typs dar.
 *
 * @author Christoph Lembeck
 */
public class PrimitiveType extends AbstractTemplateMember<TypePrimitiveContext> implements ClassOrPrimitiveType {

    /**
     * Referenz auf den primitiven Typen oder das Array des Typs.
     */
    private Class<?> classRef;

    /**
     * Erzeugt einen neuen Repräsentanten des übergebenen Typs.
     * 
     * @param ctx
     *            Kontext des Parsers, so wie er ihn für dieses Element gelesen hat.
     * @param classRef
     *            Referenz auf den primitiven Typen oder den Array-Typen eines primitiven Typs.
     */
    public PrimitiveType(final TypePrimitiveContext ctx, final Class<?> classRef) {
        super(ctx);
        this.classRef = classRef;
    }

    /**
     * Gibt den Typ oder Array-Typ des primitiven Datentyps zurück.
     */
    @Override
    public Class<?> getClassRef() {
        return classRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return classRef.getName();
    }
}