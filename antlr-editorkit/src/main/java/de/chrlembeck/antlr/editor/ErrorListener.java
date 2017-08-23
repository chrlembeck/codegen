package de.chrlembeck.antlr.editor;

import java.util.Map;

import org.antlr.v4.runtime.Token;

/**
 * ErrorListener können dazu verwendet werden, sich über die Fehler beim Lexen oder Parsen des im Editor befindlichen
 * Dokumentes informieren zu lassen.
 * 
 * @author Christoph Lembeck
 * @see AntlrDocument#addErrorListener(ErrorListener)
 */
public interface ErrorListener {

    /**
     * Wird aufgerufen, wenn bei einer Validierung innerhalb des Editors neue Fehler innerhalb des Dokuments gefunden
     * wurden.
     * 
     * @param errors
     *            Liste der aktuell im Dokument erkannten Fehler. Die Liste enthält die Token, bei denen Probleme
     *            erkannt wurden zusammen mit einer entsprechenden Beschreibung des Problems.
     */
    public void errorsChanged(Map<Token, String> errors);
}