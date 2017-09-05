package de.chrlembeck.codegen.generator.lang;

import java.lang.reflect.Array;

import de.chrlembeck.codegen.grammar.CodeGenParser.TypeClassOrInterfaceContext;

/**
 * Stellt den Typ einer Klasse, eines Interfacs der eines Arrays einer Klasse oder eines Interfaces dar.
 * 
 * @author Christoph Lembeck
 */
public class ClassOrInterfaceType extends AbstractTemplateMember<TypeClassOrInterfaceContext>
        implements ClassOrPrimitiveType {

    /**
     * Dimension des Arrays &gt= 0;
     */
    private int arrayDim;

    /**
     * Gibt den Namen der Klasse, die in dem Array enthalten ist, zurück.
     */
    private String className;

    /**
     * Erstellt ein neuen Repräsentanten für einen Klassen- Interface oder Array-Typen.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser für dieses Objekt ermittelt hat.
     * @param className
     *            Name der Klasse, des Interfaces oder der in dem Array enthaltenen Klasse oder Interface.
     * @param arrayDim
     *            Dimension des Arrays.
     */
    public ClassOrInterfaceType(final TypeClassOrInterfaceContext ctx, final String className, final int arrayDim) {
        super(ctx);
        this.className = className;
        this.arrayDim = arrayDim;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getClassRef() throws ClassNotFoundException {
        Class<?> classRef;
        classRef = Class.forName(className);
        for (int i = 0; i < arrayDim; i++) {
            final Object array = Array.newInstance(classRef, 0);
            classRef = array.getClass();
        }
        return classRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return className;
    }
}