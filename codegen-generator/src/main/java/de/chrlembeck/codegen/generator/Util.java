package de.chrlembeck.codegen.generator;

/**
 * Enthält praktische Hilfsmethoden, die aus den Templates heraus verwendet werden können.
 * 
 * @author Christoph Lembeck
 */
public class Util {

    /**
     * Verwandelt den ersten Buchstaben der übergebenen Zeichenkette in einen Großbuchstaben.
     * 
     * @param text
     *            Zeichenkette, deren erster Buchstabe umgewandelt werden soll.
     * @return Zeichenkette mit dem ersten Buchstaben als Großbuchstabe, null, falls die Eingabe null war oder eine
     *         leere Zeichenkette, falls die Zeichenkette vorher auch leer war.
     */
    public static String toFirstUpper(final String text) {
        return text == null ? null : text.isEmpty() ? "" : Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    /**
     * Verwandelt den ersten Buchstaben der übergebenen Zeichenkette in einen Kleinbuchstaben.
     * 
     * @param text
     *            Zeichenkette, deren erster Buchstabe umgewandelt werden soll.
     * @return Zeichenkette mit dem ersten Buchstaben als Kleinbuchstabe, null, falls die Eingabe null war oder eine
     *         leere Zeichenkette, falls die Zeichenkette vorher auch leer war.
     */
    public static String toFirstLower(final String text) {
        return text == null ? null : text.isEmpty() ? "" : Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }
}