package de.chrlembeck.antlr.editor;

import java.util.List;

import org.antlr.v4.runtime.Token;

/**
 * Enthält Hilfsmethoden zum Umgang mit Token.
 * 
 * @author Christoph Lembeck
 */
public class TokenUtil {

    /**
     * Sucht innerhalb einer nach auftreten in einem Template sortierten Listen von Token das Token heraus, welches an
     * der übergebenen Position zu finden ist. Die Position ist dabei der absolute Indes eines Zeichens in der Datei
     * beginnend bei 0. Die Liste der Token muss vollständig sein, d.h. für jede Position innerhalb der Datei muss ein
     * Token zu finden sein. Dies bedeutet, dass durch den Lexer auch Whitespace und nicht erkannte Token in die Liste
     * mit aufgenommen werden müssen.
     * 
     * @param list
     *            Sortierte, vollständige Liste von Token.
     * @param position
     *            Position des Zeichens in der Datei, zu dem das entsprechende Token gefunden werden soll.
     * @return Index des Tokens, das die gesuchte Zeichenposition beinhaltet.
     */
    public static int findTokenIndex(final List<? extends Token> list, final int position) {
        int left = 0;
        int right = list.size() - 1;
        Token currentToken = null;
        while (left <= right) {
            final int center = (left + right) >>> 1;
            currentToken = list.get(center);
            if (currentToken.getStartIndex() < position) {
                left = center + 1;
            } else if (currentToken.getStartIndex() > position) {
                right = center - 1;
            } else {
                return center; // Das Token startet genau an der Position
            }
        }
        if (currentToken.getStopIndex() >= position)
            return left - 1;
        else
            return left;
    }
}