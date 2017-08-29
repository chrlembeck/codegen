package de.chrlembeck.codegen.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import org.antlr.v4.runtime.Token;

import de.chrlembeck.antlr.editor.AntlrDocument;
import de.chrlembeck.antlr.editor.LineNumberComponent;
import de.chrlembeck.antlr.editor.TokenStyle;
import de.chrlembeck.antlr.editor.TokenStyleRepository;
import de.chrlembeck.codegen.generator.lang.TemplateFile;
import de.chrlembeck.codegen.generator.visitor.TemplateFileVisitor;
import lang.CodeGenLexer;
import lang.CodeGenParser.TemplateFileContext;

/**
 * EditorPanel für das Bearbeiten von Template-Dateien inklusive einer umgebenden ScrollPane.
 *
 * @author Christoph Lembeck
 */
public class TemplatePanel extends JScrollPane implements TabComponent {

    /**
     * Initialisiert das Syntax-Highlighting für den Code-Editor.
     */
    static {
        final TokenStyleRepository styles = TokenStyleRepository.getInstance();
        final TokenStyle keywordStyle = new TokenStyle(new Color(127, 0, 85), Font.BOLD);
        final TokenStyle javaPrimaryType = new TokenStyle(new Color(180, 0, 60), Font.BOLD);
        final TokenStyle stringLiteral = new TokenStyle(new Color(42, 0, 255), Font.PLAIN);
        final TokenStyle errorStyle = new TokenStyle(new Color(220, 0, 0), Font.ITALIC);
        styles.putStyle(CodeGenLexer.BlockComment, new TokenStyle(new Color(63, 127, 95), Font.ITALIC));
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
    }

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -7441639849404315523L;

    /**
     * Referenz auf die in dieser Komponente entheltene EditorPane.
     */
    private TemplateEditorPane<TemplateFileContext> editorPane;

    /**
     * Erzeugt ein neues TemplatePanel mit den übergebenen Daten.
     * 
     * @param path
     *            Pfad zu der Datei, die initial in den Editor geladen werden soll oder null, falls der Editor leer sein
     *            soll.
     * @param charset
     *            Encoding der Datei, die initial in den Editor geladen werden soll oder null, falls der Editor leer
     *            sein soll.
     */
    public TemplatePanel(final Path path, final Charset charset) {
        editorPane = new TemplateEditorPane<TemplateFileContext>(path, charset);
        setViewportView(editorPane);
        initLineNumberComponent();
    }

    /**
     * Initialisiert die Componente, die die Zeilennummern links neben dem Editortext anzeigt.
     */
    private void initLineNumberComponent() {
        final LineNumberComponent textLineNumbers = new LineNumberComponent(editorPane);
        textLineNumbers.setForeground(Color.GRAY);
        textLineNumbers.setCurrentLineForeground(Color.LIGHT_GRAY);
        final int resolution = Toolkit.getDefaultToolkit().getScreenResolution();
        textLineNumbers.setFont(new Font("Consolas", Font.PLAIN, 10 * resolution / 72));
        this.setRowHeaderView(textLineNumbers);
    }

    /**
     * Gibt den im Editor enthaltenen Text zurück.
     * 
     * @return Aktueller Text im Editor.
     * @see JEditorPane#getText()
     */
    public String getText() {
        return editorPane.getText();
    }

    /**
     * Gibt die enthaltene EditorPane zurück.
     * 
     * @return referenz auf die enthaltene EditorPane.
     */
    public TemplateEditorPane<TemplateFileContext> getEditorPane() {
        return editorPane;
    }

    /**
     * Prüft, ob für ein beliebiges Token in dem Dokument eine Fehlermeldung registriert ist.
     * 
     * @return true, falls es ein Token mit einer Fehlermeldung gibt, false, falls gar keine Fehlermeldung existiert.
     */
    public boolean hasErrors() {
        return editorPane.getAntlrDocument().hasErrors();
    }

    /**
     * Ruft die Validierung des enthaltenen Dokuments auf und aktualisiert so die angezeigten Fehlermeldungen.
     */
    public void validateTemplate() {
        editorPane.getAntlrDocument().validate(true);
    }

    /**
     * List den Inhalt des Editors aus, parsed diesen und gibt die enthaltenen Template-Definitionen als
     * TemplateFile-Objekt zurück.
     * 
     * @return TemplateFile-Objekt aus dem Inhalt des Editors.
     */
    public TemplateFile getTemplateFile() {
        final AntlrDocument<TemplateFileContext> antlrDocument = editorPane.getAntlrDocument();
        final TemplateFileContext fileContext = antlrDocument.validate(true);
        final TemplateFile templateFile = fileContext.accept(new TemplateFileVisitor(getPath().toUri()));
        return templateFile;
    }

    /**
     * Gibt eine Liste von Token mit den dazugehörigen, bislang gefundenen Fehlermeldungen zurück.
     * 
     * @return Liste von Token mit den zu ihnen bislang gesammelten Fehlermeldungen.
     */
    public Map<Token, String> getErrors() {
        return editorPane.getAntlrDocument().getErrors();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUnsavedModifications() {
        return editorPane.hasUnsavedModifications();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNewArtifact() {
        return editorPane.isNewArtifact();
    }

    /**
     * Gibt den Pfad zu der Datei zurück, falls diese gelsen oder schon einmal gespechert wurde.
     * 
     * @return Pfad zu der Datei, aus der das Dokument zuletzt gelesen oder in die es zuletzt gespeichert wurde.
     */
    public Path getPath() {
        return editorPane.getPath();
    }

    /**
     * Gibt das Encoding der in dem Dokument enthaltenen Datei zurück, falls diese aus einer Datei geladen oder bereits
     * in eine Datei gespeichert wurde.
     * 
     * @return Encoding der in dem Dokument enthaltenen Datei. {@code null}, falls die Datei noch nie gespeichert wurde.
     */
    public Charset getCharset() {
        return editorPane.getCharset();
    }
}