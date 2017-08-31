package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.charset.Charset;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.UserSettings;

/**
 * Action zum Speichern einer Template-Datei aus einem Editor-Fenster in eine Datei mit neuem Dateinamen.
 *
 * @author Christoph Lembeck
 */
public class SaveTemplateAsAction extends AbstractAction {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -6091745683121830321L;

    /**
     * Text für die Schaltfläche zum Abbrechen der Aktion.
     */
    private static final String OPTION_CANCEL = "Abbrechen";

    /**
     * Referenz auf die Anwendung, in der ein Template unter neuem Namen gespeichert werden soll.
     */
    private CodeGenGui codeGenGui;

    /**
     * Erstellt eine neue Action mit den übergebenen Daten.
     * 
     * @param codeGenGui
     *            Referenz auf die Anwendung, in der ein Template unter neuem Namen gespeichert werden soll.
     * @param toolbar
     *            {@code true}, falls die Action innerhalb einer Toolbar verwendet werden soll, {@code false}, falls die
     *            Action in einem Menü verwendet wird.
     */
    public SaveTemplateAsAction(final CodeGenGui codeGenGui, final boolean toolbar) {
        super();
        this.codeGenGui = codeGenGui;
        if (toolbar) {
            putValue(NAME, "Template speichern unter");
            putValue(SHORT_DESCRIPTION, "Speichert das aktive Template in einer neuen Datei");
            putValue(SMALL_ICON, IconFactory.SAVE_AS_32.icon());
        } else {
            putValue(NAME, "Template speichern unter");
            putValue(SHORT_DESCRIPTION, "Speichert das aktive Template in einer neuen Datei");
            putValue(SMALL_ICON, IconFactory.SAVE_AS_32.icon());
            putValue(MNEMONIC_KEY, KeyEvent.VK_U);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        if (!codeGenGui.isDocumentSelected()) {
            JOptionPane.showMessageDialog(codeGenGui, "Es ist gar kein Template ausgewählt.", "Fehler",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        final FileNameExtensionFilter filter = new FileNameExtensionFilter("Template Files", "codegen");
        final JFileChooser chooser = CodeGenGui.createFileChooser(new UserSettings().getLastTemplateDirectory(),
                filter);
        do {
            final int selection = chooser.showSaveDialog(codeGenGui);
            if (selection == JFileChooser.APPROVE_OPTION) {
                final File file = chooser.getSelectedFile();
                if (file.exists()) {
                    final String OPTION_OVERWRITE = "Überschreiben";
                    final int ovSelection = JOptionPane.showOptionDialog(codeGenGui,
                            "Wollen sie die bestehende Datei '" + file.getName() + "' überschreiben?", "Überschreiben?",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                            new String[] { OPTION_OVERWRITE, OPTION_CANCEL }, OPTION_CANCEL);
                    if (ovSelection == 1) {
                        // Überschreiben abbrechen
                        continue; // also noch einmal eine Datei aussuchen
                    }
                }
                new UserSettings().setLastTemplateDirectory(file.toPath());
                codeGenGui.saveDocument(file.toPath(), Charset.forName("UTF-8"));
                // Speichern fertig
                break;
            } else {
                // Speichern abbrechen
                break;
            }
        } while (true);
    }
}