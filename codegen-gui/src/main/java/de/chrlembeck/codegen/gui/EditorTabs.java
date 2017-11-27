package de.chrlembeck.codegen.gui;

import java.awt.Component;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import org.antlr.v4.runtime.Token;

import de.chrlembeck.antlr.editor.ErrorListener;
import de.chrlembeck.antlr.editor.TokenStyle;
import de.chrlembeck.antlr.editor.TokenStyleRepository;
import de.chrlembeck.codegen.grammar.CodeGenLexer;

/**
 * TabbedPane, welche die Fenster der Editoren für die Template-Dateien verwaltet und anzeigt.
 *
 * @author Christoph Lembeck
 */
public class EditorTabs extends BasicTabbedPane implements CaretPositionChangeListener {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -7702736682576253059L;

    /**
     * Liste der Listener, die über neue Cursor-Positionen des gerade aktiven Dokuments informiert werden sollen.
     */
    private List<CaretPositionChangeListener> caretPositionChangeListeners = new ArrayList<>();

    /**
     * Liste der Listener, die über Änderungen an den im gerade aktiven Dokument enthaltenen Fehlern informiert werden
     * wollen.
     */
    private List<ErrorListener> errorListeners = new ArrayList<>();

    /**
     * Formatierungsregeln für die Template-Editoren.
     */
    private TokenStyleRepository tokenStyles = initializeTokenStyles();

    private List<UndoableEditListener> undoableEditListeners = new ArrayList<>();

    /**
     * Erstellt das Objekt und initialisiert es.
     */
    public EditorTabs() {
        super(TOP, WRAP_TAB_LAYOUT);
    }

    private TokenStyleRepository initializeTokenStyles() {
        final UserSettings settings = new UserSettings();
        final TokenStyle defaultStyle = settings.getDefaultTokenStyle();
        final TokenStyle keywordStyle = settings.getKeywordTokenStyle();
        final TokenStyle javaPrimaryType = settings.getPrimitiveTypeTokenStyle();
        final TokenStyle stringLiteral = settings.getStringLiteralTokenStyle();
        final TokenStyle errorStyle = settings.getErrorTokenStyle();
        final TokenStyle commentStyle = settings.getCommentTokenStyle();
        final TokenStyle literalStyle = settings.getLiteralTokenStyle();
        return createTokenStyles(defaultStyle, keywordStyle, javaPrimaryType, stringLiteral, errorStyle, commentStyle,
                literalStyle);
    }

    /**
     * Initialisiert das Syntax-Highlighting für den Code-Editor.
     * 
     * @param commentStyle
     * @param errorStyle
     * @param stringLiteral
     * @param javaPrimaryType
     * @param keywordStyle
     */
    public static TokenStyleRepository createTokenStyles(final TokenStyle defaultStyle, final TokenStyle keywordStyle,
            final TokenStyle javaPrimaryType, final TokenStyle stringLiteral, final TokenStyle errorStyle,
            final TokenStyle commentStyle, final TokenStyle literalStyle) {
        final TokenStyleRepository styles = new TokenStyleRepository();
        styles.setDefaultStyle(defaultStyle);
        styles.putStyle(CodeGenLexer.BlockComment, commentStyle);
        styles.putStyle(CodeGenLexer.IMPORT, keywordStyle);
        styles.putStyle(CodeGenLexer.AS, keywordStyle);
        styles.putStyle(CodeGenLexer.TEMPLATE, keywordStyle);
        styles.putStyle(CodeGenLexer.ENDTEMPLATE, keywordStyle);
        styles.putStyle(CodeGenLexer.EXEC, keywordStyle);
        styles.putStyle(CodeGenLexer.FOREACH, keywordStyle);
        styles.putStyle(CodeGenLexer.FROM, keywordStyle);
        styles.putStyle(CodeGenLexer.ENDFOREACH, keywordStyle);
        styles.putStyle(CodeGenLexer.FOR, keywordStyle);
        styles.putStyle(CodeGenLexer.OUTPUT, keywordStyle);
        styles.putStyle(CodeGenLexer.ENDOUTPUT, keywordStyle);
        styles.putStyle(CodeGenLexer.COUNTER, keywordStyle);
        styles.putStyle(CodeGenLexer.SEPARATOR, keywordStyle);
        styles.putStyle(CodeGenLexer.IF, keywordStyle);
        styles.putStyle(CodeGenLexer.ELSE, keywordStyle);
        styles.putStyle(CodeGenLexer.ENDIF, keywordStyle);
        styles.putStyle(CodeGenLexer.INT, javaPrimaryType);
        styles.putStyle(CodeGenLexer.FLOAT, javaPrimaryType);
        styles.putStyle(CodeGenLexer.LONG, javaPrimaryType);
        styles.putStyle(CodeGenLexer.CHAR, javaPrimaryType);
        styles.putStyle(CodeGenLexer.SHORT, javaPrimaryType);
        styles.putStyle(CodeGenLexer.BOOLEAN, javaPrimaryType);
        styles.putStyle(CodeGenLexer.DOUBLE, javaPrimaryType);
        styles.putStyle(CodeGenLexer.VOID, javaPrimaryType);
        styles.putStyle(CodeGenLexer.NullLiteral, javaPrimaryType);
        styles.putStyle(CodeGenLexer.StringLiteral, stringLiteral);
        styles.putStyle(CodeGenLexer.ERR_CHAR, errorStyle);
        styles.putStyle(CodeGenLexer.IERR_CHAR, errorStyle);
        styles.putStyle(CodeGenLexer.IntegerLiteral, literalStyle);
        styles.putStyle(CodeGenLexer.BooleanLiteral, literalStyle);
        styles.putStyle(CodeGenLexer.FloatingPointLiteral, literalStyle);
        styles.putStyle(CodeGenLexer.CharacterLiteral, literalStyle);
        styles.putStyle(CodeGenLexer.NullLiteral, literalStyle);
        return styles;
    }

    /**
     * Öffnet einen neuen, leeren Template-Editor.
     */
    public void newTemplate() {
        final TemplatePanel templatePanel = new TemplatePanel(null, Charset.forName("UTF-8"), tokenStyles);
        addTabComponent(templatePanel, null, true);
        final TemplateEditorPane editorPane = templatePanel.getEditorPane();
        editorPane.addErrorListener(this::errorsChanged);
        editorPane.addUndoableEditListener(this::undoableEditHappened);
        editorPane.grabFocus();
    }

    /**
     * Entfernt den Listener für die Cursor-Position von allen Editorfenstern und setzt einen neuen Listener auf genau
     * das gerade aktive Fenster. Danach werden alle eingetragenen CaretPositionChangeListener über den Wechsel der
     * Cursor-Position informiert.
     */
    private void resetCaretListeners() {
        for (int i = 0; i < getTabCount(); i++) {
            final Component comp = getComponentAt(i);
            if (comp instanceof TemplatePanel) {
                final TemplatePanel templatePanel = (TemplatePanel) comp;
                templatePanel.getEditorPane().removeCaretPositionChangeListener(this);
                if (i == getSelectedIndex()) {
                    templatePanel.getEditorPane().addCaretPositionChangeListener(this);
                    templatePanel.getEditorPane().fireStatusEvent();
                }
            }
        }
    }

    /**
     * Wird aufgerufen, wenn ein anderer Tab aktiviert wird. Informiert danach die eingetragenen TabListener über dieses
     * Event.
     * 
     * @param event
     *            Event, welches den Wechsel zwischen zwei Tabs angezeigt hat.
     */
    @Override
    protected void tabChanged(final ChangeEvent event) {
        resetCaretListeners();
        super.tabChanged(event);
    }

    /**
     * {@inheritDoc} Informiert alle eingetragenen CaretPositionChangeListener über die neue Cursorposition.
     */
    @Override
    public void caretPositionChanged(final CaretPositionChangeEvent event) {
        for (final CaretPositionChangeListener editorStatusListener : caretPositionChangeListeners) {
            editorStatusListener.caretPositionChanged(event);
        }
    }

    /**
     * Fügt den übergebenen Listener zu der Liste der Listener hinzu, die über neue Cursor-Positionen im gerade aktiven
     * Dokument informiert werden sollen.
     * 
     * @param listener
     *            Neuer Listener für Cursor-Benachristigungen.
     */
    public void addCaretPositionChangeListener(final CaretPositionChangeListener listener) {
        caretPositionChangeListeners.add(listener);
    }

    /**
     * Entfernt den übergebenen Listener von der Liste der Listener, die über Cursorbewegungen im gerade aktiven
     * Dokument informiert werden.
     * 
     * @param listener
     *            Listener, der von der Liste gestrichen werden soll.
     */
    public void removeCaretPositionChangeListener(final CaretPositionChangeListener listener) {
        caretPositionChangeListeners.remove(listener);
    }

    /**
     * Lädt den Inhalt der übergebenen Datei in das Editorfenster.
     * 
     * @param path
     *            Pfad zu der zu ladenden Datei.
     * @param charset
     *            Encoding, mit dem die Datei gelesen werden soll.
     */
    public void loadTemplate(final Path path, final Charset charset) {
        final TemplatePanel templatePanel = new TemplatePanel(path, charset, tokenStyles);
        addTabComponent(templatePanel, null, true);
        final TemplateEditorPane editorPane = templatePanel.getEditorPane();
        editorPane.addErrorListener(this::errorsChanged);
        editorPane.addUndoableEditListener(this::undoableEditHappened);
        editorPane.grabFocus();
    }

    /**
     * Wird aufgerufen, wenn bei einer Validierung innerhalb des Editors neue Fehler innerhalb des Dokuments gefunden
     * wurden.
     * 
     * @param errors
     *            Liste der aktuell im Dokument erkannten Fehler. Die Liste enthält die Token, bei denen Probleme
     *            erkannt wurden zusammen mit einer entsprechenden Beschreibung des Problems.
     */
    private void errorsChanged(final Map<Token, String> errors) {
        errorListeners.forEach(listener -> listener.errorsChanged(errors));
    }

    /**
     * Fügt ein Paar der doppelten spitzen Klammern (&#x00ab;&#x00bb;) in den aktuell aktiven Editor ein.
     */
    public void insertBraces() {
        final TabComponent comp = getSelectedDocument();
        if (comp instanceof TemplatePanel) {
            final TemplatePanel templatePanel = (TemplatePanel) comp;
            templatePanel.getEditorPane().performInsertBraces();
        } else {
            JOptionPane.showMessageDialog(this, "Es ist gerade gar kein Template geöffnet.");
        }
    }

    /**
     * Fügt einen Listener in die Liste der Listener ein, die über Änderungen an den im gerade aktiven Dokument
     * enthaltenen Fehlern informiert werden wollen.
     * 
     * @param listener
     *            Listener, der zu der Liste hinzugefügt werden soll.
     */
    public void addErrorListener(final ErrorListener listener) {
        errorListeners.add(listener);
    }

    /**
     * Entfernt einen Listener von der Liste der Listener, die über Änderungen an den im gerade aktiven Dokument
     * enthaltenen Fehlern informiert werden wollen.
     * 
     * @param listener
     *            Listener, der von der Liste entfernt werden soll.
     */
    public void removeErrorListener(final ErrorListener listener) {
        errorListeners.remove(listener);
    }

    public void addUndoableEditListener(final UndoableEditListener listener) {
        undoableEditListeners.add(listener);
    }

    public void removeUndoableEditListener(final UndoableEditListener listener) {
        undoableEditListeners.remove(listener);
    }

    void undoableEditHappened(final UndoableEditEvent editEvent) {
        undoableEditListeners.forEach(listener -> listener.undoableEditHappened(editEvent));
    }

    public TokenStyleRepository getTokenStyles() {
        return tokenStyles;
    }

    public void updateTokenStyles(final TokenStyleRepository newTokenStyles) {
        this.tokenStyles.update(newTokenStyles);
        invalidate();
        repaint();
    }
}