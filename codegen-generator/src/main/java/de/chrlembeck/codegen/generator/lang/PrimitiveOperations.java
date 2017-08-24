package de.chrlembeck.codegen.generator.lang;

/**
 * Interface für Binäre Operationen auf primitiven numerischen Datentypen.
 *
 * @author Christoph Lembeck
 */
public interface PrimitiveOperations {

    /**
     * Führt die Operation auf zwei int-Werten aus.
     * 
     * @param a
     *            Erster Operand der Operation.
     * @param b
     *            Zweiter Operand der Operation.
     * @return Ergebnis der Berechnung.
     */
    public ObjectWithType<?> apply(int a, int b);

    /**
     * Führt die Operation auf zwei float-Werten aus.
     * 
     * @param a
     *            Erster Operand der Operation.
     * @param b
     *            Zweiter Operand der Operation.
     * @return Ergebnis der Berechnung.
     */
    public ObjectWithType<?> apply(float a, float b);

    /**
     * Führt die Operation auf zwei long-Werten aus.
     * 
     * @param a
     *            Erster Operand der Operation.
     * @param b
     *            Zweiter Operand der Operation.
     * @return Ergebnis der Berechnung.
     */
    public ObjectWithType<?> apply(long a, long b);

    /**
     * Führt die Operation auf zwei double-Werten aus.
     * 
     * @param a
     *            Erster Operand der Operation.
     * @param b
     *            Zweiter Operand der Operation.
     * @return Ergebnis der Berechnung.
     */
    public ObjectWithType<?> apply(double a, double b);
}