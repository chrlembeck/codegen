package de.chrlembeck.codegen.gui.util;

import java.util.Objects;
import java.util.function.Function;

/**
 * Hilfklasse zum Wrappen eines Objekts, so dass man ihm eine angepasste toString-Methode mitgeben kann. Kann z.B. zur
 * Anzeige von Objekten in Swing-Komponenten verwendet werden, welche Häufig die toString-Methode zur Darstellung
 * verwenden.
 * 
 * @author Christoph Lembeck.
 *
 * @param <O>
 *            Typ den in dem Wrapper enthaltenen Objekts.
 */
public class ToStringWrapper<O> {

    /**
     * Referenz auf das enthaltene Objekt.
     */
    private O object;

    /**
     * Methode, die Statt der toString-Methode des eingepackten Objekts ausgeführt werden soll.
     */
    private Function<O, String> toStringFunction;

    /**
     * Erstell einen neuen Wrapper für das Objekt.
     * 
     * @param object
     *            Zu kapseldnes Objekt.
     * @param toStringFunction
     *            Funktion, die statt der toString-Methode aufgerufen werden soll.
     */
    public ToStringWrapper(final O object, final Function<O, String> toStringFunction) {
        this.object = object;
        this.toStringFunction = Objects.requireNonNull(toStringFunction);
    }

    /**
     * Ruft die im Wrapper hinterlegte Funktion zur textuellen Darstellung des enthaltenen Objekts auf.
     * 
     * @see #toStringFunction
     */
    @Override
    public String toString() {
        return toStringFunction.apply(object);
    }

    /**
     * Gibt das enthaltene Objekt wieder zurück.
     * 
     * @return Das enthaltene Objekt.
     */
    public O getObject() {
        return object;
    }
}