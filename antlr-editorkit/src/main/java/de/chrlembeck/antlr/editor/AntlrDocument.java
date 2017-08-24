package de.chrlembeck.antlr.editor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Document zur Verwendung mit dem {@link AntlrEditorKit}. Dem Dokument wird ein Lexer und ein Parser übergeben, die zum
 * einen dazu dienen, die einzelnen Token des Dokumentes individuell formatieren zu können und zum Anderen eine
 * syntaktische Validierung der Eingaben durchführen. Über dabei gefundene Fehler kann man sich über enstprechende
 * ErrorListener vom Dokument informieren lassen.
 * <p>
 * See the <a href="{@docRoot}/de/chrlembeck/antlr/editor/package-summary.html#package.description">package overview</a>
 * for more information.
 * </p>
 * 
 * @param <T>
 *            Typ des vom Parser zurückgegebenen Kontext-Ojekts
 * @author Christoph Lembeck
 * @see AntlrEditorKit
 * @see AntlrEditorKit#createDefaultDocument()
 */
public class AntlrDocument<T extends ParserRuleContext> extends PlainDocument {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 3443813441348051137L;

    /**
     * Der Logger für diese Klasse.
     */
    private static Logger LOGGER = LoggerFactory.getLogger(AntlrDocument.class);

    /**
     * Aktuelle Liste der vom Lexer erkannten Token. Wird bei jeder Eingabe neu berechnet.
     */
    private List<Token> tokens;

    /**
     * Liste der aktuell von Lexer und Parser erkannten Syntax-Probleme zu einzelnen Token. Jedes Token, zu dem ein
     * Problem erkannt wurde ist hier mit einer Beschreibung des Problems hinterlegt.
     */
    private Map<Token, String> errors = new TreeMap<>(
            Comparator.<Token> nullsFirst(
                    Comparator.<Token> comparingInt(Token::getStartIndex).thenComparing(Token::getText)));

    /**
     * Referenz auf den Lexer, der zur Erzeugung der Token in dem Dokument verwendet werden soll.
     */
    private Lexer lexer;

    /**
     * Instanz des Parsers, der zur Validierung des Dokuments verwendet werden soll.
     */
    private Parser parser;

    /**
     * Referenz auf die Classe des Parsers, der zur Validierung des Dokuments verwendet werden soll.
     */
    private Class<? extends Parser> parserClass;

    /**
     * Start-Regel innerhalb der ANTLR-Grammatik, welche dazu verwendet werden kann, das komplette Dokument zu parsen
     * und dabei zu validieren.
     */
    private String ruleName;

    /**
     * TokenStream, der dem Lexer zum Lesen des Dokumentes zur Verfügung gestellt wird. Nach jeder Eingabe muss dieser
     * Stream zurückgesetzt werden können.
     */
    private CommonTokenStream tokenStream;

    /**
     * ErrorListener, der zur Registrierung von Syntaxfehlern durch den Lexer oder Parser in das Dokument dient.
     */
    private final ANTLRDocumentErrorListener errorListener;

    /**
     * Liste der eingetragenen Listener, die über neue oder behobene Fehler innerhalb des Dokumentes informiert werden
     * sollen.
     */
    private List<ErrorListener> errorListeners = new ArrayList<>();

    /**
     * Erzeugt ein neues Dokument und initialisiert es mit dem übergebenen Lexer und Parser.
     * 
     * @param lexer
     *            Lexer, der zur Erzeugung der Token für das Dokument verwendet werden soll.
     * @param parserClass
     *            Klasse des Parsers, der zur Validierung des Dokuments verwendet werden soll.
     * @param ruleName
     *            Name der ParserRule, mit der das komplette Dokument vom Parser verarbeitet werden kann.
     */
    public AntlrDocument(final Lexer lexer, final Class<? extends Parser> parserClass, final String ruleName) {
        this.lexer = lexer;
        this.parserClass = parserClass;
        this.ruleName = ruleName;
        this.tokenStream = new CommonTokenStream(lexer);
        this.parser = createParser(tokenStream);
        this.errorListener = new ANTLRDocumentErrorListener();
        this.lexer.addErrorListener(errorListener);
        this.parser.addErrorListener(errorListener);
    }

    /**
     * Entfernt den ErrorListener für dieses Dokument aus dem Lexer und dem Parser. Zukünftige Events werden dann nicht
     * mehr an dieses Dokument berichtet.
     */
    public void dispose() {
        lexer.removeErrorListener(errorListener);
        parser.removeErrorListener(errorListener);
    }

    /**
     * Erzeugt eine Teilliste der Token. Die Liste beginnt genau mit dem Token, welches das Zeichen im Dokument mit dem
     * start-Index enthält und endet mit dem Token zu dem Zeichen im end-Index.
     * 
     * @param start
     *            Position des Zeichens, zu dem das erste Token gesucht werden soll.
     * @param end
     *            Position des Zeichens, zu dem das letzte Token gesucht werden soll.
     * @return Liste von Token vom das erste Zeichen enthaltenden Token bis zum das letzte Zeichen enthaltende Token.
     */
    public List<Token> getTokenSubList(final int start, final int end) {
        if (tokens == null || tokens.isEmpty()) {
            @SuppressWarnings("unchecked")
            final List<Token> emptyList = Collections.EMPTY_LIST;
            return emptyList;
        }
        final int startIndex = TokenUtil.findTokenIndex(tokens, start);
        final int endIndex = TokenUtil.findTokenIndex(tokens, end);
        return tokens.subList(startIndex, endIndex + 1);
    }

    /**
     * Überprüft das Dokument auf lexikalische und grammatikalische Korrektheit und erzeugt eine aktuelle Liste der
     * Token für das Dokument. Der Lexer wird dabei in jedem Fall aufgerufen, um die Token für das Dokument zu erzeugen.
     * Wahlweise kann gesteuert werden, ob bei der Überprüfung auch der Parser die grammatikalische Korrektheit
     * überprüft.
     * 
     * @param runParser
     *            true, falls auch der Parser nach Fehlern in dem Dokument suchen soll, false, falls nur der Lexer das
     *            Dokument prüfen soll.
     * @return Vom Parser zurückgegebener RuleContext. Wird der Parser nicht aufgerufen, ist das Ergebniss null.
     */
    public T validate(final boolean runParser) {
        LOGGER.trace("start validating " + (runParser ? "with" : "without") + " parsing.");
        final List<Token> newTokens = new ArrayList<Token>();
        T result = null;
        try {
            errorListener.reset();
            final String text = getText(0, getLength());
            errors.clear();
            lexer.setInputStream(CharStreams.fromString(text));
            int currentPos = 0;

            // TokenStream resetten durch Setzen der TokenSource
            tokenStream.setTokenSource(lexer);
            // Alle Token einlesen
            tokenStream.fill();
            for (final Token nextToken : tokenStream.getTokens()) {
                newTokens.add(nextToken);
                if (nextToken.getStartIndex() != currentPos) {
                    LOGGER.error("Missing Token: currentPos=" + currentPos + ", nextToken=" + nextToken);
                }
                currentPos = nextToken.getStopIndex() + 1;
            }

            if (runParser) {
                parser.reset();
                result = callParser();
            }
            for (final Entry<Integer, String> error : errorListener.getErrors().entrySet()) {
                final int tokenIndex = TokenUtil.findTokenIndex(newTokens, error.getKey());
                if (tokenIndex < newTokens.size()) {
                    final Token token = newTokens.get(tokenIndex);
                    this.errors.put(token, error.getValue());
                }
            }
            fireErrorsChangedEvent();
        } catch (final BadLocationException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            tokens = newTokens;
        }
        return result;
    }

    /**
     * Informiert alle eingetragenen ErrorListener, dass es eine Änderung an den im Dokument gefundenen Fehlern gegeben
     * hat.
     */
    private void fireErrorsChangedEvent() {
        errorListeners.stream().forEach(l -> l.errorsChanged(this.errors));
    }

    /**
     * Ruft den Parser zur Überprüfung des Dokumentes auf.
     * 
     * @return vom Parser zurückgegebener RuleContext.
     */
    private T callParser() {
        try {
            final Method method = parserClass.getMethod(ruleName);
            @SuppressWarnings("unchecked")
            final T ruleContext = (T) method.invoke(parser);
            return ruleContext;
        } catch (final NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Informiert alle eingetragenen DocumentListener über Änderungen an dem Dokument. Anschließend werden die neuen
     * Token für das Doument erzeugt und das Dokument nach Syntaxfehlern durchsucht.
     */
    @Override
    protected void fireChangedUpdate(final DocumentEvent e) {
        super.fireChangedUpdate(e);
        validate(true);
    }

    /**
     * Informiert alle eingetragenen DocumentListener über eine Einfügeoperation in das Dokument. Anschließend werden
     * die neuen Token für das Doument erzeugt und das Dokument nach Syntaxfehlern durchsucht.
     */
    @Override
    protected void fireInsertUpdate(final DocumentEvent e) {
        super.fireInsertUpdate(e);
        validate(true);
    }

    /**
     * Informiert alle eingetragenen DocumentListener über eine Löschung in dem Dokument. Anschließend werden die neuen
     * Token für das Doument erzeugt und das Dokument nach Syntaxfehlern durchsucht.
     */
    @Override
    protected void fireRemoveUpdate(final DocumentEvent e) {
        super.fireRemoveUpdate(e);
        validate(true);
    }

    /**
     * Gibt zurück, ob für das übergebene Token aktuell ein Fehler registriert ist.
     * 
     * @param token
     *            Token, für das geprüft werden soll, ob für dieses ein Fehler erkannt wurde.
     * @return true, falls das Token einen Fehler im Lexer oder Parser erzeugt hat, false, falls kein Fehler bekannt
     *         ist.
     */
    public boolean hasError(final Token token) {
        return errors.containsKey(token);
    }

    /**
     * Fügt dem Dokument eine Fehlermeldung für das Token an der angegebenen Textposition hinzu.
     * 
     * @param startIndex
     *            Index im Dokument, an dem der Fehler aufgetreten ist.
     * @param message
     *            Fehlermeldung, die das Problem genauer beschreibt.
     */
    public void addError(final int startIndex, final String message) {
        final int tokenIndex = TokenUtil.findTokenIndex(tokens, startIndex);
        final Token token = tokens.get(tokenIndex);
        errors.put(token, message);
        super.fireChangedUpdate(
                new DefaultDocumentEvent(startIndex, token.getStopIndex() - token.getStartIndex(), EventType.CHANGE));
        fireErrorsChangedEvent();
    }

    /**
     * Listener zur Registrierung der Syntaxfehler durch Lexer und Parser im Dokument.
     *
     * @author Christoph Lembeck
     */
    private class ANTLRDocumentErrorListener implements ANTLRErrorListener {

        /**
         * Speichert Fehlermeldungen zu Start-Indizes.
         */
        private Map<Integer, String> errors = new TreeMap<>();

        /**
         * Gibt die bislang gesammelten Fehlermeldungen zurück.
         * 
         * @return Liste von gesammelten Fehlermeldungen zu den einzelnen Positionen, an denen sie aufgetreten sind.
         */
        public Map<Integer, String> getErrors() {
            return errors;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.antlr.v4.runtime.ANTLRErrorListener#syntaxError(org.antlr.v4.runtime.Recognizer, java.lang.Object,
         *      int, int, java.lang.String, org.antlr.v4.runtime.RecognitionException)
         */
        @Override
        public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
                final int charPositionInLine, final String msg, final RecognitionException e) {
            final int startIndex = getIndexFromLineAndPositionInLine(line - 1, charPositionInLine);
            errors.put(startIndex, msg);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.antlr.v4.runtime.ANTLRErrorListener#reportAmbiguity(org.antlr.v4.runtime.Parser,
         *      org.antlr.v4.runtime.dfa.DFA, int, int, boolean, java.util.BitSet,
         *      org.antlr.v4.runtime.atn.ATNConfigSet)
         */
        @Override
        public void reportAmbiguity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex,
                final boolean exact,
                final BitSet ambigAlts, final ATNConfigSet configs) {
            System.out.println("REPORT AMBIGUITY ");
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.antlr.v4.runtime.ANTLRErrorListener#reportAttemptingFullContext(org.antlr.v4.runtime.Parser,
         *      org.antlr.v4.runtime.dfa.DFA, int, int, java.util.BitSet, org.antlr.v4.runtime.atn.ATNConfigSet)
         */
        @Override
        public void reportAttemptingFullContext(final Parser recognizer, final DFA dfa, final int startIndex,
                final int stopIndex,
                final BitSet conflictingAlts, final ATNConfigSet configs) {
            System.out.println("ATTEMPTING FULL CONTEXT ");
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.antlr.v4.runtime.ANTLRErrorListener#reportContextSensitivity(org.antlr.v4.runtime.Parser,
         *      org.antlr.v4.runtime.dfa.DFA, int, int, int, org.antlr.v4.runtime.atn.ATNConfigSet)
         */
        @Override
        public void reportContextSensitivity(final Parser recognizer, final DFA dfa, final int startIndex,
                final int stopIndex, final int prediction,
                final ATNConfigSet configs) {
            System.out.println("CONTEXT SENSITIVITY ");
        }

        /**
         * Löscht alle bislang gesammelten Fehlermeldungen.
         */
        public void reset() {
            errors.clear();
        }
    }

    /**
     * Gibt die Zeile zurück, in der das Zeichen mit dem angegebenen Index steht.
     * 
     * @param index
     *            Index des Zeichens, dessen Zeile gesucht werden soll. Das erste Zeichen hat den Index 0.
     * @return Zeile, in der das Zeichen mit dem Index steht. Die erste Zeile hat die Zeilennummer 0.
     */
    public int getLineFromIndex(final int index) {
        final Element rootElement = getDefaultRootElement();
        final int line = rootElement.getElementIndex(index);
        return line;
    }

    /**
     * Gibt die Position des Zeichens mit dem angegebenen Index innerhalb der bereits bekannten Zeile line zurück.
     * 
     * @param index
     *            Index des zu suchenden Zeichens (Erstes Zeichen hat Index 0).
     * @param line
     *            Zeile, in der sich das Zeichen befindet.
     * @return Position des Zeichens innerhalb der Zeile. Das erste Zeichen der Zeile hat den Index 0.
     */
    public int getColumnFromIndexAndLine(final int index, final int line) {
        final Element rootElement = getDefaultRootElement();
        final Element element = rootElement.getElement(line);
        final int column = index - element.getStartOffset();
        return column;
    }

    /**
     * Sucht nach dem Token, der das Zeichen an dem übergebenen Index enthält, und gibt dieses zurück.
     * 
     * @param index
     *            Index des Zeichens, zu dem das Token, dass es enthält, gesucht werden soll.
     * @return Token an der Position des Zeichens oder null, falls kein solches ermittelt werden kann.
     */
    public Token getTokenFromIndex(final int index) {
        if (tokens == null || tokens.isEmpty()) {
            return null;
        }
        final int pos = TokenUtil.findTokenIndex(tokens, index);
        return pos < tokens.size() ? tokens.get(pos) : null;
    }

    /**
     * Gibt den zum Erzeugen der Token hinterlegten Lexer für dieses Dokument aus.
     * 
     * @return Lexer, der verwendet wird, die Token für das Dokument zu erzeugen.
     */
    public Lexer getLexer() {
        return lexer;
    }

    /**
     * Prüft, ob zu dem Token im Dokument eine Fehlermeldung hinterlegt ist, und gibt diese dann zurück.
     * 
     * @param token
     *            Token, zu dem nach einer Fehlermeldung gesucht werden soll.
     * @return Fehlermeldung zu dem Token oder null, falls kein Fehler zu dem Token registriert wurde.
     */
    public String getErrorMessage(final Token token) {
        if (token == null) {
            return null;
        }
        return errors.get(token);
    }

    /**
     * Löscht die Zeile, in der sich die Textposition befindet.
     * 
     * @param index
     *            Index des Zeichens, dessen komplette Zeile entfernt werden soll.
     * @throws BadLocationException
     *             Falls der Index für das Dokument ungültig ist.
     */
    public void removeLineAtIndex(final int index)
            throws BadLocationException {
        final Element e = getParagraphElement(index);
        final int startOffset = e.getStartOffset();
        remove(startOffset, Math.min(getLength(), e.getEndOffset() - startOffset));
    }

    /**
     * Berechnet die Index-Position des Zeichens, das sich in der angegebenen Zeile und der angegebenen Spalte des
     * Dokuments befindet.
     * 
     * @param line
     *            Zeile, in der sich das Zeichen befinden soll (beginnend bei 0).
     * @param charPositionInLine
     *            Spalte innerhalb der Zeile, in der sich das Zeichen befindet (beginnend bei 0).
     * @return Index des gesuchten Zeichens innerhalb des Dokuments.
     */
    public int getIndexFromLineAndPositionInLine(final int line, final int charPositionInLine) {
        final Element rootElement = getDefaultRootElement();
        final Element lineElement = rootElement.getElement(line);
        final int startOffset = lineElement.getStartOffset();
        return startOffset + charPositionInLine;
    }

    /**
     * Prüft, ob für ein beliebiges Token in dem Dokument eine Fehlermeldung registriert ist.
     * 
     * @return true, falls es ein Token mit einer Fehlermeldung gibt, false, falls gar keine Fehlermeldung existiert.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Gibt eine Liste von Token mit den dazugehörigen, bislang gefundenen Fehlermeldungen zurück.
     * 
     * @return Liste von Token mit den zu ihnen bislang gesammelten Fehlermeldungen.
     */
    public Map<Token, String> getErrors() {
        return errors;
    }

    /**
     * Erstellt einen neuen Parser anhand der in der Klasse hinterlegten Parser-Klasse und initialisiert ihn mit dem
     * übergebenen TokenStream.
     * 
     * @param input
     *            TokenStream, aus dem der Parser bei der Validierung die Token lesen kann. Dieser Stream sollte nicht
     *            für andere Zwecke verwendet werden, da er bei jeder Validierung erneut zurückgesetzt wird.
     * @return Neuer Parser zur Validierung des Dokuments.
     * @see #parserClass
     */
    private Parser createParser(final TokenStream input) {
        try {
            final Constructor<? extends Parser> constructor = parserClass.getConstructor(TokenStream.class);
            final Parser parser = constructor.newInstance(input);
            return parser;
        } catch (final SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Fügt dem Dokument einen neuen ErrorListener zur Information über Änderungen an den Fehlern hinzu.
     * 
     * @param errorListener
     *            Neuer Listener für Änderungen an den Fehlern.
     */
    public void addErrorListener(final ErrorListener errorListener) {
        this.errorListeners.add(errorListener);
    }

    /**
     * Entfernt einen eingetragenen ErrorListener von dem Dokument.
     * 
     * @param errorListener
     *            Listener, der aus dem Dokument entfernt werden soll.
     */
    public void removeErrorListener(final ErrorListener errorListener) {
        this.errorListeners.remove(errorListener);
    }
}