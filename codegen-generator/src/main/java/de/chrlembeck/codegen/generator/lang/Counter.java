package de.chrlembeck.codegen.generator.lang;

/**
 * Hilfsobjekt, welches dem Template bei der Ausführung von Schleifen zur Verfügung gestellt wird. Die Methoden dieser
 * Klasse können innerhalb von Schleifen dazu verwendet werden, auszulesen, an welcher Stelle innerhalb einer Schleife
 * sich eine Ausführung gerade befindet.
 * 
 * @author Christoph Lembeck
 */
public class Counter {

    /**
     * Index der Ausführung. Der erste Schleifendurchlauf hat den Index 0.
     */
    private long index;

    /**
     * Gibt an, ob es der erste Schleifendurchlauf ist.
     */
    private boolean first;

    /**
     * Gib an, ob dies der letzte Schleifendurchlauf ist.
     */
    private boolean last;

    /**
     * Erstellt ein neues Hilfspobjekt mit den übergebenen Daten.
     * 
     * @param index
     *            Index des Schleifendurchlaufs beginnend mit 0.
     * @param first
     *            true= erster Durchlauf, sonst false.
     * @param last
     *            true = letzter Schleifendurchlauf, sonst false.
     */
    Counter(final long index, final boolean first, final boolean last) {
        this.index = index;
        this.first = first;
        this.last = last;
    }

    /**
     * Gibt den Index des Schleifendurchlaufs beginnend mit 0 zurück.
     * 
     * @return Index des aktuellen Schleifendurchlaufs.
     */
    public long getIndex() {
        return index;
    }

    /**
     * Gibt an, ob man sich in einer geraden Anzahl von Schleifendurchläufen befindet.
     * 
     * @return true = gerade Anzahl an bisherigen Durchläufen, false = ungerade Anhzahl an bisherigen Durchläufen.
     */
    public boolean isOdd() {
        return (index & 1) == 1;
    }

    /**
     * Gibt an, ob man sich in einer ungeraden Anzahl von Schleifendurchläufen befindet.
     * 
     * @return true = ungerade Anzahl an bisherigen Durchläufen, false = gerade Anhzahl an bisherigen Durchläufen.
     */
    public boolean isEven() {
        return (index & 1) == 0;
    }

    /**
     * Gibt an, ob dies der erste Schleifendurchlauf ist.
     * 
     * @return true = dies ist der erste Schleifendurchlauf, sonst false.
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * Gibt an, ob es sich um den letzten Schleifendurchlauf handelt.
     * 
     * @return true = dies ist der letzte Schleifendurchlauf, sonst false.
     */
    public boolean isLast() {
        return last;
    }
}