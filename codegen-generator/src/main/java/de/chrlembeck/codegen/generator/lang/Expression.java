package de.chrlembeck.codegen.generator.lang;

import org.antlr.v4.runtime.ParserRuleContext;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.Position;

/**
 * Repräsentiert einen Java-ähnlichen Ausdruck, so wie er in der ANTLR-Grammatik definiert ist.
 * 
 * @author Christoph Lembeck
 */
public interface Expression {

    /**
     * Wertet den Ausdruck auf Basis des übergebenen Modells und der aktuellen Ausführungsumgebung aus.
     * 
     * @param model
     *            Modell, welches bei der Generierung verwendet werden soll.
     * @param environment
     *            Aktuelle Ausführungsumgebung.
     * @return Ergebnis der Auswertung des Ausdrucks.
     */
    public ObjectWithType<?> evaluate(Object model, Environment environment);

    /**
     * Gibt die Position innerhalb der Template-Datei zurück, an der das Element beginnt.
     * 
     * @return Startposition des Elements innerhalb der Template-Datei aus der es gelesen wurde.
     */
    public Position getStartPosition();

    /**
     * Sucht das Objekt, auf welchem eine Methode aufgerufen oder dessen Attribut ausgelesen werden soll.
     */
    public CallSource findCallSource(Object model, Environment environment);

    /**
     * Gibt den ursprünglichen Kontext des Parsers zurück, der das Element gefunden hat.
     * 
     * @return Kontext des Parser der zur Erzeugung des Elements geführt hat.
     */
    public ParserRuleContext getContext();
}