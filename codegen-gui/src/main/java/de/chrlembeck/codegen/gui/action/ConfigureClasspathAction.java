package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.dialog.AbstractDialog;
import de.chrlembeck.codegen.gui.dialog.ConfigureClasspathDialog;

public class ConfigureClasspathAction extends AbstractAction {

    private static final long serialVersionUID = 4870705111742833789L;

    private CodeGenGui codeGenGui;

    public ConfigureClasspathAction(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
        putValue(NAME, "Classpath konfigurieren");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final ConfigureClasspathDialog dialog = new ConfigureClasspathDialog(codeGenGui);
        dialog.setVisible(true);
        if (dialog.getResult() == AbstractDialog.RESULT_OK) {
            codeGenGui.setModelClasspath(dialog.getClasspathElements());
        }
    }
}