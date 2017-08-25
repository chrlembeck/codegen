package de.chrlembeck.codegen.gui;

import org.antlr.v4.runtime.Token;

import de.chrlembeck.codegen.generator.Position;

/**
 * Informiert über die neue Position des Cursors innerhalb eines Editor-Fensters.
 *
 * @author Christoph Lembeck
 */
public class CaretPositionChangeEvent {

    /**
     * Position innerhab des Editor, an dem sich der Cursor jetzt befindet.
     */
    private Position position;

    /**
     * Token, über dem der Cursor gerade steht.
     */
    private Token token;

    /**
     * Fehlermeldung, die zu dem Token unter dem Cursor hinterlegt ist oder null, wenn das Token keine Meldung besitzt.
     */
    private String message;

    /**
     * Erstellt ein neues Event mit den übergebenen Daten.
     * 
     * @param pos
     *            Position innerhab des Editor, an dem sich der Cursor jetzt befindet.
     * @param token
     *            Token, über dem der Cursor gerade steht.
     * @param message
     *            Fehlermeldung, die zu dem Token unter dem Cursor hinterlegt ist oder null, wenn das Token keine
     *            Meldung besitzt.
     */
    public CaretPositionChangeEvent(final Position pos, final Token token, final String message) {
        this.position = pos;
        this.token = token;
        this.message = message;
    }

    /**
     * Gibt die Position des Cursors innerhab des Dokuments zurück.
     * 
     * @return Neue Position des Cursors.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gibt das Token zurück, an dem sich der Cursor gerade befindet.
     * 
     * @return Token an der Stelle des Cursors.
     */
    public Token getToken() {
        return token;
    }

    /**
     * Gibt eine zu dem Token unter den Cursor hinterlegte Meldung zurück.
     * 
     * @return Fehlermeldung, die zu dem Token unter dem Cursor hinterlegt ist oder null, wenn das Token keine Meldung
     *         besitzt.
     */
    public String getMessage() {
        return message;
    }
}