package de.chrlembeck.codegen.generator;

import java.io.IOException;
import java.net.URI;

import de.chrlembeck.codegen.generator.lang.TemplateFile;
import de.chrlembeck.codegen.generator.lang.TemplateStatement;
import de.chrlembeck.codegen.generator.output.GeneratorOutput;
import de.chrlembeck.codegen.generator.output.GeneratorWriter;
import de.chrlembeck.codegen.generator.output.OutputPreferences;

/**
 * Der eigentliche Codegenerator. Er überführt einzelne Templates einer Template-Datei zusammen mit einem passenden
 * Modell in ein oder mehrere in dem Template beschriebene Artefakte.
 * 
 * @author Christoph Lembeck
 */
public class Generator {

    /**
     * Verwalter für die Ausgaben des Generators.
     */
    private GeneratorOutput output;

    /**
     * Detaileinstellungen zu der Art und Weise der Ausgaben.
     */
    private OutputPreferences outputPreferences;

    /**
     * Resolver für die Templates, die während der Generierung geladen oder gegebenenfalls durch Imports nachgeladen
     * werden müssen.
     */
    private TemplateResolver templateResolver;

    /**
     * Speichert den aktuell gültigen Ausgabekanal für die Generierung der Artefakte.
     */
    private GeneratorWriter currentWriter;

    /**
     * Erstellt einen neuen Generator und übergibt ihm die benötigten Einstellungen.
     * 
     * @param resolver
     *            Resolver für das Nachladen der Template-Dateien.
     * @param output
     *            Verwalter für die Ausgaben des Generators.
     * @param outputPreferences
     *            Einstellungen zu den Ausgaben.
     */
    public Generator(final TemplateResolver resolver, final GeneratorOutput output,
            final OutputPreferences outputPreferences) {
        this.templateResolver = resolver;
        this.output = output;
        this.outputPreferences = outputPreferences;
    }

    /**
     * Lädt eine Template-Datei anhand ihres Identifiers, sucht darin nach dem übergebenen Template-Namen und wendet das
     * Template auf das übergebene Model an.
     * 
     * @param templateResourceIdentifier
     *            Identifer, mit Hilfe dessen der templateResolver das Template laden kann.
     * @param templateName
     *            Name des Templates, dass der Generator ausführen soll.
     * @param model
     *            Modell, dass durch das Template bearbeitet werden soll.
     * @throws IOException
     *             Falls bei der Ausgabe ein Problem auftritt.
     */
    public void generate(final URI templateResourceIdentifier, final String templateName, final Object model)
            throws IOException {
        final TemplateFile templateFile = templateResolver.getOrLoadTemplateFile(templateResourceIdentifier);
        generate(templateFile, templateName, model);
    }

    /**
     * Nimmt die übergebene Template-Datei, sucht darin nach dem übergebenen Template-Namen und wendet das Template auf
     * das übergebene Model an.
     * 
     * @param templateFile
     *            Template-Datei, aus der ein Template ausgeführt werden soll.
     * @param templateName
     *            Name des Templates, dass der Generator ausführen soll.
     * @param model
     *            Modell, dass durch das Template bearbeitet werden soll.
     * @throws IOException
     *             Falls bei der Ausgabe ein Problem auftritt.
     */
    public void generate(final TemplateFile templateFile, final String templateName, final Object model)
            throws IOException {
        final TemplateStatement templateStatement = templateFile.findTemplate(templateName, model.getClass());
        if (templateStatement == null) {
            throw new RuntimeException(
                    "No template definition '" + templateName + "' found for type " + model.getClass().getName() + ".");
        }
        final Environment environment = new Environment();
        environment.execute(templateStatement, this, model);
    }

    /**
     * Gibt den aktuell gültigen Ausgabekanal für die Generierung von Artefakten zurück oder null, wenn kein Kanal
     * definiert wurde.
     * 
     * @return Aktuell gültiger Ausgabekanal für das Template oder null, falls noch kein Kanal geöffnet wurde.
     */
    public GeneratorWriter getCurrentWriter() {
        return currentWriter;
    }

    /**
     * Definiert den für das aktuelle Template zu verwendenden Ausgabekanal. In der Regel wird der Ausgabekanal durch
     * die OUTPUT-Definitionen in der Template-Datei selbst gesteuert.
     * 
     * @param currentWriter
     *            Ausgabekanal für das aktuelle Template.
     */
    public void setCurrentWriter(final GeneratorWriter currentWriter) {
        this.currentWriter = currentWriter;
    }

    /**
     * Fragt den GeneratorOutput nach dem Ausgabekanal mit dem übergebenen Namen. Dieser wird, falls er noch nicht
     * existier oder bereits wieder geschlossen wurde, neu erzeugt und dann zurückgegeben.
     * 
     * @param channelName
     *            Name des zu suchenden oder zu erzeugenden Ausgabekanals.
     * @return Writer für den benannten Azsgabekanal.
     * @throws IOException
     *             Falls bei der Erzeugung des Writers eine Exception aufgetreten ist.
     */
    public GeneratorWriter getWriter(final String channelName) throws IOException {
        return output.getWriter(channelName, outputPreferences);
    }
}