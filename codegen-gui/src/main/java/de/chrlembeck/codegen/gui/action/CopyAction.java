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

public class CopyAction extends AbstractAction {

    private static final long serialVersionUID = -429517082398811761L;

    private CodeGenGui codeGenGui;

    public CopyAction(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
        putValue(NAME, "Kopieren");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        putValue(SMALL_ICON, IconFactory.COPY_32.icon());
        putValue(SHORT_DESCRIPTION, "Kopiert die Auswahl in die Zwischenablage.");
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TabComponent selectedDocument = codeGenGui.getSelectedDocument();
        if (selectedDocument instanceof TemplatePanel) {
            final TemplatePanel templatePanel = (TemplatePanel) selectedDocument;
            templatePanel.getEditorPane().copy();
        }
    }

}
