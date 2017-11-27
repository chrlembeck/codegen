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
import de.chrlembeck.codegen.gui.dialog.FindDialog;

public class FindAction extends AbstractAction {

    private static final long serialVersionUID = 1545464546343435544L;

    private CodeGenGui codeGenGui;

    public FindAction(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
        putValue(NAME, "Finden");
        putValue(MNEMONIC_KEY, KeyEvent.VK_F);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        putValue(SMALL_ICON, IconFactory.FIND_32.icon());
        putValue(SHORT_DESCRIPTION, "Sucht im aktuellen Dokument nach einer beliebigen Zeichenkette");
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final TabComponent selectedDocument = codeGenGui.getSelectedDocument();
        if (selectedDocument instanceof TemplatePanel) {
            final TemplatePanel templatePanel = (TemplatePanel) selectedDocument;
            final TemplateEditorPane editorPane = templatePanel.getEditorPane();
            new FindDialog(codeGenGui, editorPane).setVisible(true);
        }
    }
}