package de.chrlembeck.codegen.generator.output;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
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
public class FileOutput<T extends GeneratorWriter> implements GeneratorOutput {

    /**
     * Der Logger für diese Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileOutput.class);

    private String suffix;

    /**
     * Root-Verzeichnis, unterhalb dessen alle Ausgaben erstellt werden.
     */
    private final Path rootPath;

    /**
     * Zuordnung von Channel-Namen zu den Writern, die die generierten Artefakte in Dateien schreiben.
     */
    private Map<String, T> writers = new TreeMap<>();

    private GeneratorWriterCreator<T> generatorWriterSupplier;

    public static FileOutput<TextGeneratorWriter> simpleTextOutput(final Path rootPath) {
        return new FileOutput<TextGeneratorWriter>(rootPath,
                (writer, channelName, path) -> new TextGeneratorWriter(writer));
    }

    /**
     * Erstellt einen neuen Verwalter mit dem angegebenen Verzeichnis als root-Verzeichnis.
     * 
     * @param rootPath
     *            Verzeichnis unterhalb dessen die Dateien des Generators abgelegt werden sollen.
     */
    public FileOutput(final Path rootPath, final GeneratorWriterCreator<T> generatorWriterSupplier) {
        this.rootPath = Objects.requireNonNull(rootPath);
        this.generatorWriterSupplier = generatorWriterSupplier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneratorWriter getWriter(final String channelName, final OutputPreferences prefs) throws IOException {
        T writer = writers.get(channelName);
        if (writer == null) {
            final Path path = getPathFromChannel(channelName);
            final Path directory = path.getParent();
            if (!directory.toFile().exists()) {
                directory.toFile().mkdirs();
            }
            if (path.toFile().exists() && keepExisting(channelName, prefs, path)) {
                writer = generatorWriterSupplier.createWriter(new NullWriter(), channelName, null);
            } else {
                final FileOutputStream fileOut = new FileOutputStream(path.toFile());
                final BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
                writer = generatorWriterSupplier
                        .createWriter(new OutputStreamWriter(bufOut, prefs.getCharsetForChannel(channelName)),
                                channelName, path);
            }
            writers.put(channelName, writer);
        }
        return writer;
    }

    /**
     * Prüft, ob die bestehende Datei ersetzt oder behalten werden soll.
     * 
     * @param channelName
     *            Name des Kanals, in den geschrieben werden soll.
     * @param prefs
     *            Einstellungen für die Ausgabe des Generators.
     * @param path
     *            Pfad auf die zu ersetzende Datei
     * @return {@code true} falls die Datei bestehen bleiben soll, {@code false} falls die Datei ersetzt werden soll.
     * @throws FileAlreadyExistsException
     *             Falls die Einstellungen beim Antreffen einer bestehenden Datei eine Exception erwarten.
     */
    private boolean keepExisting(final String channelName, final OutputPreferences prefs, final Path path)
            throws FileAlreadyExistsException {
        final OverwritePreferences overwritePreferences = prefs.getOverwritePreferencesForChannel(channelName);
        if (overwritePreferences == OverwritePreferences.THROW_EXCEPTION) {
            final String message = "Die zu schreibende Datei existiert bereits und soll nicht überschrieben werden: "
                    + path;
            LOGGER.error(message);
            throw new FileAlreadyExistsException(
                    message);
        } else if (overwritePreferences == OverwritePreferences.KEEP_EXISTING) {
            LOGGER.warn("Die Ausgabedatei existiert bereits und wird aufgrund der Einstellung "
                    + overwritePreferences + " nicht verändert: " + path);
            return true;
        }
        return false;
    }

    /**
     * Ermittelt aus dem Channel-Namen den Pfad zu der zu erstellenden Datei.
     * 
     * @param channelName
     *            Name des Ausgabe-Channels.
     * @return Pfad zu der Datei für den Ausgabe-Chanel.
     */
    private Path getPathFromChannel(final String channelName) {
        return rootPath.resolve(channelName + (suffix == null ? "" : suffix));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeAll() {
        for (final GeneratorWriter writer : writers.values()) {
            try {
                writer.close();
            } catch (final IOException e) {
                LOGGER.error("Writer konnte nicht geschlossen werden.", e);
            }
        }
    }

    /**
     * Hilfsklasse für das Verhalten des Generators, wenn er eine existierende Datei nicht überschreiben soll.
     * 
     * @author Christoph Lembeck
     */
    static class NullWriter extends Writer {

        /**
         * Funktionslos.
         */
        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            // do nothing
        }

        /**
         * Funktionslos.
         */
        @Override
        public void flush() throws IOException {
            // do nothing
        }

        /**
         * Funktionslos.
         */
        @Override
        public void close() throws IOException {
            // do nothing
        }
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
}