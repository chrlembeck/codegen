package de.chrlembeck.antlr.editor.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.antlr.editor.AntlrDocument;

/**
 * Fügt den hinterlegten Text an der aktuellen Stelle des Cursors bzw. der aktuellen Markierung ein. Es kann dabei
 * gesteuert werden, ob der aktuell markierte Text ersetzt werden soll, oder ob der Text mit Textfragmenten umgeben
 * werden soll. Tusätzliche kann die Position des Cursors nach der Einfügeoperation festgelegt werden.
 *
 * @author Christoph Lembeck
 */
public class InsertAction extends AbstractAction {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 2040796318544472822L;

    /**
     * Der Logger für die Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InsertAction.class);

    /**
     * Text, der an den Beginn der aktuellen Markierung geschrieben werden soll (oder an die Cursorposition, wenn keine
     * Markierung vorgenommen wurde).
     */
    public String startText;

    /**
     * Text, der an das Ende der aktuellen Markierung geschrieben werden soll (oder an die Cursorposition, wenn keine
     * Markierung vorgenommen wurde).
     */
    public String endText;

    /**
     * Legt die Position des Cursors nach der Einfügeoperation fest. Die Position wird dabei abhängig vom ersten
     * engegebenen Zeichen festgelegt.
     */
    public int caretOffset;

    /**
     * Legt fest, ob der Text eingefügt oder ersetzt werden soll. true entspricht ersetzen, false einfügen.
     */
    public boolean replace;

    /**
     * Erstelle eine neue Action mit den übergebenen Werten.
     * 
     * @param startText
     *            Text, der an den Beginn der aktuellen Markierung geschrieben werden soll (oder an die Cursorposition,
     *            wenn keine Markierung vorgenommen wurde).
     * @param endText
     *            Text, der an das Ende der aktuellen Markierung geschrieben werden soll (oder an die Cursorposition,
     *            wenn keine Markierung vorgenommen wurde).
     * @param caretOffset
     *            Legt die Position des Cursors nach der Einfügeoperation fest. Die Position wird dabei abhängig vom
     *            ersten engegebenen Zeichen festgelegt.
     * @param replace
     *            Legt fest, ob der Text eingefügt oder ersetzt werden soll. true entspricht ersetzen, false einfügen.
     */
    public InsertAction(final String startText, final String endText, final int caretOffset, final boolean replace) {
        super();
        this.startText = toString(startText);
        this.endText = toString(endText);
        this.caretOffset = caretOffset;
        this.replace = replace;
    }

    /**
     * Fügt das Einfügen oder Erstzen durch.
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        final JEditorPane editor = (JEditorPane) event.getSource();
        if (editor.isEditable() && editor.isEnabled()) {
            try {
                final AntlrDocument<?> document = (AntlrDocument<?>) editor.getDocument();
                System.out.println("---");
                for (final DocumentListener dl : document.getDocumentListeners()) {
                    System.out.println(dl);
                }
                System.out.println("---");
                final int selectionStart = editor.getSelectionStart();
                if (replace) {
                    editor.replaceSelection(startText + endText);
                } else {
                    final int selectionEnd = editor.getSelectionEnd();
                    document.insertString(selectionStart, startText, null);
                    document.insertString(startText.length() +
                            selectionEnd, endText, null);
                }
                editor.setCaretPosition(selectionStart + caretOffset);
                editor.grabFocus();
            } catch (final BadLocationException e1) {
                LOGGER.error("Bad Location Exception: " + e1.getMessage(), e1);
            }
        }
    }

    /**
     * Hilfsmethode, die einen null-String in einen Leer-String umwandelt.
     * 
     * @param string
     *            Zu prüfende Zeichenkette.
     * @return Leerstring, wenn die Zeichenkette null war, sonst bleibt die Zeichenkette unverändert.
     */
    private static String toString(final String string) {
        return string == null ? "" : string;
    }
}