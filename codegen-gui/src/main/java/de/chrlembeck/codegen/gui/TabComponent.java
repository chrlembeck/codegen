package de.chrlembeck.codegen.gui;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * Interface für alle Reiterinhalte, die in der TabbedPane der Editoren angezeigt werden können.
 *
 * @author Christoph Lembeck
 */
public interface TabComponent {

    /**
     * Gibt an, ob das Dokument in dem Tab neu ist und noch nie gespeichert wurde.
     * 
     * @return true, falls das Dokument neu ist, sonst false
     */
    public boolean isNewArtifact();

    /**
     * Gibt an, ob das Dokument in dem Tab ungespeicherte Änderungen enthält.
     * 
     * @return true, falls das Dokument noch nicht gespeicherte Änderungen enthält, sonst false.
     */
    boolean hasUnsavedModifications();

    /**
     * Speichert das in dem Tab enthaltene Dokument mit dem übergebenen Encoding in die übergebene Datei.
     * 
     * @param path
     *            Pfad zu der zu schreibenden Datei.
     * @param charset
     *            Encoding, mit der das Dokument geschrieben werden soll.
     * @return {@code true} falls das Speichern erfolgreicht war, sonst {@code false}.
     * @throws IOException
     *             Falls beim Schreiben ein Problem auftritt.
     */
    public boolean saveDocument(Path path, Charset charset) throws IOException;

    /**
     * Gibt den Pfad zu der Datei zurück, falls diese gelesen oder schon einmal gespechert wurde.
     * 
     * @return Pfad zu der Datei, aus der das Dokument zuletzt gelesen oder in die es zuletzt gespeichert wurde.
     */
    public Path getPath();

    /**
     * Fügt den übergebenen Listener zu der Liste der Listener hinzu, die über Änderungen an dem Dokument informiert
     * werden sollen.
     * 
     * @param listener
     *            Neuer Listener für Änderungen an dem Dokument.
     */
    public void addModificationListener(ModificationListener listener);

    /**
     * Gibt das Encoding der in dem Dokument enthaltenen Datei zurück, falls diese aus einer Datei geladen oder bereits
     * in eine Datei gespeichert wurde.
     * 
     * @return Encoding der in dem Dokument enthaltenen Datei. {@code null}, falls die Datei noch nie gespeichert wurde.
     */
    public Charset getCharset();
}