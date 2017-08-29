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
}