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

public class CutAction extends AbstractAction {

    private static final long serialVersionUID = -8791493540532865061L;

    private CodeGenGui codeGenGui;

    public CutAction(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
        putValue(NAME, "Ausschneiden");
        putValue(MNEMONIC_KEY, KeyEvent.VK_X);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        putValue(SMALL_ICON, IconFactory.CUT_32.icon());
        putValue(SHORT_DESCRIPTION, "Löscht die Auswahl und legt sie in die Zwischenablage.");
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final TabComponent selectedDocument = codeGenGui.getSelectedDocument();
        if (selectedDocument instanceof TemplatePanel) {
            final TemplatePanel templatePanel = (TemplatePanel) selectedDocument;
            templatePanel.getEditorPane().cut();
        }
    }

}
