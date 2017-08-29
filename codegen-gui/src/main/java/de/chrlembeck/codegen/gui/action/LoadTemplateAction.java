package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.charset.Charset;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.UserSettings;

/**
 * Action zum Laden einer Template-Datei in den Editor.
 *
 * @author Christoph Lembeck
 */
public class LoadTemplateAction extends AbstractAction {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -517041202124336072L;

    /**
     * Referenz auf die Anwendung, in der ein Template geladen werden soll.
     */
    private CodeGenGui codeGenGui;

    /**
     * Erstellt eine neue Action mit den übergebenen Daten.
     * 
     * @param codeGenGui
     *            Referenz auf die Anwendung, in der ein Template geladen werden soll.
     * @param toolbar
     *            {@code true}, falls die Action innerhalb einer Toolbar verwendet werden soll, {@code false}, falls die
     *            Action in einem Menü verwendet wird.
     */
    public LoadTemplateAction(final CodeGenGui codeGenGui, final boolean toolbar) {
        super();
        this.codeGenGui = codeGenGui;
        if (toolbar) {

        } else {
            putValue(NAME, "Template öffnen");
            putValue(MNEMONIC_KEY, KeyEvent.VK_F);
            putValue(SMALL_ICON, IconFactory.OPEN_32.icon());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final FileNameExtensionFilter filter = new FileNameExtensionFilter("Template Files", "codegen");

        final JFileChooser chooser = CodeGenGui.createFileChooser(new UserSettings().getLastTemplateDirectory(),
                filter);
        final int selection = chooser.showOpenDialog(codeGenGui);
        if (selection == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();
            new UserSettings().setLastTemplateDirectory(file.toPath());
            codeGenGui.loadTemplate(file.toPath(), Charset.forName("UTF-8"));
        }
    }
}