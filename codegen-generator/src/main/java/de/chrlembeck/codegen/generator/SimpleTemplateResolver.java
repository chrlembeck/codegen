package de.chrlembeck.codegen.generator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import de.chrlembeck.codegen.generator.lang.TemplateFile;
import de.chrlembeck.codegen.generator.visitor.TemplateFileVisitor;
import lang.CodeGenLexer;
import lang.CodeGenParser;

/**
 * Einfache Implementierung eines TemplateResolvers. Die Strategie bei der Auflösung der URIs in dieser Klasse basiert
 * darauf, die URI in eine URL zu überführen und einen Eingabedatenstrom von der URL zu lesen.
 *
 * @author Christoph Lembeck
 * @see TemplateResolver
 */
public class SimpleTemplateResolver implements TemplateResolver {

    /**
     * Gespeicherte Zuordnungen von resource identifiern zu bereits gelesenen Template-Dateien.
     */
    private Map<URI, TemplateFile> templateCache = new HashMap<>();

    /**
     * Haupt-identifier, von dem aus relative identifier aufgelöst werden.
     * 
     * @see URI#resolve(URI)
     */
    private final URI rootResourceIdentifier;

    /**
     * Erstellt einen neuen Resolver mit der übergebenen Template-Datei aus Ausgangs-Template. Die URI des Templates
     * muss zwingend zum Auflösen später benötigter relativer Adressen angegeben sein.
     * 
     * @param templateFile
     *            Initialie Template-Datei für die Generierung.
     */
    public SimpleTemplateResolver(final TemplateFile templateFile) {
        this.rootResourceIdentifier = Objects.requireNonNull(templateFile.getResourceIdentifier());
        templateCache.put(templateFile.getResourceIdentifier(), templateFile);
    }

    /**
     * Erstellt einen neuen Resolver mit dem übergebenen initialen resource identifier als Basis für die Suche nach
     * weiteren Templates.
     * 
     * @param rootResourceIdentifier
     *            Identifier, von dem aus relative URIs aufgelöst werden.
     * @see URI#resolve(URI)
     */
    public SimpleTemplateResolver(final URI rootResourceIdentifier) {
        this.rootResourceIdentifier = rootResourceIdentifier;
    }

    /**
     * Gibt die Template-Datei zu dem resource identifier aus dem Cache zurück oder liest diese Datei initial ein und
     * gibt sie dann zurück.
     * 
     * @param templateResourceIdentifier
     *            Identifier zum Auffinden der benötigen Template-Datei.
     * @param errorListener
     *            ErrorListener zum Empfangen möglicher Fehlermeldungen beim Lexen und Parsen der Datei.
     * @return Gelesene und übersetzte Template-Datei zum gewünschten identifier.
     * @throws IOException
     *             Falls beim Lesen der Datei ein Problem aufgetreten ist.
     */
    public TemplateFile getOrLoadTemplateFile(URI templateResourceIdentifier, final ANTLRErrorListener errorListener)
            throws IOException {
        TemplateFile templateFile = templateCache.get(templateResourceIdentifier);
        if (templateFile == null) {
            if (!templateResourceIdentifier.isAbsolute()) {
                templateResourceIdentifier = rootResourceIdentifier.resolve(templateResourceIdentifier);
            }
            templateFile = loadAndParseTemplateFile(templateResourceIdentifier, errorListener);
            templateCache.put(templateFile.getResourceIdentifier(), templateFile);
        }
        return templateFile;
    }

    /**
     * Gibt die Template-Datei zu dem resource identifier aus dem Cache zurück oder liest diese Datei initial ein und
     * gibt sie dann zurück.
     * 
     * @param templateResourceIdentifier
     *            Identifier zum Auffinden der benötigen Template-Datei.
     * @return Gelesene und übersetzte Template-Datei zum gewünschten identifier.
     * @throws IOException
     *             Falls beim Lesen der Datei ein Problem aufgetreten ist.
     */
    @Override
    public TemplateFile getOrLoadTemplateFile(final URI templateResourceIdentifier)
            throws IOException {
        return getOrLoadTemplateFile(templateResourceIdentifier, null);
    }

    /**
     * Liest die Gewünschte Template-Datei von der angegebenen URI ein und übersetzt sie in ein TemplateFile-Objekt. Zur
     * Sammlung von Fehlern, die beim Lexen oder Parsen auftreten, kann ein ErrorListener übergeben werden.
     * 
     * @param templateResourceIdentifier
     *            Identifier zur Lokalisierung der Datei.
     * @param additionalErrorListener
     *            Optionaler Listener für die beim Lexen und Parsen gefundenen Syntaxfehler.
     * @return Gelesene und übersetzte Template-Datei zum identifier.
     * @throws IOException
     *             Falls beim Lesen der Daten ein Problem auftritt.
     * @throws ParserException
     *             Falls beim Lexen oder Parsen ein Fehler festgestellt wird.
     */
    private TemplateFile loadAndParseTemplateFile(final URI templateResourceIdentifier,
            final ANTLRErrorListener additionalErrorListener) throws IOException {
        try (InputStream input = templateResourceIdentifier.toURL().openStream()) {
            final CodeGenLexer lexer = new CodeGenLexer(CharStreams.fromStream(input));
            final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            final CodeGenParser parser = new CodeGenParser(tokenStream);
            final ErrorListener errorListener = new ErrorListener();
            parser.addErrorListener(errorListener);
            if (additionalErrorListener != null) {
                parser.addErrorListener(additionalErrorListener);
            }
            final TemplateFile templateFile = parser.templateFile()
                    .accept(new TemplateFileVisitor(templateResourceIdentifier));
            final Map<Position, String> errors = errorListener.getErrors();
            if (errors.isEmpty()) {
                return templateFile;
            } else {
                final Entry<Position, String> entry = errors.entrySet().iterator().next();
                throw new ParserException(entry.getValue(), entry.getKey());
            }
        }
    }

    /**
     * Einfacher ErrorListener zum Sammeln von Fehlern beim Lexen oder Parsen einer Datei.
     * 
     * @author Christoph Lembeck
     */
    static class ErrorListener implements ANTLRErrorListener {

        /**
         * Zuordnung von Positionen, an denen Fehler gefunden wurden zu den entsprechenden Fehlermeldungen.
         */
        private Map<Position, String> errors = new TreeMap<>();

        /**
         * Gibt die Liste der Positionen, an den Fehler gefunden wurden zusammen mit den entsprechenden Fehlermeldungen
         * zurück.
         * 
         * @return Liste von Positionen und dort gefundenen Syntax-Problemen.
         */
        public Map<Position, String> getErrors() {
            return errors;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
                final int charPositionInLine,
                final String msg, final RecognitionException exception) {
            errors.put(new Position(line, charPositionInLine), msg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void reportAmbiguity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex,
                final boolean exact,
                final BitSet ambigAlts, final ATNConfigSet configs) {
            System.out.println("*** reportAmbiguity");

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void reportAttemptingFullContext(final Parser recognizer, final DFA dfa, final int startIndex,
                final int stopIndex,
                final BitSet conflictingAlts, final ATNConfigSet configs) {
            System.out.println("*** reportAttemptingFullContext");

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void reportContextSensitivity(final Parser recognizer, final DFA dfa, final int startIndex,
                final int stopIndex, final int prediction,
                final ATNConfigSet configs) {
            System.out.println("*** reportContextSensitivity");
        }
    }
}