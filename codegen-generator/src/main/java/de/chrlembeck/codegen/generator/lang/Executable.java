package de.chrlembeck.codegen.generator.lang;

import java.io.IOException;

import org.antlr.v4.runtime.ParserRuleContext;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.Generator;
import de.chrlembeck.codegen.generator.Position;

/**
 * Interface für alle Statements, die vom Generator direkt ausgeführt werden können.
 * 
 * @author Christoph Lembeck
 *
 * @param <T>
 *            Typ des ParserRuleContexts, der vom Parser zu diesem Element der Template-Datei erzeugt wurde.
 */
public interface Executable<T extends ParserRuleContext> {

    /**
     * Führt das Statement in der übergebenen Umgebung aus.
     * 
     * @param generator
     *            Generator, der die Ausführung angestoßen hat.
     * @param model
     *            Aktuelles Modell oder Teil des Modells, das durch das Statement verarbeitet werden soll
     * @param environment
     *            Laufzeitumgebung mit den einthaltenen Variablen.
     * @throws IOException
     *             Falls beim Schreiben der Ausgabe ein Fehler auftritt.
     */
    void execute(Generator generator, Object model, Environment environment) throws IOException;

    /**
     * Gibt den ParserRuleContext zu dem Statement zurück.
     * 
     * @return ParserRuleContext, so wie der Parser ihn bei der Erkennung des Elements erzeugt hat.
     */
    T getContext();

    /**
     * Gibt die Position des ersten Zeichens innerhalb der Template-Datei zurück, das zu diesem Statement gehört.
     * 
     * @return Position des erten Zeichens des Statements in der Template-Datei.
     */
    Position getStartPosition();

    /**
     * Gibt die Referenz auf das TemplateFile zurück, in dem das Statement enthalten ist.
     * 
     * @return TemplateFile, in dem dieses Element enthalten ist.
     */
    TemplateFile getTemplateFile();
}