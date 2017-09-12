package de.chrlembeck.codegen.gui;

import java.lang.ref.SoftReference;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hilfsklasse zum einfachen Laden der Icons für den Editor.
 *
 * @author Christoph Lembeck
 */
public enum IconFactory {

    /**
     * Icon für das Einfügen eines Klammer-Paares
     */
    BRACES_32("braces_32.png"),

    GEAR_16("gear_16.png"),

    GEAR_24("gear_24.png"),

    GEAR_32("gear_32.png"),

    GEAR_48("gear_48.png"),

    CANCEL_22("cancel_22.png"),

    CANCEL_32("cancel_32.png"),

    ERROR_32("warning_32.png"),

    EXIT_22("exit_22.png"),

    /**
     * Icon für das Beenden der Anwendung.
     */
    EXIT_32("exit_32.png"),

    FOLDER_16("folder_16.png"),

    /**
     * Icon für das Starten des Generators.
     */
    GENERATE_32("generate_32.png"),

    /**
     * Icon für die ERstellung eines neuen Templates.
     */
    NEW_32("new_32.png"),

    OK_22("ok_22.png"),

    OK_32("ok_32.png"),

    /**
     * Icon für das Laden von Dateien.
     */
    OPEN_32("open_32.png"),

    /**
     * Icon für das Speichern von Dateien.
     */
    SAVE_32("save_32.png"),

    /**
     * Icon für das Speichern aller geöffneten Dateien.
     */
    SAVE_ALL_32("save_all_32.png"),

    /**
     * Icon für das Speichern von Dateien unter neuem Dateinamen.
     */
    SAVE_AS_32("save_as_32.png"),

    /**
     * Icon für das Einstellungen-Menü.
     */
    SETTINGS_32("settings_32.png"),

    WARNING_32("warning_32.png"),

    WARNING_48("warning_48.png"),

    UNDO_32("undo_32.png"),

    REDO_32("redo_32.png");

    /**
     * Logger für die Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IconFactory.class);

    /**
     * Referenz auf ein eventuell schon einmal geladenes Icon.
     */
    private SoftReference<Icon> iconCache;

    /**
     * Speichert den Ablageort eines Icons.
     */
    private String iconName;

    /**
     * Erstell eine neue Referenz auf ein Icon.
     * 
     * @param iconLocation
     *            Ablageort des hinterlegten Icons.
     */
    private IconFactory(final String iconLocation) {
        this.iconName = iconLocation;
    }

    /**
     * Gibt den Namen zurück, unter dem das Icon abgelegt ist.
     * 
     * @return Name der Icon-Datei.
     */
    public String getIconName() {
        return iconName;
    }

    /**
     * Ladt das Icon als Icon und gibt es zurück.
     * 
     * @return Icon als geladenes Bild.
     */
    public Icon icon() {
        Icon icon = iconCache == null ? null : iconCache.get();
        if (icon == null) {
            final URL location = getClass().getResource("/icons/" + iconName);
            if (location == null) {
                LOGGER.error("Icon '" + this.name() + "' konnte nicht gefunden werden: '" + getIconName() + "'.");
            } else {
                icon = new ImageIcon(location);
                iconCache = new SoftReference<Icon>(icon);
            }
        }
        return icon;
    }
}