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
    public Charset getCharsetForChannel(final String channelName);
}