package de.chrlembeck.codegen.generator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import de.chrlembeck.codegen.generator.lang.TemplateFile;

/**
 * Ein TemplateResolver wird dazu benötigt, von einer Template-Datei referenzierte Templates laden zu können. Die
 * referenzierten Tamplates werden dabei in der Template-Datei in den Import-Statements über einen relativen URI
 * beschrieben. Bei der Ausführung der Templates kann es sein, dass eine so Referenzierte Datei nachgeladen werden muss.
 * Der TemplateResolver wird dann dazu benötigt, den relativen URI aufzulösen und die benötigte Template-Datei nach zu
 * laden.
 * 
 * @author Christoph Lembeck
 */
public interface TemplateResolver {

    /**
     * Löst die übergene URI, die die Position des zu ladenden Templates beschreibt, auf und lädt das entsprechende
     * Template. Die URI kann dabei eine relative Adresse enthalten, die ausgehend von der Position der ursprünglichen
     * Template-Datei in eine absolute Adresse überführt werden muss.
     * 
     * @param templateResourceIdentifier
     *            Beschreibung des Orts (relativ oder absolut), an dem die zu ladende Template-Datei gesucht werden
     *            soll.
     * @return Geladene und geparste Template-Datei, die an der beschreibenen Stelle gefunden wurde.
     * @throws MalformedURLException
     *             Falls der übergebene URI nicht als URI erkannt werden konnte.
     * @throws IOException
     *             Falls beim Lesen der Tamplate-Datei ein Fehler aufgetreten ist.
     */
    public TemplateFile loadTemplateFile(URI templateResourceIdentifier) throws MalformedURLException, IOException;
}