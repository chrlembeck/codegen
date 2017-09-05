package de.chrlembeck.codegen.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import org.antlr.v4.runtime.Token;

import de.chrlembeck.antlr.editor.AntlrDocument;
import de.chrlembeck.antlr.editor.LineNumberComponent;
import de.chrlembeck.antlr.editor.TokenStyleRepository;
import de.chrlembeck.codegen.generator.lang.TemplateFile;
import de.chrlembeck.codegen.generator.visitor.TemplateFileVisitor;
import de.chrlembeck.codegen.grammar.CodeGenParser.TemplateFileContext;

/**
 * EditorPanel für das Bearbeiten von Template-Dateien inklusive einer umgebenden ScrollPane.
 *
 * @author Christoph Lembeck
 */
public class TemplatePanel extends JScrollPane implements TabComponent {

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
     * @param tokenStyles
     *            Darstellungsregeln für die Token in den Template-Editoren.
     */
    public TemplatePanel(final Path path, final Charset charset, final TokenStyleRepository tokenStyles) {
        editorPane = new TemplateEditorPane<TemplateFileContext>(path, charset, tokenStyles);
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
        final TemplateFile templateFile = fileContext
                .accept(new TemplateFileVisitor(getPath() == null ? null : getPath().toUri()));
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
    @Override
    public Path getPath() {
        return editorPane.getPath();
    }

    /**
     * Gibt das Encoding der in dem Dokument enthaltenen Datei zurück, falls diese aus einer Datei geladen oder bereits
     * in eine Datei gespeichert wurde.
     * 
     * @return Encoding der in dem Dokument enthaltenen Datei. {@code null}, falls die Datei noch nie gespeichert wurde.
     */
    @Override
    public Charset getCharset() {
        return editorPane.getCharset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveDocument(final Path path, final Charset charset) throws IOException {
        getEditorPane().saveTemplatePanel(path, charset);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addModificationListener(final ModificationListener listener) {
        getEditorPane().addModificationListener(listener);
    }
}