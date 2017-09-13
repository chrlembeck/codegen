package de.chrlembeck.codegen.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.antlr.editor.AntlrDocument;
import de.chrlembeck.antlr.editor.AntlrEditorKit;
import de.chrlembeck.antlr.editor.ErrorListener;
import de.chrlembeck.antlr.editor.TokenStyleRepository;
import de.chrlembeck.antlr.editor.action.InsertAction;
import de.chrlembeck.codegen.generator.Position;
import de.chrlembeck.codegen.grammar.CodeGenLexer;
import de.chrlembeck.codegen.grammar.CodeGenParser;
import de.chrlembeck.codegen.grammar.CodeGenParser.TemplateFileContext;
import de.chrlembeck.util.swing.ContiguousUpdateManager;
import de.chrlembeck.util.swing.SimpleDocumentListener;

/**
 * Editor für Template-Dateien.
 *
 * @param <T>
 *            Typ des vom Parser zurückgegebenen Kontext-Ojekts
 * @author Christoph Lembeck
 */
public class TemplateEditorPane<T extends ParserRuleContext> extends JEditorPane {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -7817592231847982962L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEditorPane.class);

    /**
     * Name der Action zum Einfügen eines Paars doppelter spitzer Klammern.
     */
    public static final String ACTION_INSERT_BRACES = "ACTION_INSERT_BRACES";

    /**
     * Name der Action zum Einfügen einer rechten doppelten spitzen Klammer.
     */
    public static final String ACTION_INSERT_RPAR = "ACTION_INSERT_RPAR";

    /**
     * Name der Action zum Einfügen einer linken doppelten spitzen Klammer.
     */
    public static final String ACTION_INSERT_LPAR = "ACTION_INSERT_LPAR";

    /**
     * Name der Action zum Einfügen des Grundgerüsts einer Template-Definition.
     */
    private static final String ACTION_INSERT_TEMPLATE_STATEMENT = "ACTION_INSERT_TEMPLATE_STATEMENT";

    /**
     * Liste der Listener, die über neue Cursor-Positionen informiert werden sollen.
     */
    private List<CaretPositionChangeListener> caretPositionChangeListeners = new ArrayList<>();

    /**
     * Liste der Listener, die über Änderungen an den im Dokument enthaltenen Fehlern informiert werden wollen.
     */
    private List<ErrorListener> errorListeners = new ArrayList<>();

    /**
     * Liste der Listener, die über Änderungen an dem Dokument informiert werden sollen.
     */
    private List<ModificationListener> modificationListeners = new ArrayList<>();

    private final UndoManager undoManager;

    /**
     * {@code true} falls das Dokument Änderungen enthält, die noch nicht gespeichert wurden, {@code false} falls das
     * Dokument neu und unverändert oder frisch gelesen oder gespeichert wurde.
     */
    private boolean unsavedChanges;

    /**
     * Pfad zu der Datei, aus der das Dokument zuletzt gelesen oder in die es zuletzt gespeichert wurde.
     */
    private Path path;

    /**
     * Encoding der enthaltenen Datei, falls dieses bekannt. {@code null}, falls die Datei noch nie geladen oder
     * gespeichert wurde.
     */
    private Charset charset;

    /**
     * Listener zur Erkennung von Änderungen am Dokument, die dazu führen, dass das Dokument wieder gespeichert werden
     * sollte.
     */
    private final DocumentListener documentChangeListener = new SimpleDocumentListener(this::notifyChange);

    /**
     * Listener, der auf Änderungen an den im Dokument enthaltenen Fehlern reagiert.
     */
    private final ErrorListener errorListener = this::errorsChanged;

    private List<UndoableEditListener> undoableEditListeners = new ArrayList<>();

    /**
     * Erstellt ein neues EditorPane-Objekt mit den übergebenen Daten.
     * 
     * @param path
     *            Pfad zu der enthaltenen Datei, falls diese geladen wurde oder {@code null}, falls das Dokument gerade
     *            neu erzeut wurde.
     * @param charset
     *            Encoding der zu bearbeitenden Datei, falls dieses bekannt ist.
     * @param tokenStyles
     *            Zuordnung der Token zu ihren Darstellungseigenschaften.
     */
    public TemplateEditorPane(final Path path, final Charset charset, final TokenStyleRepository tokenStyles) {
        super();
        this.path = path;
        this.charset = charset;
        setContentType("text/plain");
        final Font font = new Font("Consolas", Font.BOLD, 16);
        putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        setFont(font);
        setForeground(Color.BLACK);
        final Lexer lexer = new CodeGenLexer(null);
        final String startRuleName = CodeGenParser.ruleNames[CodeGenParser.RULE_templateFile];
        final AntlrEditorKit<T> editorKit = new AntlrEditorKit<T>(lexer, CodeGenParser.class, startRuleName,
                tokenStyles);
        setEditorKit(editorKit);
        initActions();
        if (path != null) {
            loadTemplate(path, charset);
        }
        addCaretListener(event -> fireStatusEvent(event.getDot()));
        undoManager = new ContiguousUpdateManager(this);
        getDocument().addUndoableEditListener(this::undoableEditHappened);
    }

    /**
     * Initialisiert die in dem Editor durchführbaren Aktionen. Diese können z.B. durch bestimmte Tastatur-Shortcuts
     * ausgelöst werden.
     */
    private void initActions() {
        // Actions
        final ActionMap actionMap = getActionMap();
        actionMap.put(ACTION_INSERT_BRACES, new InsertAction("\u00ab", "\u00bb", 1, false));
        actionMap.put(ACTION_INSERT_TEMPLATE_STATEMENT,
                new InsertAction("\u00abTEMPLATE  FOR \u00bb\n    ", "\n\u00abENDTEMPLATE\u00bb", 10, false));
        actionMap.put(ACTION_INSERT_LPAR, new InsertAction("\u00ab", null, 1, true));
        actionMap.put(ACTION_INSERT_RPAR, new InsertAction("\u00bb", null, 1, true));

        // key bindings
        final InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
        final KeyStroke ksLpar = KeyStroke.getKeyStroke(KeyEvent.VK_LESS, InputEvent.CTRL_DOWN_MASK);
        final KeyStroke ksRpar = KeyStroke.getKeyStroke(KeyEvent.VK_LESS,
                InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        final KeyStroke ksCtrlT = KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK);
        final KeyStroke ksCtrlB = KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK);
        inputMap.put(ksCtrlB, ACTION_INSERT_BRACES);
        inputMap.put(ksLpar, ACTION_INSERT_LPAR);
        inputMap.put(ksRpar, ACTION_INSERT_RPAR);
        inputMap.put(ksCtrlT, ACTION_INSERT_TEMPLATE_STATEMENT);
    }

    /**
     * Setzt das Dokument für diesen Editor. Wird während der Initialisierungsphase zunächst mit einem PlainDocument
     * bestückt, welches später dann durch ein AntlrDocument ersetzt wird. Bei jedem Wechsel werden dem neuen Dokument
     * ggf. Listener hinzugefügt und den zu entfernenden Dokumenten diese Listener wieder entzugen.
     * 
     * @param doc
     *            Das neue Dokument für diesen Editor.
     */
    @Override
    public void setDocument(final Document doc) {
        final Document oldDocument = getDocument();
        if (oldDocument != null) {
            oldDocument.removeDocumentListener(documentChangeListener);
            if (oldDocument instanceof AntlrDocument) {
                final AntlrDocument<?> oldAntlrDocument = (AntlrDocument<?>) oldDocument;
                oldAntlrDocument.removeErrorListener(errorListener);
                oldAntlrDocument.dispose();
            }
        }
        super.setDocument(doc);
        if (doc instanceof AntlrDocument) {
            final AntlrDocument<?> antlrDocument = (AntlrDocument<?>) doc;
            antlrDocument.addDocumentListener(documentChangeListener);
            antlrDocument.addErrorListener(errorListener);
        }
    }

    /**
     * Reagiert auf Änderungen an den im Dokument enhaltenen Fehlern. Informiert alle registrierten ErrorListener, dass
     * es Änderungen gegeben hat.
     * 
     * @param errors
     *            Die neue Zuordnung von Token zu ihren Fehlermeldungen.
     */
    private void errorsChanged(final Map<Token, String> errors) {
        System.out.println("neue Fehler:");

        for (final Map.Entry<Token, String> entry : errors.entrySet()) {
            System.out.println(" - " + new Position(entry.getKey()).toShortString() + ": " + entry.getValue());
        }
        errorListeners.forEach(listener -> listener.errorsChanged(errors));
    }

    /**
     * Informiert alle CaretPositionChangeListener über die Position des Cursors.
     * 
     * @param dot
     *            Indexposition, die an die Listener übermittelt werden soll.
     */
    private void fireStatusEvent(final int dot) {
        final AntlrDocument<TemplateFileContext> doc = getAntlrDocument();
        final Position pos = getPositionFromIndex(dot);
        final Token token = doc.getTokenFromIndex(dot);
        final CaretPositionChangeEvent statusEvent = new CaretPositionChangeEvent(pos, token,
                doc.getErrorMessage(token));
        caretPositionChangeListeners.stream().forEach(listener -> listener.caretPositionChanged(statusEvent));
    }

    /**
     * Ermittelt zu der Indexposition innerhalb des Dokuments die Zeile und Spalte, in der sich das Zeichen an der
     * Indexposition befindet.
     * 
     * @param index
     *            Indexpostion des Zeichens im Dokument.
     * @return Position in Form von Zeilen- und Spaltenangabe.
     */
    public Position getPositionFromIndex(final int index) {
        final Element rootElement = getAntlrDocument().getDefaultRootElement();
        final int line = rootElement.getElementIndex(index);
        final Element element = rootElement.getElement(line);
        final int column = index - element.getStartOffset();
        return new Position(line + 1, column + 1);
    }

    /**
     * Fügt den übergebenen Listener zu der Liste der Listener hinzu, die über neue Cursor-Positionen informiert werden
     * sollen.
     * 
     * @param listener
     *            Neuer Listener für Cursor-Benachristigungen.
     */
    public void addCaretPositionChangeListener(final CaretPositionChangeListener listener) {
        caretPositionChangeListeners.add(listener);
    }

    /**
     * Entfernt den übergebenen Listener von der Liste der Listener, die über Cursorbewegungen informiert werden.
     * 
     * @param listener
     *            Listener, der von der Liste gestrichen werden soll.
     */
    public void removeCaretPositionChangeListener(final CaretPositionChangeListener listener) {
        caretPositionChangeListeners.remove(listener);
    }

    /**
     * Informiert alle CaretPositionChangeListener über die aktuelle Position des Cursors.
     */
    public void fireStatusEvent() {
        fireStatusEvent(getCaret().getDot());
    }

    /**
     * Fügt einen Listener in die Liste der Listener ein, die über Änderungen an den im Dokument enthaltenen Fehlern
     * informiert werden wollen.
     * 
     * @param listener
     *            Listener, der zu der Liste hinzugefügt werden soll.
     */
    public void addErrorListener(final ErrorListener listener) {
        errorListeners.add(listener);
    }

    /**
     * Entfernt einen Listener von der Liste der Listener, die über Änderungen an den im Dokument enthaltenen Fehlern
     * informiert werden wollen.
     * 
     * @param listener
     *            Listener, der von der Liste entfernt werden soll.
     */
    public void removeErrorListener(final ErrorListener listener) {
        errorListeners.remove(listener);
    }

    /**
     * Fügt ein Paar der doppelten spitzen Klammern (&#x00ab;&#x00bb;) in das Dokument Editor ein.
     */
    public void performInsertBraces() {
        final Action action = getActionMap().get(ACTION_INSERT_BRACES);
        action.actionPerformed(new ActionEvent(this, 0, ACTION_INSERT_BRACES));
    }

    /**
     * Wird aufgerufen, wenn es Änderungen im Dokument gegeben hat. War das Dokument bislang neu oder gespeichert, wird
     * das Template darüber informiert, dass es nun ungespeicherte Änderungen enthält.
     * 
     * @param documentEvent
     *            Event, dass über die Änderungen im Dokument informiert hat.
     */
    public void notifyChange(final DocumentEvent documentEvent) {
        if (!unsavedChanges) {
            unsavedChanges = true;
            Container parent = getParent();
            while (parent != null && !(parent instanceof TemplatePanel)) {
                parent = parent.getParent();
            }
            if (parent instanceof TemplatePanel) {
                final TemplatePanel panel = (TemplatePanel) parent;
                modificationListeners.stream().forEach(listener -> listener.documentWasModified(panel));
            }
        }
    }

    /**
     * Liest die übergebene Datei ein und ersetzt den Inhalt des Editors mit dem Inhalt der Datei.
     * 
     * @param path
     *            Pfad zu der zu lesenden Datei.
     * @param charset
     *            Encosing, mit dem versucht werden soll, die Datei zu lesen.
     */
    private void loadTemplate(final Path path, final Charset charset) {
        try (InputStream fileIn = new FileInputStream(path.toFile());
                Reader reader = new InputStreamReader(fileIn, charset)) {
            read(reader, path.getFileName());
            this.path = path;
            this.charset = charset;
            unsavedChanges = false;
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage());
        }
    }

    /**
     * Speichert das in dem Editor enthaltene Dokument mit dem übergebenen Encoding in die übergebene Datei.
     * 
     * @param path
     *            Pfad zu der zu schreibenden Datei.
     * @param charset
     *            Encoding, mit der das Dokument geschrieben werden soll.
     * @throws IOException
     *             Falls beim Schreiben ein Problem auftritt.
     */
    public void saveTemplatePanel(final Path path, final Charset charset) throws IOException {
        try (OutputStream fileOut = new FileOutputStream(path.toFile());
                Writer writer = new OutputStreamWriter(fileOut)) {
            write(writer);
            this.path = path;
            this.charset = charset;
            unsavedChanges = false;
        }
    }

    /**
     * Prüft, ob der Editor ein Dokument enthält, welches ungespeicherte Änderungen enthält.
     * 
     * @return {@code true} wenn das Dokument ungespeicherte Änderungen enthält, {@code false} wenn das Dokument neu ist
     *         oder nach dem Speichern oder Laden nicht mehr verändert wurde.
     */
    public boolean hasUnsavedModifications() {
        return unsavedChanges;
    }

    /**
     * Fügt den übergebenen Listener zu der Liste der Listener hinzu, die über Änderungen an dem Dokument informiert
     * werden sollen.
     * 
     * @param listener
     *            Neuer Listener für Änderungen an dem Dokument.
     */
    public void addModificationListener(final ModificationListener listener) {
        modificationListeners.add(listener);
    }

    /**
     * Entfernt den übergebenen Listener von der Liste der Listener, die über Änderungen an dem Dokument informiert
     * werden sollen.
     * 
     * @param listener
     *            Zu löschender Listener für Änderungen an dem Dokument.
     */
    public void removeModificationListener(final ModificationListener listener) {
        modificationListeners.remove(listener);
    }

    /**
     * Gibt den Pfad zu der Datei zurück, falls diese gelsen oder schon einmal gespechert wurde.
     * 
     * @return Pfad zu der Datei, aus der das Dokument zuletzt gelesen oder in die es zuletzt gespeichert wurde.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Prüft, ob das Dokument neu ist oder bereits einmal aus einer oder in eine Datei gelesen oder geschrieben wurde.
     * 
     * @return {@code true} falls das Dokument neu ist, {@code false} falls das Dokument aus einer Datei stammt.
     */
    public boolean isNewArtifact() {
        return path == null;
    }

    /**
     * Gibt das Encoding der in dem Dokument enthaltenen Datei zurück, falls diese aus einer Datei geladen oder bereits
     * in eine Datei gespeichert wurde.
     * 
     * @return Encoding der in dem Dokument enthaltenen Datei. {@code null}, falls die Datei noch nie gespeichert wurde.
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Gibt das enthaltene Dokument als AntlrDocument zurück.
     * 
     * @return Enthaltenes Dokument als AntlrDocument.
     * @see #getDocument()
     */
    @SuppressWarnings("unchecked")
    public AntlrDocument<TemplateFileContext> getAntlrDocument() {
        return (AntlrDocument<TemplateFileContext>) getDocument();
    }

    public void undoableEditHappened(final UndoableEditEvent editEvent) {
        undoableEditListeners.forEach(listener -> listener.undoableEditHappened(editEvent));
    }

    public void undo() {
        try {
            undoManager.undo();
        } catch (final CannotUndoException cue) {
            LOGGER.info("Can not undo.", cue);
        }
    }

    public void redo() {
        try {
            undoManager.redo();
        } catch (final CannotRedoException cre) {
            LOGGER.info("Can not redo.", cre);
        }
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }

    public void addUndoableEditListener(final UndoableEditListener listener) {
        undoableEditListeners.add(listener);
    }

    public void removeUndoableEditListener(final UndoableEditListener listener) {
        undoableEditListeners.remove(listener);
    }
}