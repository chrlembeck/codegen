package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;

/**
 * Action zum behandeln der {@code Beenden}-Funktionalität der Anwendung.
 *
 * @author Christoph Lembeck
 */
public class CloseApplicationAction extends AbstractAction implements WindowListener {

    /**
     * Text der Schaltfläche zum Abbrechen der Aktion.
     */
    private static final String OPTION_CANCEL = "Abbrechen";

    /**
     * Text der Schaltfläche zum Beenden des Editors ohne Speichern der ungespeicherten Änderungen.
     */
    private static final String OPTION_CLOSE_WITHOUT_SAVE = "Beenden ohne Speichern";

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -1655086550143969889L;

    /**
     * Referenz auf die Anwendung, die durch diese Aktion beendet werden soll.
     */
    private CodeGenGui codeGenGui;

    /**
     * Erstellt eine neue Action mit den übergebenen Daten.
     * 
     * @param codeGenGui
     *            Referenz auf die Anwendung, die durch diese Aktion beendet werden soll.
     */
    public CloseApplicationAction(final CodeGenGui codeGenGui) {
        super();
        this.codeGenGui = codeGenGui;
        putValue(NAME, "Beenden");
        putValue(MNEMONIC_KEY, KeyEvent.VK_B);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        putValue(SMALL_ICON, IconFactory.EXIT_32.icon());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        if (codeGenGui.containsUnsavedModifications()) {
            final int option = JOptionPane.showOptionDialog(codeGenGui,
                    "Wollen Sie die Anwendung wirklich beenden ohne die Änderungen zu speichern?",
                    "Beenden ohne Speichern?", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                    new String[] { OPTION_CLOSE_WITHOUT_SAVE, OPTION_CANCEL }, OPTION_CANCEL);
            if (option == 0) {
                codeGenGui.dispose();
            }
        } else {
            codeGenGui.dispose();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowOpened(final WindowEvent event) {
        // nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosing(final WindowEvent event) {
        actionPerformed(new ActionEvent(event.getSource(), event.getID(), event.paramString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosed(final WindowEvent event) {
        // nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowIconified(final WindowEvent event) {
        // nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowDeiconified(final WindowEvent event) {
        // nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowActivated(final WindowEvent event) {
        // nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowDeactivated(final WindowEvent event) {
        // nothing to do here
    }
}