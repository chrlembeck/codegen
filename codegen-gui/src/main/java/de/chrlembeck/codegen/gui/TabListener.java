package de.chrlembeck.codegen.gui;

import java.awt.Component;

/**
 * Listener für Zustandsänderungen an den im Editor verwalteten Fenstern.
 *
 * @author Christoph Lembeck
 */
public interface TabListener {

    /**
     * Wird aufgerufen, wenn das erste Editorfenster geöffnet wurde.
     * 
     * @param component
     *            Referenz auf das neue Fenster.
     */
    void firstTabOpened(TabComponent component);

    /**
     * Wird aufgerufen, wenn das letzte Editorfenster geschlossen wurde.
     * 
     * @param component
     *            Referenz auf das geschlossenen Editorfenseter.
     */
    void lastTabClosed(TabComponent component);

    /**
     * Wird aufgerufen, wenn ein anderen Editorfenster aktiviert wurde.
     * 
     * @param oldComponent
     *            Referenz auf das zuvor aktive Editorfenster oder null, wenn das erste Fesnter geöffnet wurde.
     * @param newComponent
     *            Referenz auf das jetzt aktive Editorfenster oder null, wenn das letzte Fenster geschlossen wurde.
     */
    void tabChanged(Component oldComponent, Component newComponent);

    /**
     * Wird aufgerufen, wenn ein Editorfenster geöffnet wurde.
     * 
     * @param component
     *            Referenz auf das neue Fenster.
     */
    void tabOpened(TabComponent component);

    /**
     * Wird aufgerufen, wenn ein Editorfenster geschlossen wurde.
     * 
     * @param component
     *            Refernez auf das geschlossene Fenster.
     */
    void tabClosed(TabComponent component);

    /**
     * Wird aufgerufen, wenn der Inhalt des Editorfensters gespeichert wurde.
     * 
     * @param component
     *            Referenz auf das Fenster, dessen Inhalt gespeichert wurde.
     */
    void tabSaved(TabComponent component);

    /**
     * Wird aufgerufen, wenn die erste nicht gespeicherte Änderung an einem Fenster durchgeführt wurde.
     * 
     * @param component
     *            Refernez auf das Fenster, das ab jetzt wieder ungespeicherte Änderungen enthält.
     */
    void tabContentHasUnsavedModifications(TabComponent component);
}