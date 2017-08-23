package de.chrlembeck.antlr.editor;

import java.awt.Color;
import java.awt.Font;

/**
 * Legt das Aussehen eines Tokens im Editor fest. Aktuell können hierfür Farbe und Schriftschnitt (Schriftstärke und
 * Schriftlage) bestimmt werden.
 * 
 * @author Christoph Lembeck
 */
public class TokenStyle {

    /**
     * Farbe, in der das Token im Editor dargestellt werden soll.
     */
    private Color color;

    /**
     * Schriftschnitt, mit dem das Token gezeichnet werden soll.
     */
    private int fontStyle;

    /**
     * Legt eine neue Formatierung für Token mit Farbe und Schriftschnitt an.
     * 
     * @param color
     *            Farbe für die Darstellung des Tokens.
     * @param fontStyle
     *            Schriftschnitt für die Darstellung des Tokens.
     * @see Font#getStyle()
     * @see Font#PLAIN
     * @see Font#BOLD
     * @see Font#ITALIC
     */
    public TokenStyle(final Color color, final int fontStyle) {
        this.color = color;
        this.fontStyle = fontStyle;
    }

    /**
     * Gibt den für die Darstellung zu verwendenden Schriftschnitt zurück.
     * 
     * @return Schriftschnitt für die Darstellung des Tokens.
     * @see Font#getStyle()
     */
    public int getFontStyle() {
        return fontStyle;
    }

    /**
     * Gibt die für die Darstellung zu verwendende Farbe zurück.
     * 
     * @return Farbe, mit der das Token im Editor dargestellt werden soll.
     */
    public Color getColor() {
        return color;
    }
}