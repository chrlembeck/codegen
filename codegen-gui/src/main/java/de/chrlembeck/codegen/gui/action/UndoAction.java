package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import de.chrlembeck.codegen.grammar.CodeGenParser.TemplateFileContext;
import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.TabComponent;
import de.chrlembeck.codegen.gui.TemplateEditorPane;
import de.chrlembeck.codegen.gui.TemplatePanel;

public class UndoAction extends AbstractAction {

    private static final long serialVersionUID = 6135621384579642786L;

    private CodeGenGui codeGenGui;

    public UndoAction(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
        putValue(NAME, "Undo");
        putValue(SHORT_DESCRIPTION, "Macht die letzte Änderung rückgängig.");
        putValue(SMALL_ICON, IconFactory.UNDO_32.icon());
        putValue(MNEMONIC_KEY, KeyEvent.VK_Z);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TabComponent selectedTabComponent = codeGenGui.getSelectedTabComponent();
        if (selectedTabComponent instanceof TemplatePanel) {
            final TemplatePanel templatePanel = (TemplatePanel) selectedTabComponent;
            final TemplateEditorPane<TemplateFileContext> editorPane = templatePanel.getEditorPane();
            editorPane.undo();
        }
        codeGenGui.updateUndoRedoState();
    }
}