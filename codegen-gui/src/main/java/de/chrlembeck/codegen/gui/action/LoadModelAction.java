package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serial;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.model.ModelFactoryHelper;
import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.UserSettings;

/**
 * Action zum Laden eines Modells für den Editor.
 *
 * @author Christoph Lembeck
 */
public class LoadModelAction extends AbstractAction {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    @Serial
    private static final long serialVersionUID = -7342896869343539882L;

    /**
     * Der Logger für diese Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadModelAction.class);

    /**
     * Referenz auf die Gui, die die Action verwenden möchte.
     */
    private final CodeGenGui codeGenGui;

    /**
     * Erstellt eine neue Action mit den übergebenen Daten.
     * 
     * @param codeGenGui
     *            Referenz auf die Gui, die die Action verwenden möchte.
     * @param toolbar
     *            {@code true}, falls die Action innerhalb einer Toolbar verwendet werden soll, {@code false}, falls die
     *            Action in einem Menü verwendet wird.
     */
    public LoadModelAction(final CodeGenGui codeGenGui, final boolean toolbar) {
        super();
        this.codeGenGui = codeGenGui;
        if (toolbar) {
            putValue(NAME, "Model laden (Serializable)");
            putValue(SHORT_DESCRIPTION, "Modell via ObjectInputStream laden");
        } else {
            putValue(NAME, "Model laden (Serializable)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_L);
            putValue(SHORT_DESCRIPTION, "Modell via ObjectInputStream laden");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final FileNameExtensionFilter filter = new FileNameExtensionFilter("Serialized Model Files", "smodel");
        final JFileChooser chooser = CodeGenGui.createFileChooser(new UserSettings().getLastModelDirectory(), filter,
                JFileChooser.FILES_ONLY);
        final int selection = chooser.showOpenDialog(codeGenGui);
        if (selection == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();
            new UserSettings().setLastModelDirectory(file.toPath());
            loadModel(file);
        }
    }

    private void loadModel(final File file) {
        try (FileInputStream fileIn = new FileInputStream(file)) {
            final Object newModel = ModelFactoryHelper.byDeserialization(fileIn, codeGenGui.getModelClassLoader());
            codeGenGui.setModel(file.getName(), newModel);
        } catch (final IOException ioe) {
            LOGGER.info("IOException beim Laden eines Modells via readObject(). " + file.getAbsolutePath(), ioe);
            JOptionPane.showMessageDialog(codeGenGui, "IO-Fehler beim Laden des Models.", "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        } catch (final ClassNotFoundException cnfe) {
            LOGGER.info("Modellklasse konnte beim Laden via readObject() nicht gefunden werden. "
                    + file.getAbsolutePath(), cnfe);
            JOptionPane.showMessageDialog(codeGenGui, "Fehler beim Deserialisieren des Modells.", "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}