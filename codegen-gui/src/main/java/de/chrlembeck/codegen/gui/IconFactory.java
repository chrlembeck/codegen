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
     * Icon für das Beenden der Anwendung.
     */
    EXIT_32("/icons/exit_32.png"),

    /**
     * Icon für die ERstellung eines neuen Templates.
     */
    NEW_32("/icons/new_32.png"),

    /**
     * Icon für das Laden von Dateien.
     */
    OPEN_32("/icons/open_32.png"),

    /**
     * Icon für das Speichern von Dateien.
     */
    SAVE_32("/icons/save_32.png"),

    /**
     * Icon für das Speichern von Dateien unter neuem Dateinamen.
     */
    SAVE_AS_32("/icons/save_as_32.png"),

    /**
     * Icon für das Speichern aller geöffneten Dateien.
     */
    SAVE_ALL_32("/icons/save_all_32.png"),

    /**
     * Icon für das Starten des Generators.
     */
    GENERATE_32("/icons/generate_32.png"),

    /**
     * Icon für das Einfügen eines Klammer-Paares
     */
    BRACES_32("/icons/braces_32.png"),

    /**
     * Icon für das Einstellungen-Menü.
     */
    SETTINGS_32("/icons/settings_32.png");

    /**
     * Logger für die Klasse.
     */
    private static Logger LOGGER = LoggerFactory.getLogger(IconFactory.class);

    /**
     * Speicher den Ablageort eines Icons.
     */
    private String iconLocation;

    /**
     * Referenz auf ein eventuell schon einmal geladenes Icon.
     */
    private SoftReference<Icon> iconCache;

    /**
     * Erstell eine neue Referenz auf ein Icon.
     * 
     * @param iconLocation
     *            Ablageort des hinterlegten Icons.
     */
    private IconFactory(final String iconLocation) {
        this.iconLocation = iconLocation;
    }

    /**
     * Gibt den Ort zurück, an dem das Icon abgelegt ist.
     * 
     * @return Ablageort des Icons.
     */
    public String getIconLocation() {
        return iconLocation;
    }

    /**
     * Ladt das Icon als Icon und gibt es zurück.
     * 
     * @return Icon als geladenes Bild.
     */
    public Icon icon() {
        Icon icon = iconCache == null ? null : iconCache.get();
        if (icon == null) {
            final URL location = getClass().getResource(iconLocation);
            if (location == null) {
                LOGGER.error("Icon '" + this.name() + "' konnte nicht gefunden werden: '" + getIconLocation() + "'.");
            } else {
                icon = new ImageIcon(location);
                iconCache = new SoftReference<Icon>(icon);
            }
        }
        return icon;
    }

}