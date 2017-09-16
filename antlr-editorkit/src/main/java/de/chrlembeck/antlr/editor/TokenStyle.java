package de.chrlembeck.antlr.editor;

import java.awt.Color;
import java.awt.Font;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

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
    private final Color color;

    /**
     * Schriftschnitt, mit dem das Token gezeichnet werden soll.
     */
    private final int fontStyle;

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

    /**
     * Gibt eine CSS-Style-Definition für die Darstellung von Tokens dieses Typs zurück.
     * 
     * @return CSS-Style-Definition für dieses Token.
     */
    public String toCSS() {
        final NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        format.setMinimumIntegerDigits(1);
        format.setMinimumFractionDigits(3);
        format.setMaximumFractionDigits(3);
        format.setRoundingMode(RoundingMode.HALF_EVEN);
        final StringBuilder sb = new StringBuilder();
        sb.append("color: rgba(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", "
                + format.format(color.getAlpha() / 255d) + ");");
        sb.append(" font-style: ");
        sb.append((fontStyle & Font.ITALIC) > 0 ? "italic" : "normal");
        sb.append("; font-weight: ");
        sb.append((fontStyle & Font.BOLD) > 0 ? "bold" : "normal");
        sb.append(";");
        return sb.toString();
    }
}