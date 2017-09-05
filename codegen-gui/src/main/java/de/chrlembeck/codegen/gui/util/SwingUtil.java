package de.chrlembeck.codegen.gui.util;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

@SuppressWarnings("PMD.UseUtilityClass")
public class SwingUtil {

    /**
     * Positioniert das übergebene Anwendungsfenster auf der Mitte des Bildschirms.
     * 
     * @param window
     *            Fenster, welches ausgerichtet werden soll.
     */
    public static void centerOnScreen(final Window window) {
        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final DisplayMode displayMode = graphicsEnvironment.getDefaultScreenDevice().getDisplayMode();
        window.setLocation((displayMode.getWidth() - window.getWidth()) / 2,
                (displayMode.getHeight() - window.getHeight()) / 2);
    }
}