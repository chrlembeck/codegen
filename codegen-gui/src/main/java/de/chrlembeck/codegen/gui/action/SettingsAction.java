package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;

/**
 * Action zum Aufrufen des Dialogs zur Verwaltung der Anwendungseinstellungen.
 *
 * @author Christoph Lembeck
 */
public class SettingsAction extends AbstractAction {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -6746508108337507582L;

    /**
     * Referenz auf die Anwendung, aus der der Dialog geöffnet wird.
     */
    private CodeGenGui codeGenGui;

    /**
     * Erstellt eine neue Action für die übergebene Anwendung.
     * 
     * @param codeGenGui
     *            Anwendung, zu der der Einstellungen-Dialog geöffnet werden soll.
     */
    public SettingsAction(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
        putValue(NAME, "Einstellungen");
        putValue(MNEMONIC_KEY, KeyEvent.VK_E);
        putValue(SMALL_ICON, IconFactory.SETTINGS_32.icon());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        System.out.println(codeGenGui);
        // TODO implementieren
    }
}