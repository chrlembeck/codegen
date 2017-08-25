package de.chrlembeck.codegen.gui;

/**
 * Informiert über ungespeicherte Änderungen an einem geladenen Template.
 *
 * @author Christoph Lembeck
 */
public interface ModificationListener {

    /**
     * Wird einmalig aufgerufen, wenn nach dem Neuerstellen, Laden oder Speichern eines Dokumentes ungespeicherte
     * Änderungen an dem Dokument vorgenommen werden.
     * 
     * @param templatePanel
     *            Template, an dem de Änderungen vorgenommen wurden.
     */
    public void documentWasModified(TemplatePanel templatePanel);
}