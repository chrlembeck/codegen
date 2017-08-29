package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;

/**
 * Action zum Einfügen eines Paars der doppelten spitzen Klaamern (&#x00ab;&#x00bb;) in das aktuell aktive Dokument.
 */
public class InsertDoubleAngleQuotationMarksAction extends AbstractAction {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 3030954085372421113L;

    /**
     * Referenz auf die Gui, der die Editoren für die Dokumente enthält.
     */
    private CodeGenGui codeGenGui;

    /**
     * Erzeugt eine neue Action mit den übergebenen Daten.
     * 
     * @param codeGenGui
     *            Referenz auf die Gui, der die Editoren für die Dokumente enthält.
     */
    public InsertDoubleAngleQuotationMarksAction(final CodeGenGui codeGenGui) {
        super();
        this.codeGenGui = codeGenGui;
        putValue(NAME, "\\u00ab\\u00bb");
        putValue(SHORT_DESCRIPTION, "Fügt \u00ab\u00bb in das Dokument ein");
        putValue(SMALL_ICON, IconFactory.BRACES_32.icon());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        codeGenGui.insertBraces();
    }
}