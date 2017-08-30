package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;

/**
 * Action zum Speichern einer Template-Datei aus dem Inhalt eines Editor-Fensters.
 *
 * @author Christoph Lembeck
 */
public class SaveTemplateAction extends AbstractAction {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 2869572367040677487L;

    /**
     * Referenz auf die Anwendung, in der ein Template gespeichert werden soll.
     */
    private CodeGenGui codeGenGui;

    /**
     * {@code true}, falls die Action innerhalb einer Toolbar verwendet werden soll, {@code false}, falls die Action in
     * einem Menü verwendet wird.
     */
    private boolean toolbar;

    /**
     * Erstellt eine neue Action mit den übergebenen Daten.
     * 
     * @param codeGenGui
     *            Referenz auf die Anwendung, in der ein Template gespeichert werden soll.
     * @param toolbar
     *            {@code true}, falls die Action innerhalb einer Toolbar verwendet werden soll, {@code false}, falls die
     *            Action in einem Menü verwendet wird.
     */
    public SaveTemplateAction(final CodeGenGui codeGenGui, final boolean toolbar) {
        super();
        this.codeGenGui = codeGenGui;
        this.toolbar = toolbar;
        if (toolbar) {
            putValue(NAME, "Template speichern");
            putValue(SHORT_DESCRIPTION, "Speichert das aktive Template");
            putValue(SMALL_ICON, IconFactory.SAVE_32.icon());
        } else {
            putValue(NAME, "Template speichern");
            putValue(SHORT_DESCRIPTION, "Speichert das aktive Template");
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
            putValue(SMALL_ICON, IconFactory.SAVE_32.icon());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (!codeGenGui.isDocumentSelected()) {
            JOptionPane.showMessageDialog(codeGenGui, "Es ist gar kein Template ausgewählt.", "Fehler",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (codeGenGui.isSelectedDocumentNew()) {
            new SaveTemplateAsAction(codeGenGui, toolbar).actionPerformed(e);
        } else {
            codeGenGui.saveDocument(codeGenGui.getSelectedDocumentPath(), codeGenGui.getSelectedTemplateCharset());
        }
    }
}