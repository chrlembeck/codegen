package de.chrlembeck.codegen.antlreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.antlr.v4.runtime.Lexer;

import de.chrlembeck.antlr.editor.AntlrEditorKit;
import de.chrlembeck.antlr.editor.LineNumberComponent;
import de.chrlembeck.antlr.editor.TokenStyle;
import de.chrlembeck.antlr.editor.TokenStyleRepository;

/**
 * Beispielklasse für die Verwendung des AntlrEditorKits aus dem Paket antlr-editorkit. Hier wird ein kleiner Editor
 * erstellt, der XML-Dateien editieren kann. - Der Parser und der Lexer kann dabei für andere Sprache beliebig
 * ausgetauscht werden.
 * 
 * @author Christoph Lembeck
 */
public class AntlrGuiEditorExample extends JFrame {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -6751219454382974132L;

    /**
     * Führt das Progamm aus.
     * 
     * @param args
     *            Wird nicht verwendet.
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(AntlrGuiEditorExample::new);
    }

    /**
     * Erstellt den Editor.
     */
    public AntlrGuiEditorExample() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final JEditorPane editor = new JEditorPane();
        final Lexer lexer = new XMLLexer(null);
        final String startRuleName = XMLParser.ruleNames[XMLParser.RULE_document];
        final AntlrEditorKit editorKit = new AntlrEditorKit(lexer, XMLParser.class, startRuleName);
        editor.setEditorKit(editorKit);

        initStyles();

        // put it inside a scrollPane
        final JScrollPane scrollPane = new JScrollPane(editor);

        // maybe add some line numbers to the editor
        final LineNumberComponent lineNumberComponent = new LineNumberComponent(editor);
        lineNumberComponent.setForeground(Color.GRAY);
        lineNumberComponent.setCurrentLineForeground(Color.LIGHT_GRAY);
        lineNumberComponent.setFont(new Font("Consolas", Font.PLAIN, 10));
        scrollPane.setRowHeaderView(lineNumberComponent);

        add(scrollPane, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    /**
     * Legt die Farben für die Token im Editor fest.
     */
    private void initStyles() {
        final TokenStyleRepository styles = TokenStyleRepository.getInstance();
        styles.putStyle(XMLParser.COMMENT, new TokenStyle(Color.GRAY, Font.ITALIC));
        styles.putStyle(XMLParser.Name, new TokenStyle(Color.BLUE, Font.BOLD));
        styles.putStyle(XMLParser.CharRef, new TokenStyle(Color.decode("0x008000"), Font.PLAIN));
        styles.putStyle(XMLParser.EntityRef, new TokenStyle(Color.decode("0x800000"), Font.BOLD));
        styles.putStyle(XMLParser.STRING, new TokenStyle(Color.decode("0x800080"), Font.PLAIN));
    }
}