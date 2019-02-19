package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.dialog.CreateModelDialog;

public class LoadModelByReflectionAction extends AbstractAction {

    private static final long serialVersionUID = 1457030547294647958L;

    private CodeGenGui codeGenGui;

    public LoadModelByReflectionAction(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
        putValue(NAME, "Erzeuge Modell (Methodenaufruf)");
        putValue(MNEMONIC_KEY, KeyEvent.VK_M);
        putValue(SHORT_DESCRIPTION, "Modell via Reflection per Methodenaufruf erzeugen");
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final CreateModelDialog dialog = new CreateModelDialog(codeGenGui);
        dialog.setVisible(true);
    }
}