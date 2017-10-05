package de.chrlembeck.codegen.gui;

import java.awt.Color;
import java.awt.Font;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import de.chrlembeck.antlr.editor.TokenStyle;

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

    private static final String TOKEN_STYLE_KEYWORD_FONT = "tsKeywordFont";

    private static final String TOKEN_STYLE_KEYWORD_COLOR = "tsKeywordColor";

    private static final String TOKEN_STYLE_JAVA_PRIMARY_TYPE_FONT = "tsJavaPrimaryTypeFont";

    private static final String TOKEN_STYLE_JAVA_PRIMARY_TYPE_COLOR = "tsJavaPrimaryTypeColor";

    private static final String TOKEN_STYLE_STRING_LITERAL_FONT = "tsStringLiteralFont";

    private static final String TOKEN_STYLE_STRING_LITERAL_COLOR = "tsStringLiteralColor";

    private static final String TOKEN_STYLE_ERROR_FONT = "tsErrorFont";

    private static final String TOKEN_STYLE_ERROR_COLOR = "tsErrorColor";

    private static final String TOKEN_STYLE_COMMENT_FONT = "tsCommentFont";

    private static final String TOKEN_STYLE_COMMENT_COLOR = "tsCommentColor";

    private static final String LAST_CLASSPATH_DIRECTORY = "classpathDir";

    private final Preferences preferences = Preferences.userNodeForPackage(CodeGenGui.class);;

    /**
     * Gibt das Verzeichnis zurück, aus dem die letzte Template-Datei gelesen wurde.
     * 
     * @return Verzeichnis des letzten Zugriffs auf eine Template-Datei.
     */
    public Path getLastTemplateDirectory() {
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
            preferences.put(LAST_OUTPUT_DIRECTORY, path.toString());
        }
    }

    /**
     * Gibt das Verzeichnis zurück, in das zuletzt Artefakte generiert wurden.
     * 
     * @return Verzeichnis der letzten Generator-Ausgabe.
     */
    public Path getLastOutputDirectory() {
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
        if (!Files.isDirectory(path)) {
            path = path.getParent();
        }
        if (path != null) {
            preferences.put(LAST_MODEL_DIRECTORY, path.toString());
        }
    }

    public Path getLastDebugOutputDirectory() {
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
            preferences.put(LAST_DEBUG_OUTPUT_DIRECTORY, path.toString());
        }
    }

    public Path getLastClasspathDirectory() {
        final String dir = preferences.get(LAST_CLASSPATH_DIRECTORY, null);
        if (dir != null) {
            return FileSystems.getDefault().getPath(dir);
        }
        return FileSystems.getDefault().getPath(System.getProperty("user.home"));
    }

    public void setLastClasspathDirectory(Path path) {
        if (!Files.isDirectory(path)) {
            path = path.getParent();
        }
        if (path != null) {
            preferences.put(LAST_CLASSPATH_DIRECTORY, path.toString());
        }
    }

    private TokenStyle readTokenStyle(final String tokenStyleKeywordFont, final int defaultFontStyle,
            final String tokenStyleKeywordColor, final Color defaultColor) {
        final int fontStyle = preferences.getInt(tokenStyleKeywordFont, defaultFontStyle);
        final Color color = new Color(preferences.getInt(tokenStyleKeywordColor, defaultColor.getRGB()));
        return new TokenStyle(color, fontStyle);
    }

    public void setTokenStyle(final TokenStyle style, final String fontKey, final String colorKey) {
        preferences.putInt(fontKey, style.getFontStyle());
        preferences.putInt(colorKey, style.getColor().getRGB());
    }

    public TokenStyle getKeywordTokenStyle() {
        return readTokenStyle(TOKEN_STYLE_KEYWORD_FONT, Font.BOLD, TOKEN_STYLE_KEYWORD_COLOR, new Color(127, 0, 85));
    }

    public void setKeywordTokenStyle(final TokenStyle style) {
        setTokenStyle(style, TOKEN_STYLE_KEYWORD_FONT, TOKEN_STYLE_KEYWORD_COLOR);
    }

    public TokenStyle getJavaPrimaryTypeTokenStyle() {
        return readTokenStyle(TOKEN_STYLE_JAVA_PRIMARY_TYPE_FONT, Font.BOLD, TOKEN_STYLE_JAVA_PRIMARY_TYPE_COLOR,
                new Color(180, 0, 60));
    }

    public void setJavaPrimaryTypeTokenStyle(final TokenStyle tokenStyle) {
        setTokenStyle(tokenStyle, TOKEN_STYLE_JAVA_PRIMARY_TYPE_FONT, TOKEN_STYLE_JAVA_PRIMARY_TYPE_COLOR);
    }

    public TokenStyle getStringLiteralTokenStyle() {
        return readTokenStyle(TOKEN_STYLE_STRING_LITERAL_FONT, Font.PLAIN, TOKEN_STYLE_STRING_LITERAL_COLOR,
                new Color(42, 0, 255));
    }

    public void setStringLiteralTokenStyle(final TokenStyle style) {
        setTokenStyle(style, TOKEN_STYLE_STRING_LITERAL_FONT, TOKEN_STYLE_STRING_LITERAL_COLOR);
    }

    public TokenStyle getErrorTokenStyle() {
        return readTokenStyle(TOKEN_STYLE_ERROR_FONT, Font.ITALIC, TOKEN_STYLE_ERROR_COLOR, new Color(220, 0, 0));
    }

    public void setErrorTokenStyle(final TokenStyle style) {
        setTokenStyle(style, TOKEN_STYLE_ERROR_FONT, TOKEN_STYLE_ERROR_COLOR);
    }

    public TokenStyle getCommentTokenStyle() {
        return readTokenStyle(TOKEN_STYLE_COMMENT_FONT, Font.ITALIC, TOKEN_STYLE_COMMENT_COLOR, new Color(63, 127, 95));
    }

    public void setCommentTokenStyle(final TokenStyle style) {
        setTokenStyle(style, TOKEN_STYLE_COMMENT_FONT, TOKEN_STYLE_COMMENT_COLOR);
    }
}