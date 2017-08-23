package de.chrlembeck.codegen.generator.output;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ausgabeverwalter zum Schreiben der generierten Artefakte auf die Festplatte. Es wird ein root-Verzeichnis angegeben,
 * unter dem die Ausgaben erstellt werden. Die Jeweiligen Dateinamen ergeben sich aus den Namen der Ausgabekanäle. Aus
 * de/test/Foo.java wird so eine Datei im Verzeichnis de/test unterhalb des root-Verzeichnis mit dem Namen Foo.java.
 *
 * @author Christoph Lembeck
 */
public class FileOutput implements GeneratorOutput {

    /**
     * Der Logger für diese Klasse.
     */
    private static Logger LOGGER = LoggerFactory.getLogger(FileOutput.class);

    /**
     * Root-Verzeichnis, unterhalb dessen alle Ausgaben erstellt werden.
     */
    private final Path rootPath;

    /**
     * Zuordnung von Channel-Namen zu den Writern, die die generierten Artefakte in Dateien schreiben.
     */
    private Map<String, Writer> writers = new TreeMap<>();

    /**
     * Erstellt einen neuen Verwalter mit dem angegebenen Verzeichnis als root-Verzeichnis.
     * 
     * @param rootPath
     *            Verzeichnis unterhalb dessen die Dateien des Generators abgelegt werden sollen.
     */
    public FileOutput(final Path rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Writer getWriter(final String channelName, final OutputPreferences prefs) throws IOException {
        Writer writer = writers.get(channelName);
        if (writer == null) {
            final Path path = getPathFromChannel(channelName);
            final Path directory = path.getParent();
            if (!directory.toFile().exists()) {
                directory.toFile().mkdirs();
            }
            final FileOutputStream fileOut = new FileOutputStream(path.toFile());
            final BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
            writer = new OutputStreamWriter(bufOut, prefs.getCharsetForChannel(channelName));
            writers.put(channelName, writer);
        }
        return writer;
    }

    /**
     * Ermittelt aus dem Channel-Namen den Pfad zu der zu erstellenden Datei.
     * 
     * @param channelName
     *            Name des Ausgabe-Channels.
     * @return Pfad zu der Datei für den Ausgabe-Chanel.
     */
    private Path getPathFromChannel(final String channelName) {
        return rootPath.resolve(channelName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeAll() {
        for (final Writer writer : writers.values()) {
            try {
                writer.close();
            } catch (final IOException e) {
                LOGGER.error("Writer konnte nicht geschlossen werden.", e);
            }
        }
    }
}