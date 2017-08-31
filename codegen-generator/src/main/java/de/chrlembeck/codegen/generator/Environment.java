package de.chrlembeck.codegen.generator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import de.chrlembeck.codegen.generator.lang.Executable;
import de.chrlembeck.codegen.generator.lang.ObjectWithType;

/**
 * Ausführungsumgebung mit den Laufzeitinformationen während der Ausführung eines Templates einer Template-Datei.
 *
 * @author Christoph Lembeck
 */
public class Environment {

    /**
     * Stack der aktuell bei der Ausführung angefallenen Frames. Verschiedene Statements können entscheiden, ob sie z.B.
     * einen neuen Variablen-Scope benötigen und daher einen neuen Frame auf den Stack legen. Die Statements sind dann
     * selbst in der Verantwortung, diese Frames auch passend wieder zu entfernen.
     */
    private final Stack<Frame> frameStack = new Stack<>();

    /**
     * Speichert den aktuellen Aufrufstack. Anhand des Stacks kann zurückverfolgt werden, woher letztendlich der Aufruf
     * der aktuellen Komponente kommt und welche Template-Definitionen zwischenzeitlich aufgerufen wurden.
     */
    private final Stack<Executable<?>> generatorStack = new Stack<>();

    /**
     * Legt einen neuen Frame auf den Stack und verknüpft ihn, falls gewünscht, mit dem vorangegangenen Frame. Eine
     * Verknüpfung ist dann notwendig, wenn der Frame Zugriff auf die Daten des vorangegangenen Frames erlauben möchte.
     * 
     * @param inheritVariables
     *            Legt fest, ob der neue Frame Zugriff auf die Variablen des vorangegangenen Frames bekommen soll.
     * @see Environment#frameStack
     */
    public void createFrame(final boolean inheritVariables) {
        if (inheritVariables) {
            frameStack.push(new Frame(frameStack.isEmpty() ? null : frameStack.peek()));
        } else {
            frameStack.push(new Frame(null));
        }
    }

    /**
     * Setzt in dem aktuellen Frame einen neuen Wert für die übergebene Variable. Alle Statements, die später auf dem
     * gleichen (oder erbenden) Frame ausgeführt werden, können den Wert dieser Variable nutzen.
     * 
     * @param varName
     *            Name der Variable.
     * @param item
     *            Wert der Variable.
     * @param type
     *            Laufzeittyp der Variable. Dieser ist besonders bei primitiven Datentypen wichtig, da er nicht aus den
     *            Wrapper-Klassen abgeleitet werden kann.
     */
    public void addVariable(final String varName, final Object item, final Class<?> type) {
        getCurrentFrame().addVariable(varName, item, type);
    }

    /**
     * Sucht im aktuellen Frame nach der übergebenen Variable und gibt bei Erfolg den zu ihr gespeicherten Wert zurück.
     * 
     * @param varName
     *            Name der zu suchenden Variable.
     * @return Wert der Variable, falls diese im Frame bekannt ist.
     */
    public ObjectWithType<?> lookupVariable(final String varName) {
        return getCurrentFrame().lookupVariable(varName);
    }

    /**
     * Prüft, ob die übergebene Variable in dem aktuell gültigen Frame bekannt ist.
     * 
     * @param name
     *            Name der zu suchenden Variablen.
     * @return true, falls der Frame die Variable kennt, false, falls die Variable unbekannt ist.
     */
    public boolean containsVariable(final String name) {
        return getCurrentFrame().containsVariable(name);
    }

    /**
     * Gibt den aktuell gültigen Frame vom Stack zurück.
     * 
     * @return Für die aktuelle Umgebung gültiger Frame.
     */
    public Frame getCurrentFrame() {
        return frameStack.peek();
    }

    /**
     * Entfernt den aktuellen Frame vom Frame-Stack.
     * 
     * @see #frameStack
     */
    public void dropFrame() {
        frameStack.pop();
    }

    /**
     * Führt einen Code-Teil oder ein Statement aus und protokolliert diesen Aufruf auf dem GeneratorStack.
     * 
     * @param codeOrStatement
     *            Code oder Statement, welcher oder welches ausgeführt werden soll.
     * @param generator
     *            Generator, der die Ausführung angestoßen hat.
     * @param model
     *            Modell oder Teil des Modells, welches gerade verarbeitet wird.
     * @throws IOException
     *             Falls bei der Ausgabe der Artefakte ein Problem auftritt.
     * @see Environment#generatorStack
     */
    public void execute(final Executable<?> codeOrStatement, final Generator generator, final Object model)
            throws IOException {
        generatorStack.push(codeOrStatement);
        codeOrStatement.execute(generator, model, this);
        generatorStack.pop();
    }

    /**
     * Gibt den Stack der Aufrufe zurück, die zur Ausführung des aktuellen Teils des Templates geführt haben.
     * 
     * @return Aufrufstack vom Beginn der Ausführung bis zum aktuellen Knoten.
     * @see #generatorStack
     */
    public List<Executable<?>> getGeneratorStack() {
        return Collections.unmodifiableList(generatorStack);
    }
}