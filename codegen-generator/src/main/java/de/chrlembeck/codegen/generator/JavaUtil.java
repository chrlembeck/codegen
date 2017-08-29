package de.chrlembeck.codegen.generator;

import de.chrlembeck.codegen.generator.lang.ObjectWithType;
import de.chrlembeck.codegen.generator.lang.PrimitiveOperations;

/**
 * Enthält Hilfsmethoden für den Umgamg mit den Java-Datentypen während der Auswertung der Java-ähnlichen Elemente einer
 * Template-Datei.
 * 
 * @author Christoph Lembeck
 */
public class JavaUtil {

    /**
     * Führt eine unary numeric promotion gemäß java language specification durch. Dabei werden zunächst die numerischen
     * Wrapper-Typen in ihre primitiven Datentypen umgewandelt und danach eine widening primitive conversion auf die
     * Typen int, long, float und double vorgenommen.
     * <p>
     * Siehe auch: <a href=
     * "https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.6.1">https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.6.1</a>
     * </p>
     * 
     * @param owt
     *            Objekt, auf dem die Konvertierung voregnommen werden soll.
     * @return Ergebnis der numerischen Umwandlung.
     */
    public static final ObjectWithType<?> unaryNumericPromotion(final ObjectWithType<?> owt) {
        if (Byte.class.isAssignableFrom(owt.getType())
                || Short.class.isAssignableFrom(owt.getType())
                || Character.class.isAssignableFrom(owt.getType())
                || byte.class.isAssignableFrom(owt.getType())
                || short.class.isAssignableFrom(owt.getType())
                || char.class.isAssignableFrom(owt.getType())) {
            return new ObjectWithType<Integer>(((Number) owt.getObject()).intValue(), int.class);
        } else if (Float.class.isAssignableFrom(owt.getType())) {
            return new ObjectWithType<Float>((Float) owt.getObject(), float.class);
        } else if (Long.class.isAssignableFrom(owt.getType())) {
            return new ObjectWithType<Long>((Long) owt.getObject(), long.class);
        } else if (Double.class.isAssignableFrom(owt.getType())) {
            return new ObjectWithType<Double>((Double) owt.getObject(), double.class);
        } else if (int.class.isAssignableFrom(owt.getType()) || long.class.isAssignableFrom(owt.getType())
                || double.class.isAssignableFrom(owt.getType()) || float.class.isAssignableFrom(owt.getType())) {
            return owt;
        } else {
            throw new IllegalStateException("unexpected type " + owt);
        }
    }

    /**
     * Prüft, ob der übergebene Typ den Typen int, long, Integer oder Long entspricht. Eine erweiternde Konvertierung
     * des Typs wird nicht vorgenommen.
     * 
     * @param type
     *            Zu prüfender Typ.
     * @return true, falls der Typ int, long, Integer oder Long ist, sonst false.
     */
    public static boolean isIntegerOrLongType(final Class<?> type) {
        return int.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)
                || Integer.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type);
    }

    /**
     * Führt einen numerischen Binäroperator auf den beiden übergebenen Operanden aus. Die beiden Operanden werden dafür
     * gemäß Regeln der java languae specification aneinander angepasst.
     * <p>
     * Siehe auch z.B.: <a href=
     * "https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.6.2">https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.6.2</a>
     * </p>
     * 
     * @param left
     *            Erster Operand der Berechnung.
     * @param right
     *            Zweiter Operand der Berechnung.
     * @param operator
     *            Operator, der auf den beiden Operanden auszuführen ist.
     * @return Wert der Berechnung abhängig vom gewählten Operator.
     */
    public static final ObjectWithType<?> applyBinaryOperation(final ObjectWithType<?> left,
            final ObjectWithType<?> right,
            final PrimitiveOperations operator) {
        final Number leftNum = (Number) left.getObject();
        final Number rightNum = (Number) right.getObject();
        final Class<?> promotionType = JavaUtil.getBinaryNumericPromotionType(left.getType(), right.getType());
        if (int.class.isAssignableFrom(promotionType)) {
            return operator.apply(leftNum.intValue(), rightNum.intValue());
        }
        if (long.class.isAssignableFrom(promotionType)) {
            return operator.apply(leftNum.longValue(), rightNum.longValue());
        }
        if (float.class.isAssignableFrom(promotionType)) {
            return operator.apply(leftNum.floatValue(), rightNum.floatValue());
        }
        if (double.class.isAssignableFrom(promotionType)) {
            return operator.apply(leftNum.doubleValue(), rightNum.doubleValue());
        }
        throw new RuntimeException("I don't know how to compare a " + left + " to a " + right + ".");
    }

    /**
     * Führt eine binary numeric promotion zweier miteinander zu verrechnenden numerischer Typen gemäß Regeln der java
     * language specification durch. Wrapper-Typen werden dabei in ihre primitiven Typen überführt.
     * <p>
     * <a href=
     * "https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.6.2">https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.6.2</a>
     * </p>
     * 
     * @param leftType
     *            Typ des ersten Operanden.
     * @param rightType
     *            Typ des zweiten Operanden.
     * @return Ergebnis der Typanpassung
     */
    public static Class<? extends Number> getBinaryNumericPromotionType(final Class<?> leftType,
            final Class<?> rightType) {
        if (leftType.equals(Double.class) || rightType.equals(Double.class) || leftType.equals(double.class)
                || rightType.equals(double.class)) {
            return double.class;
        }
        if (leftType.equals(Float.class) || rightType.equals(Float.class) || leftType.equals(float.class)
                || rightType.equals(float.class)) {
            return float.class;
        }
        if (leftType.equals(Long.class) || rightType.equals(Long.class) || leftType.equals(long.class)
                || rightType.equals(long.class)) {
            return long.class;
        }
        if (leftType.equals(Integer.class) || rightType.equals(Integer.class) || leftType.equals(int.class)
                || rightType.equals(int.class)) {
            return int.class;
        }
        throw new IllegalStateException(leftType.getName() + ":" + rightType.getName());
    }

    /**
     * Prüft, ob der übergebene Typ boolean oder Boolean ist.
     * 
     * @param type
     *            Zu prüfender Typ.
     * @return true, falls der Typ boolean oder Boolean ist, sonst false.
     */
    public static boolean isBooleanType(final Class<?> type) {
        return boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type);
    }

    /**
     * Prüft ob der übergebene Typ ein ganzzahliger numerischer typ, also byte, short, char, int, long oder einer der
     * dazugehörigen Wrapper-Typen ist.
     * 
     * @param type
     *            Zu prüfender Typ.
     * @return true, falls der Typ byte, short, char, int, long oder einer ihrer Wrapper-Typen ist.
     */
    public static boolean isIntegralNumericType(final Class<?> type) {
        return byte.class.isAssignableFrom(type) ||
                Byte.class.isAssignableFrom(type) ||
                short.class.isAssignableFrom(type) ||
                Short.class.isAssignableFrom(type) ||
                char.class.isAssignableFrom(type) ||
                Character.class.isAssignableFrom(type) ||
                int.class.isAssignableFrom(type) ||
                Integer.class.isAssignableFrom(type) ||
                long.class.isAssignableFrom(type) ||
                Long.class.isAssignableFrom(type);
    }

    /**
     * Prüft, ob der übergebene Typ numerisch (ganzzahlig oder mit Fließkomma) ist.
     * 
     * @param type
     *            Zu prüfender Typ.
     * @return true, falls der Typ numerisch ist, sonst false.
     */
    public static boolean isNumericType(final Class<?> type) {
        return isIntegralNumericType(type) || isFloatingPointType(type);
    }

    /**
     * Prüft, ob der übergebene Typ ein Flißkommazahlentyp, also float, Float, double oder Double ist.
     * 
     * @param type
     *            Zu prüfender Typ.
     * @return true, falls der Typ float, Float, double oder Double ist.
     */
    private static boolean isFloatingPointType(final Class<?> type) {
        return float.class.isAssignableFrom(type) ||
                Float.class.isAssignableFrom(type) ||
                double.class.isAssignableFrom(type) ||
                Double.class.isAssignableFrom(type);
    }

    /**
     * Ermittelt zu einem primitiven Datentyp seinen Wrapper-Typ, also z.B. zu int Integer.
     * 
     * @param type
     *            Zu konvertierender primitiver Typ.
     * @return Wrapper des primitiven Datentyps.
     * @throws IllegalArgumentException
     *             Falls der übergebene Datentyp kein primitiver Typ oder null war.
     */
    public static Class<?> getWrapperClass(final Class<?> type) {
        if (byte.class.isAssignableFrom(type)) {
            return Byte.class;
        } else if (char.class.isAssignableFrom(type)) {
            return Character.class;
        } else if (short.class.isAssignableFrom(type)) {
            return Short.class;
        } else if (boolean.class.isAssignableFrom(type)) {
            return Boolean.class;
        } else if (int.class.isAssignableFrom(type)) {
            return Integer.class;
        } else if (float.class.isAssignableFrom(type)) {
            return Float.class;
        } else if (long.class.isAssignableFrom(type)) {
            return Long.class;
        } else if (double.class.isAssignableFrom(type)) {
            return Double.class;
        } else
            throw new IllegalArgumentException("Only primitive types do have a wrapper class.");
    }

    /**
     * Prüft ob der übergebene Typ vom in, long, float, double oder Number ist.
     * 
     * @param type
     *            Zu prüfender Typ.
     * @return true, falls die obige Annahme stimmt, sonst false. TODO prüfen, ob dieser Test so sinnvoll ist oder ob er
     *         nicht entfernt werden kann.
     */
    public static boolean isNumberType(final Class<?> type) {
        return (Number.class.isAssignableFrom(type)) || (type.equals(int.class)) || (type.equals(long.class))
                || (type.equals(float.class)) || (type.equals(double.class));
    }

    /**
     * Prüft, ob der übergebene Typ den Typen int, oder Integer entspricht. Eine erweiternde Konvertierung des Typs wird
     * nicht vorgenommen.
     * 
     * @param type
     *            Zu prüfender Typ.
     * @return true, falls der Typ int, oder Integer ist, sonst false.
     */
    public static boolean isIntegerType(final Class<?> type) {
        return int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type);
    }
}