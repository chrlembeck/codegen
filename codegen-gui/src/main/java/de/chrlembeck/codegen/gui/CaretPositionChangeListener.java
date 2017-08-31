package de.chrlembeck.codegen.gui;

/**
 * Informiert über Veränderungen der Cursor-Positon innerhalb eines Editor-Fensters.
 * 
 * @author Christoph Lembeck
 */
public interface CaretPositionChangeListener {

    /**
     * Wird aufgerufen, wenn sich die Cursorposition innerhalb des Editorfensters verändert hat.
     * 
     * @param event
     *            Event, welches die aktuelle Position des Cursors mit Zusazuinformationen beschreibt.
     */
    void caretPositionChanged(CaretPositionChangeEvent event);
}