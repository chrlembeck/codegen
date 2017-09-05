package de.chrlembeck.codegen.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;

public class CloseApplicationConfirmDialog extends AbstractDialog {

    /**
     * Text der Schaltfläche zum Abbrechen der Aktion.
     */
    private static final String OPTION_CANCEL = "Abbrechen";

    /**
     * Text der Schaltfläche zum Beenden des Editors ohne Speichern der ungespeicherten Änderungen.
     */
    private static final String OPTION_CLOSE_WITHOUT_SAVE = "Beenden ohne Speichern";

    public CloseApplicationConfirmDialog(final CodeGenGui owner) {
        super(owner, "Beenden ohne Speichern?");
        addOKButton(OPTION_CLOSE_WITHOUT_SAVE, KeyEvent.VK_B, IconFactory.EXIT_22.icon());
        addCancelButton(OPTION_CANCEL, KeyEvent.VK_C, IconFactory.CANCEL_22.icon());
    }

    @Override
    protected JPanel createMainPanel() {
        final JPanel panel = new JPanel();
        final JLabel label = new JLabel("Wollen Sie die Anwendung wirklich beenden ohne die Änderungen zu speichern?");
        label.setIcon(IconFactory.WARNING_48.icon());
        label.setIconTextGap(20);
        label.setVerticalTextPosition(SwingConstants.CENTER);
        label.setHorizontalTextPosition(SwingConstants.RIGHT);
        panel.add(label, BorderLayout.CENTER);

        final Font font = label.getFont();
        label.setFont(font.deriveFont(font.getSize() + 2f));
        return panel;
    }

}
