package de.chrlembeck.codegen.generator.output;

import java.io.IOException;

/**
 * Verwaltet die Ausgabekanäle für einen Generator-Lauf. Die Ausgabe des Generators erfolgt dabei in verschiedene, durch
 * die Templates vorgegebene Channels, für die der GeneratorOutput jeweils einen eigenen Writer zur Verfügung stellen
 * muss. Im Falle einer Ausgabe in Dateien sollte so ein Channel mit dem Namen "org/test/MyClass.java" einen Writer
 * bekommen, der die Ausgabe des Generator genau in die Datei MyClass.java im Verzeichnis org/test ablegt.
 * 
 * @author Christoph Lembeck
 */
public interface GeneratorOutput {

    /**
     * Gibt einen Writer zurück, in den der Generator die Ausgaben für den übergebenen Ausgabe-Channel schreiben kann.
     * 
     * @param channelName
     *            Name des Channels, in den die Ausgabe geschrieben werden soll (z.B. "/org/test/MyClass.java").
     * @param prefs
     *            OutputPreferences, die die Art der Ausgabe beeinflussen können.
     * @return Writer-Objekt, in das der Generator die Ausgaben für den Channel schreiben kann.
     * @throws IOException
     *             Falls bei der Erstellung des Writers ein Fehler aufgetreten ist.
     */
    GeneratorWriter getWriter(String channelName, OutputPreferences prefs) throws IOException;

    /**
     * Informiert alle Ausgabekanäle darüber, dass die Ausgabedatenströme geschlossen werden können.
     */
    void closeAll();
}