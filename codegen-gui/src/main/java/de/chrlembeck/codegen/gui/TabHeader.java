package de.chrlembeck.codegen.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Hilfsklasse zur Gestaltung der Header für die Reiter der Editorfenster.
 *
 * @author Christoph Lembeck
 */
class TabHeader extends JPanel {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 4890677437643078261L;

    /**
     * Label zur Anzeige des Reiternamens.
     */
    private JLabel lbTitle;

    /**
     * Erstellt eine neue Reiterüberschrift.
     * 
     * @param editorTabs
     *            Referenz auf das TabbedPane, in den der Reiter eingefügt wird.
     * @param name
     *            Anzuzeigender Name für den Reiter.
     * @param addCloseButton
     *            {@code true} falls der neue Reiter einen Button zum Schließen erhalten soll, {@code false} falls der
     *            Reiter nicht geschlossen können werden soll.
     */
    public TabHeader(final BasicTabbedPane editorTabs, final String name, final boolean addCloseButton) {
        setOpaque(false);
        lbTitle = new JLabel(name);
        setLayout(new GridBagLayout());
        add(lbTitle, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        if (addCloseButton) {
            final JButton btClose = new JButton("x");
            btClose.setBorder(null);
            btClose.setFocusable(false);
            add(btClose, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 5, 0, 0), 0, 0));
            btClose.addActionListener(action -> performCloseButtonAction(editorTabs));
        }
    }

    /**
     * Wird aufgerufen, wenn der Benutzer den Button zum Schließen des Reiters angeklickt hat.
     * 
     * @param tabbedPane
     *            Referenz auf das TabbedPane, in dem der Reiter enthalten ist.
     */
    public final void performCloseButtonAction(final BasicTabbedPane tabbedPane) {
        final int idx = tabbedPane.indexOfTabComponent(this);
        tabbedPane.performCloseTabAction(idx);
    }

    /**
     * Informiert den Reiter darüber, dass er ungespeicherte Änderungen enthält.
     */
    public void notifyTemplateWasModified() {
        lbTitle.setText("*" + lbTitle.getText());
    }

    /**
     * Informiert den Reiter darüber, dass er ein neues, noch nie gespeichertes Dokument enthält.
     * 
     * @param isNew
     *            {@code true} falls der Inhalt des Reiters neu und bislang ungespeichert ist, {@code false} falls das
     *            Dokument bereits auf der Festplatte existiert.
     */
    public void setNew(final boolean isNew) {
        if (isNew) {
            lbTitle.setText("<neu>");
        }
    }

    /**
     * Informiert den Reiter darüber, dass das Template gerade gespeichert wurde.
     * 
     * @param path
     *            Datei, in die das Template gespeichert wurde.
     */
    public void notifyDocumentSaved(final Path path) {
        if (path != null) {
            lbTitle.setText(String.valueOf(path.getFileName()));
            lbTitle.setFont(lbTitle.getFont().deriveFont(Font.PLAIN));
        }
    }

    /**
     * Ändert den Namen des im Reiter enthaltenen Dokuments.
     * 
     * @param newName
     *            Neuer Name des im Reitern anhtaltenen Dokuments.
     */
    public void setTemplateName(final String newName) {
        lbTitle.setText(newName);
    }
}