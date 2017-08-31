package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;

/**
 * Action zur Erstellung eines neuen, leeren Template-Editors.
 *
 * @author Christoph Lembeck
 */
public class NewTemplateAction extends AbstractAction {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -511163526770485627L;

    /**
     * Referenz auf die Anwendung, in der ein neues Template erstellt werden soll.
     */
    private CodeGenGui codeGenGui;

    /**
     * Erstellt eine neue Action mit den übergebenen Daten.
     * 
     * @param codeGenGui
     *            Referenz auf die Anwendung, in der ein neues Template erstellt werden soll.
     * @param toolbar
     *            {@code true}, falls die Action innerhalb einer Toolbar verwendet werden soll, {@code false}, falls die
     *            Action in einem Menü verwendet wird.
     */
    public NewTemplateAction(final CodeGenGui codeGenGui, final boolean toolbar) {
        super();
        this.codeGenGui = codeGenGui;
        if (toolbar) {
            putValue(NAME, "Neues Template");
            putValue(SHORT_DESCRIPTION, "Legt ein neues Template an");
            putValue(SMALL_ICON, IconFactory.NEW_32.icon());
        } else {
            putValue(NAME, "Neues Template");
            putValue(MNEMONIC_KEY, KeyEvent.VK_N);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
            putValue(SMALL_ICON, IconFactory.NEW_32.icon());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        codeGenGui.newTemplate();
    }
}