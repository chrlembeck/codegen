package de.chrlembeck.antlr.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JEditorPane;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.ViewFactory;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;

/**
 * EditorKit für die Darstellung von Dokumenten, die anhand einer ANTLR-Syntax-Definition geparsed werden können. Das
 * EditorKit ist für die Verwendung in einem JEditorPane vorgesehen.
 * 
 * @author Christoph Lembeck
 */
public class AntlrEditorKit extends DefaultEditorKit implements ViewFactory {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -7883379484816131093L;

    /**
     * Referenz auf den Lexer, der Token zu dem anzuzeigenden Dokument erzeugen kann. Der Lexer muss dabei so
     * spezifiziert sein, dass er zu jeder Eingabe eine durchgehende Liste von Token produzieren kann. Das beinhaltet
     * dabei auch das Vorsehen von Token für Eingaben die syntaktisch nicht korrekt sind oder Whitespace. Token, die für
     * die eigentliche Sprache nicht benötigt werden, können dabei in den HIDDEN-Channel geschrieben werden.
     */
    private Lexer lexer;

    /**
     * Name der Klasse, die den eigentlichen Parser der Sprache für das anzuzeigende Dokument beinhaltet.
     */
    private Class<? extends Parser> parserClass;

    /**
     * Name der Start-Rule. Dies ist die ParserRule, die das komplette Dokument definiert und welche zur Validierung der
     * kompletten Eingabe in den Editor verwendet werden soll.
     */
    private String startRuleName;

    /**
     * Erzeugt ein neues EditorKit zur Verwendung in einem JEditorPane.
     * 
     * @param lexer
     *            Lexer, der zum Erzeugen von Token zu dem eingegebenen Dokument verwendet werden soll.
     * @param parserClass
     *            Name der Parser-Klasse, die zur Validierung des Dokumentes benutzt werden kann.
     * @param startRuleName
     *            Name der Parser-Rule, die bei der Validierung der Eingabe verwendet werden soll.
     * @see #lexer
     * @see #parserClass
     * @see #startRuleName
     * @see JEditorPane
     */
    public AntlrEditorKit(final Lexer lexer, final Class<? extends Parser> parserClass, final String startRuleName) {
        this.lexer = lexer;
        this.parserClass = parserClass;
        this.startRuleName = startRuleName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AntlrView create(final Element elem) {
        return new AntlrView(elem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewFactory getViewFactory() {
        return this;
    }

    /**
     * Erzeugt ein neues {@link AntlrDocument}, welches in dem Editor verwendet werden soll.
     */
    @Override
    public AntlrDocument createDefaultDocument() {
        return new AntlrDocument(lexer, parserClass, startRuleName);
    }

    /**
     * Installiert dieses EditorKit in dem übergebenen Editor.
     */
    @Override
    public void install(final JEditorPane editorPane) {
        super.install(editorPane);
        final int resolution = Toolkit.getDefaultToolkit().getScreenResolution();
        final Font font = new Font("Consolas", Font.PLAIN, 10 * resolution / 72);
        editorPane.setFont(font);
        editorPane.setBackground(Color.WHITE);
        editorPane.setSelectionColor(new Color(200, 200, 255));
        editorPane.getCaret().setBlinkRate(0);
    }
}