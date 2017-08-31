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
     * @param leftOperand
     *            Erster Operand der Operation.
     * @param rightOperand
     *            Zweiter Operand der Operation.
     * @return Ergebnis der Berechnung.
     */
    ObjectWithType<?> apply(int leftOperand, int rightOperand);

    /**
     * Führt die Operation auf zwei float-Werten aus.
     * 
     * @param leftOperand
     *            Erster Operand der Operation.
     * @param rightOperand
     *            Zweiter Operand der Operation.
     * @return Ergebnis der Berechnung.
     */
    ObjectWithType<?> apply(float leftOperand, float rightOperand);

    /**
     * Führt die Operation auf zwei long-Werten aus.
     * 
     * @param leftOperand
     *            Erster Operand der Operation.
     * @param rightOperand
     *            Zweiter Operand der Operation.
     * @return Ergebnis der Berechnung.
     */
    ObjectWithType<?> apply(long leftOperand, long rightOperand);

    /**
     * Führt die Operation auf zwei double-Werten aus.
     * 
     * @param leftOperand
     *            Erster Operand der Operation.
     * @param rightOperand
     *            Zweiter Operand der Operation.
     * @return Ergebnis der Berechnung.
     */
    ObjectWithType<?> apply(double leftOperand, double rightOperand);
}