package de.chrlembeck.codegen.generator.output;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

/**
 * Standardimplementierung für die Einstellungen, die der Generator während der Aufgabe der zu erstellenden Artefakte
 * benötigt.
 * 
 * @author Christoph Lembeck
 */
public class BasicOutputPreferences implements OutputPreferences {

    /**
     * Standard-Ausgabe-Encoding (initial ist dies UTF-8). Wird verwendet, wenn für einzelne Channels kein anderes
     * Encoding hinterlegt ist.
     */
    private Charset defaultCharset = Charset.forName("UTF-8");

    /**
     * Individuelle Zuordnung von Channel-Namen zu den für sie zu verwendenden Encodings. Ohne Eintrag in dieser Map
     * wird das Default-Encoding verwendet.
     * 
     * @see #defaultCharset
     */
    private Map<String, Charset> charsets = new TreeMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Charset getCharsetForChannel(final String channelName) {
        final Charset charset = charsets.get(channelName);
        return charset == null ? getDefaultCharset() : charset;
    }

    /**
     * Ändert das Standard-Encoding für zu erstellende Dateien, zu deren Channel-Name kein anderes Encoding hinterlegt
     * ist.
     * 
     * @param defaultCharset
     *            Neues Encoding für alle Dateien, für die kein anderes Encoding hinterlegt ist.
     */
    public void setDefaultCharset(final Charset defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    /**
     * Gibt das Standard-Encoding für Dateien, zu denen kein anderes Encoding hinterlegt ist, zurück.
     * 
     * @return Standard-Encoding für alle Dateien ohne eigene Zuordnung.
     */
    public Charset getDefaultCharset() {
        return defaultCharset;
    }

    /**
     * Legt ein individuelles Encoding für einen einzelnen Channel-Namen fest. Die Festlegung muss vor der ersten
     * Verwendung des Channels getroffen werden.
     * 
     * @param channelName
     *            Name des Channel, zu dem ein eigenes Encoding hinterlegt werden soll.
     * @param charset
     *            Encoding für die angegebene Datei.
     */
    public void setCharsetForChannel(final String channelName, final Charset charset) {
        charsets.put(channelName, charset);
    }
}