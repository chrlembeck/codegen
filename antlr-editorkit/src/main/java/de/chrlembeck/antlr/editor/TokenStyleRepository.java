package de.chrlembeck.antlr.editor;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.v4.runtime.Token;

/**
 * Beeinhaltet das Mapping von einzelnen Tokentypen zu deren Darstellungsweise im Editor. Über diese Klasse kann das
 * Aussehen der einzelnen Token im Editor gesteuert werden. Für jeden Tokentyp kann hier hinterlegt werden, wie dieser
 * später darzustellen ist. So können z.B. bestimmte Schlüsselwörter einer Sprache farbig und/oder fett oder kursiv
 * dargestellt werden.
 * 
 * @author Christoph Lembeck
 */
public class TokenStyleRepository {

    /**
     * Darstellungsform von Token, für die kein spezieller Style definiert ist.
     */
    public static final TokenStyle DEFAULT = new TokenStyle(Color.BLACK, Font.PLAIN);

    /**
     * Darstellungsform von Text, zu dem kein Token ermittelt werden kann. (Dies sollte bei guten Lexern nicht
     * auftreten.)
     */
    public static final TokenStyle NO_TOKEN_STYLE = new TokenStyle(Color.GRAY, Font.PLAIN);

    /**
     * Beinhaltet die Zuordnung von Token-Typen zu deren Darstellungsform.
     */
    private final Map<Integer, TokenStyle> styles = new TreeMap<>();

    /**
     * Gibt die Darstellungsform zu dem übergebenen Token anhand dessen Typ zurück. Ist für den Tokentyp keine spezielle
     * Darstellung hinterlegt, wird die Standarddarstellungsweise zurückgegeben.
     * 
     * @param token
     *            Token, zu dem die Darstellungsweise bestimmt werden soll.
     * @return Stil, in dem das Token gezeichnet werden soll.
     * @see TokenStyleRepository#DEFAULT
     */
    public TokenStyle getStyle(final Token token) {
        final TokenStyle style = styles.get(token.getType());
        return style == null ? getDefaultStyle() : style;
    }

    /**
     * Gibt die Darstellungsform von Token zurück, für die kein spezieller Darstellungsstil hinterlegt wurde.
     * 
     * @return Standarddarstellungsform.
     * @see #DEFAULT
     */
    public TokenStyle getDefaultStyle() {
        return DEFAULT;
    }

    /**
     * Gibt den Stil für Text zurück, der keinem Token zugeordnet werden kann. Bei Lexern, die für jedes Zeichen ein
     * Token zurückgeben, wird dies nicht benötigt.
     * 
     * @return Stil für Text, der keinem Token zugeordnet werden kann.
     */
    public TokenStyle getNoTokenStyle() {
        return NO_TOKEN_STYLE;
    }

    /**
     * Legt die Darstellungsweise für einen Tokentyp fest. Ist für diesen Typ bereits eine Darstellungsform hinterlegt,
     * wird diese durch die neue Definition ersetzt. Die Übergabe von {@code null} für einen Stil führt zum löschen des
     * Eintrags.
     * 
     * @param tokenType
     *            Tokentyp, für den die Darstellungsform definiert werden soll.
     * @param style
     *            Neue Darstellungsform für den Tokentyp oder null, falls die bisherige Definition gelöscht werden soll.
     * @see Token#getType()
     */
    public void putStyle(final int tokenType, final TokenStyle style) {
        if (style == null) {
            styles.remove(tokenType);
        } else {
            styles.put(tokenType, style);
        }
    }
}