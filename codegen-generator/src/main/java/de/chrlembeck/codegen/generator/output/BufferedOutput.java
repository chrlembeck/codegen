package de.chrlembeck.codegen.generator.output;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Ausgabeverwalter, der sämtliche zu generierenden Artefakte in StringWritern sammelt, so dass diese später als reine
 * Zeichenketten abgerufen werden können. Durch die Generierung werden keine Dateien auf der Festplatte abgelegt.
 * 
 * @author Christoph Lembeck
 */
public class BufferedOutput implements GeneratorOutput {

    /**
     * Zuordnung von Ausgabekanälen zu den StringWritern, in denen die Ausgaben gesammelt werden.
     */
    private Map<String, TextGeneratorWriter> writerMap = new TreeMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneratorWriter getWriter(final String channelName, final OutputPreferences prefs) {
        TextGeneratorWriter writer = writerMap.get(channelName);
        if (writer == null) {
            writer = new TextGeneratorWriter(new StringWriter());
            writerMap.put(channelName, writer);
        }
        return writer;
    }

    /**
     * Gibt eine Collection aller Namen der in dem Verwalter angesammelten Ausgabekanäle aus.
     * 
     * @return Liste aller Namen der Ausgabekanäle.
     */
    public Set<String> getChannelNames() {
        return writerMap.keySet();
    }

    /**
     * Gibt den Inhalt eines ausgewählten Ausgabekanals zurück.
     * 
     * @param channelName
     *            Name der Ausgabe, die angezeigt werden soll.
     * @return Inhalt der Ausgabe, so wie der Generator sie erzeugt hat.
     */
    public String getContent(final String channelName) {
        final TextGeneratorWriter writer = writerMap.get(channelName);
        return writer == null ? null : writer.getWriter().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public void closeAll() {
        for (final TextGeneratorWriter writer : writerMap.values()) {
            try {
                writer.close();
            } catch (final IOException e) {
                // StringWriters do not throw an IOException on close.
            }
        }
    }
}