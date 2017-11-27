package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.TabComponent;
import de.chrlembeck.codegen.gui.TemplatePanel;

public class PasteAction extends AbstractAction {

    private static final long serialVersionUID = -5368284505606264553L;

    private CodeGenGui codeGenGui;

    public PasteAction(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
        putValue(NAME, "Einfügen");
        putValue(MNEMONIC_KEY, KeyEvent.VK_V);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        putValue(SMALL_ICON, IconFactory.PASTE_32.icon());
        putValue(SHORT_DESCRIPTION, "Fügt den Inhalt der Zwischenablage in den Editor ein.");
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final TabComponent selectedDocument = codeGenGui.getSelectedDocument();
        if (selectedDocument instanceof TemplatePanel) {
            final TemplatePanel templatePanel = (TemplatePanel) selectedDocument;
            templatePanel.getEditorPane().paste();
        }
    }
}