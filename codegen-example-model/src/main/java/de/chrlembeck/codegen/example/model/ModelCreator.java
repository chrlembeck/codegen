package de.chrlembeck.codegen.example.model;

import java.util.Arrays;

/**
 * Hilfsklasse zur Erzeugung einiger Test-Modelle, aus denen mit den Templates aus dem Beispielprojekt verschiedene
 * Artefakte generiert werden können.
 * 
 * @author Christoph Lembeck
 *
 */
public class ModelCreator {

    /**
     * Erzeugt eine Liste von Strings zur Verwendung in einigen Test-Templates.
     * 
     * @return Liste von Strings.
     */
    public Object getListOfStrings() {
        return Arrays.asList("Hello", "Test", "Example");
    }
}