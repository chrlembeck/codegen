package de.chrlembeck.antlr.editor.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.antlr.editor.AntlrDocument;

/**
 * Löscht die Zeile aus dem Dokument, in dem der Cursor gerade steht und ggf. auch die, die gerade zusätzlch markiert
 * sind.
 * 
 * @author Christoph Lembeck
 */
public class DeleteRowAction extends AbstractAction implements Action {

    /**
     * Der Logger für die Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteRowAction.class);

    /**
     * Name der Action zum Löschen der aktuellen Zeile im Editor.
     */
    public static final String ACTION_DELETE_ROW = "ACTION_DELETE_ROW";

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -4819251994637905752L;

    /**
     * Löscht die Zeile, inder der Cursor gerade steht und ggf. auch die Zeilen, die zusätzlich markiert sind.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final JEditorPane editor = (JEditorPane) event.getSource();
        final AntlrDocument<?> doc = (AntlrDocument<?>) editor.getDocument();
        final int selectionStart = editor.getSelectionStart();
        int selectionEnd = editor.getSelectionEnd();
        // bei einer Selektion einer ganzen Zeile steht der Cursor in der nächsten. - Diese soll nicht mit gelöscht
        // werden.
        if (selectionEnd > selectionStart) {
            selectionEnd -= 1;
        }
        final Element firstElement = doc.getParagraphElement(selectionStart);
        final Element lastElement = doc.getParagraphElement(selectionEnd);
        final int startIndex = firstElement.getStartOffset();
        final int endIndex = Math.min(doc.getLength(), lastElement.getEndOffset());
        try {
            doc.remove(startIndex, endIndex - startIndex);
        } catch (final BadLocationException e1) {
            LOGGER.error("BadLocationException: " + e1.getMessage(), e1);
        }
    }
}