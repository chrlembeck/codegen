package de.chrlembeck.codegen.generator.output;

import java.nio.charset.Charset;

/**
 * Definiert Einstellungen, die vom Generator während des Erzeugens von Artefkaten berücksichtigt werden.
 * 
 * @author Christoph Lembeck
 */
public interface OutputPreferences {

    /**
     * Legt fest, mit welchem Encoding ein bestimmter channel erzeugt werden soll.
     * 
     * @param channelName
     *            Name des Channels, dessen Encoding bestimmt werden soll.
     * @return Encoding, mit dem die Daten in den Channel geschrieben werden sollen.
     */
    Charset getCharsetForChannel(final String channelName);

    /**
     * Gibt zurück, wie der Generator sich verhalten soll, wenn er beim Generieren einer Datei zu einem Ausgabekanal
     * eine vorhandene Datei überschreiben soll.
     * 
     * @param channelName
     *            Name des Ausgabekanals, zu dem die Datei erstellt werden soll.
     * @return Gewünschtes Verhalten des Generators für diesen Kanal.
     * @see OverwritePreferences
     */
    OverwritePreferences getOverwritePreferencesForChannel(String channelName);
}