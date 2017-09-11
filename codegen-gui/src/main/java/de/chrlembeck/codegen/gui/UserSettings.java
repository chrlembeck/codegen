package de.chrlembeck.codegen.gui;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.prefs.Preferences;

/**
 * Repository für die benutzerdefinierten Einstellungen, des Code-Generators. Diese werden von Sitzung zu Sitzung
 * gespeichert, so dass sie nicht verloren gehen.
 *
 * @author Christoph Lembeck
 */
public class UserSettings {

    /**
     * Name der Einstellung für das Verzeichnis, aus dem zuletzt eine Template-Datei geladen wurde.
     */
    private static final String LAST_TEMPLATE_DIRECTORY = "lastTemplateDirectory";

    /**
     * Name der Einstellung für das Verzeichnis, aus dem zuletzt eine Model-Datei geladen wurde.
     */
    private static final String LAST_MODEL_DIRECTORY = "lastModelDirectory";

    /**
     * Name der Einstellung für das Verzeichnis, in das zuletzt generierte Artefakte geschrieben wurden.
     */
    private static final String LAST_OUTPUT_DIRECTORY = "lastOutputDirectory";

    private static final String LAST_DEBUG_OUTPUT_DIRECTORY = "lastDebugOutputDirectory";

    /**
     * Gibt das Verzeichnis zurück, aus dem die letzte Template-Datei gelesen wurde.
     * 
     * @return Verzeichnis des letzten Zugriffs auf eine Template-Datei.
     */
    public Path getLastTemplateDirectory() {
        final Preferences preferences = Preferences.userNodeForPackage(CodeGenGui.class);
        final String dir = preferences.get(LAST_TEMPLATE_DIRECTORY, null);
        if (dir != null) {
            return FileSystems.getDefault().getPath(dir);
        }
        return FileSystems.getDefault().getPath(System.getProperty("user.home"));
    }

    /**
     * Setzt das Verzeichnis, aus dem gerade eine Template-Datei gelesen oder in welches eins geschrieben wurde.
     * 
     * @param path
     *            Verzeichnis des Zugriffs auf eine Template-Datei.
     */
    public void setLastTemplateDirectory(Path path) {
        final Preferences preferences = Preferences.userNodeForPackage(CodeGenGui.class);
        if (!Files.isDirectory(path)) {
            path = path.getParent();
        }
        if (path != null) {
            preferences.put(LAST_TEMPLATE_DIRECTORY, path.toString());
        }
    }

    /**
     * Setzt das Verzeichnis, welches gerade für die Ausgabe eines Generatorlaufs verwendet wurde.
     * 
     * @param path
     *            Verzeichnis der Ausgabe des Generatorlaufs.
     */
    public void setLastOutputDirectory(final Path path) {
        if (path != null) {
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException("Path is not a directory: " + path);
            }
            final Preferences preferences = Preferences.userNodeForPackage(CodeGenGui.class);
            preferences.put(LAST_OUTPUT_DIRECTORY, path.toString());
        }
    }

    /**
     * Gibt das Verzeichnis zurück, in das zuletzt Artefakte generiert wurden.
     * 
     * @return Verzeichnis der letzten Generator-Ausgabe.
     */
    public Path getLastOutputDirectory() {
        final Preferences preferences = Preferences.userNodeForPackage(CodeGenGui.class);
        final String dir = preferences.get(LAST_OUTPUT_DIRECTORY, null);
        if (dir != null) {
            return FileSystems.getDefault().getPath(dir);
        }
        return FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Gibt das Verzeichnis zurück, aus dem die letzte Modell-Datei gelesen wurde.
     * 
     * @return Verzeichnis des letzten Zugriffs auf eine Modell-Datei.
     */
    public Path getLastModelDirectory() {
        final Preferences preferences = Preferences.userNodeForPackage(CodeGenGui.class);
        final String dir = preferences.get(LAST_MODEL_DIRECTORY, null);
        if (dir != null) {
            return FileSystems.getDefault().getPath(dir);
        }
        return FileSystems.getDefault().getPath(System.getProperty("user.home"));
    }

    /**
     * Setzt das Verzeichnis, aus dem gerade eine Modell-Datei gelesen oder in welches eine geschrieben wurde.
     * 
     * @param path
     *            Verzeichnis des Zugriffs auf eine Modell-Datei.
     */
    public void setLastModelDirectory(Path path) {
        final Preferences preferences = Preferences.userNodeForPackage(CodeGenGui.class);
        if (!Files.isDirectory(path)) {
            path = path.getParent();
        }
        if (path != null) {
            preferences.put(LAST_MODEL_DIRECTORY, path.toString());
        }
    }

    public Path getLastDebugOutputDirectory() {
        final Preferences preferences = Preferences.userNodeForPackage(CodeGenGui.class);
        final String dir = preferences.get(LAST_DEBUG_OUTPUT_DIRECTORY, null);
        if (dir != null) {
            return FileSystems.getDefault().getPath(dir);
        }
        return FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Setzt das Verzeichnis, welches gerade für die Ausgabe der Debug-Informationen eines Generatorlaufs verwendet
     * wurde.
     * 
     * @param path
     *            Verzeichnis der Ausgabe der Debug-Informationen für den Generatorlauf.
     */
    public void setLastDebugOutputDirectory(final Path path) {
        if (path != null) {
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException("Path is not a directory: " + path);
            }
            final Preferences preferences = Preferences.userNodeForPackage(CodeGenGui.class);
            preferences.put(LAST_DEBUG_OUTPUT_DIRECTORY, path.toString());
        }
    }
}