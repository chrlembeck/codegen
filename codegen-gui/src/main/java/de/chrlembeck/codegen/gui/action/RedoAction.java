package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.TabComponent;
import de.chrlembeck.codegen.gui.TemplateEditorPane;
import de.chrlembeck.codegen.gui.TemplatePanel;

public class RedoAction extends AbstractAction {

    private static final long serialVersionUID = 4998816869460876112L;

    private CodeGenGui codeGenGui;

    public RedoAction(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
        putValue(NAME, "Undo");
        putValue(SHORT_DESCRIPTION, "Macht die letzte Änderung rückgängig.");
        putValue(SMALL_ICON, IconFactory.REDO_32.icon());
        putValue(MNEMONIC_KEY, KeyEvent.VK_Y);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final TabComponent selectedTabComponent = codeGenGui.getSelectedTabComponent();
        if (selectedTabComponent instanceof TemplatePanel) {
            final TemplatePanel templatePanel = (TemplatePanel) selectedTabComponent;
            final TemplateEditorPane editorPane = templatePanel.getEditorPane();
            editorPane.redo();
        }
        codeGenGui.updateUndoRedoState();
    }
}