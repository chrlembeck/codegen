package de.chrlembeck.codegen.generator;

import java.util.Map;
import java.util.TreeMap;

import de.chrlembeck.codegen.generator.lang.ObjectWithType;

/**
 * Repräsentiert einen Frame, der Zustandsinformationen zum aktuell ausgeführten Template speichert
 *
 * @author Christoph Lembeck
 */
class Frame {

    /**
     * Verknüpft im Frame definierte Variablennamen mit den ihnen zugewiesenen Werten.
     */
    private final Map<String, ObjectWithType<?>> variables = new TreeMap<>();

    /**
     * Verknüpfung des Frames mit dem vorangegangenen Frame, falls dieser Frame auf die Daten des Vorgängers Zugriff
     * haben soll.
     */
    private final Frame parent;

    /**
     * Erstell einen neuen Frame und hinterlegt die Referenz auf den Vorgänger.
     * 
     * @param parent
     *            Optionale Referenz auf den vorangegangenen Frame.
     */
    public Frame(final Frame parent) {
        this.parent = parent;
    }

    /**
     * Gibt, falls vorhanden, die Referenz auf einen vorangegangenen Frame zurück. Frames, die keinen Zugriff auf ihre
     * Vorgänger haben, geben hier null zurück.
     * 
     * @return Vorgänger des Frames, falls vorhanden und Zugriff darauf erlaubt.
     */
    public Frame getParent() {
        return parent;
    }

    /**
     * Setzt in dem aktuellen Frame einen neuen Wert für die übergebene Variable. Alle Statements, die später auf dem
     * gleichen (oder erbenden) Frame ausgeführt werden, können den Wert dieser Variable nutzen.
     * 
     * @param varName
     *            Name der Variable.
     * @param item
     *            Wert der Variable.
     * @param type
     *            Laufzeittyp der Variable. Dieser ist besonders bei primitiven Datentypen wichtig, da er nicht aus den
     *            Wrapper-Klassen abgeleitet werden kann.
     */
    public void addVariable(final String varName, final Object item, final Class<?> type) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        final ObjectWithType<?> owt = new ObjectWithType(item, type);
        variables.put(varName, owt);
    }

    /**
     * Prüft, ob die übergebene Variable in dem aktuellen Frame bekannt ist.
     * 
     * @param name
     *            Name der zu suchenden Variablen.
     * @return true, falls der Frame die Variable kennt, false, falls die Variable unbekannt ist.
     */
    public boolean containsVariable(final String name) {
        return variables.containsKey(name) || (parent != null && parent.containsVariable(name));
    }

    /**
     * Sucht im aktuellen Frame nach der übergebenen Variable und gibt bei Erfolg den zu ihr gespeicherten Wert zurück.
     * Hat der Frame Zugriff auf die Variablen des Vorgängerframes, wird auch dort gesucht.
     * 
     * @param varName
     *            Name der zu suchenden Variable.
     * @return Wert der Variable, falls diese im Frame oder seinen Vorgängern bekannt ist.
     */
    public ObjectWithType<?> lookupVariable(final String varName) {
        if (variables.containsKey(varName)) {
            return variables.get(varName);
        } else if (parent == null) {
            throw new RuntimeException("Variable '" + varName + "' could not be found.");
        } else {
            return parent.lookupVariable(varName);
        }
    }
}