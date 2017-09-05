package de.chrlembeck.codegen;

import java.io.File;

import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import de.chrlembeck.codegen.generator.output.OverwritePreferences;

/**
 * Repräsentiert die Einstellungen eines Generator-Laufs für das CodeGen-Maven-Plugin. Pro Template-Aufruf wird in der
 * <code>configuration</code>-Sektion des Aufrufs des Maven-Plugins in einer <code>pom.xml</code> eine
 * Template-Beschreibung benötigt.
 * 
 * @author Christoph Lembeck
 */
public class Template {

    /**
     * Referenz auf die Datei, in der das auszuführende Template gefunden werden kann.
     */
    @Parameter(required = true)
    private File templateFile;

    /**
     * Name des im Generatorlauf auszuführenden Templates.
     */
    @Parameter(required = true, defaultValue = "root")
    private String templateName = "root";

    /**
     * Name der Klasse, aus der das Modell erstellt werden kann. Die Klasse muss zur Laufzeit des Plugins für das Plugin
     * zur verfügung stehen. Dies kann entweder dadurch gewährleistet werden, dass das Plugin in einer Phase nach dem
     * compilieren aufgerufen wird, oder die Klasse liegt in einem eigenen Modell-Projekt, welches in der
     * Plugin-Definition als dependency hinterlegt ist.
     */
    @Parameter(required = true)
    private String modelCreatorClass;

    /**
     * Methode in der modelCreatorClass, die das Modell erzeugt.
     * 
     * @see #modelCreatorClass
     */
    @Parameter(required = true)
    private String modelCreatorMethod;

    /**
     * Pfad, in dem die generierten Artefakte abgelegt werden sollen. (Z.B.: '/target/generated-sources/codegen')
     */
    @Parameter(required = true, defaultValue = "/target/generated-sources/codegen")
    private String outputPath;

    /**
     * Encoding für die zu erstellenden Ausgabe-Dateien. Default = 'UTF-8'
     */
    @Parameter(required = true, defaultValue = "UTF-8")
    private String outputEncoding = "UTF-8";

    /**
     * Legt fest, zu welchem Scope die erzeugten Dateien im Build-Prozess hinzugefügt werden sollen. Mögliche Optionen
     * sind COMPILE, TEST und SCRIPT.
     */
    @Parameter(property = "generate.artifactScope", defaultValue = "false")
    private ArtifactScope artifactScope = ArtifactScope.COMPILE;

    /**
     * Legt das Verhalten des Generators beim Antreffen einer zu überschreibenden Datei fest. Mögliche Optionen sind
     * KEEP_EXISTING, OVERWRITE und THROW_EXCEPTION.
     */
    @Parameter(property = "generate.overwritePreferences", defaultValue = "THROW_EXCEPTION")
    private OverwritePreferences overwritePreferences = OverwritePreferences.THROW_EXCEPTION;

    /**
     * Gitb die Klasse zurück, mit deren Hilfe das Modell erzeugt werden kann.
     * 
     * @return Klasse, in der die Methode enthalten ist, mit der das Modell erzeugt werden kann.
     * @see #modelCreatorClass
     */
    public String getModelCreatorClass() {
        return modelCreatorClass;
    }

    /**
     * Gibt die Methode zurück, die aufgerufen werden muss, um das Modell zu erzeugen.
     * 
     * @return Methode aus der modelCreatorClass, die das Modell erzeugt.
     * @see #modelCreatorClass
     */
    public String getModelCreatorMethod() {
        return modelCreatorMethod;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getName() + "[templateFile=" + templateFile + ", templateName=" + templateName
                + ", modelCreatorClass=" + modelCreatorClass + ", modelCreatorMethod=" + modelCreatorMethod
                + ", outputPath=" + outputPath + ", artifactScope="
                + artifactScope + "]";
    }

    /**
     * Gibt den Pfad zurück, in dem die generierten Artefakte gespeichert werden.
     * 
     * @param project
     *            Maven-Projekt, in dem das Plugin aktuell ausgeführt wird.
     * @return Pfad zur Ausgabe der generierten Artefakte.
     * @see #outputPath
     */
    public String getOutputPath(final MavenProject project) {
        if (outputPath == null) {
            outputPath = "/target/generated-sources/codegen";
        }
        return outputPath;
    }

    /**
     * Gibt das Encoding zurück, welches für die Ausgabe verwendet werden soll. (default='UTF-8')
     * 
     * @return Encoding für die Ausgabe der Generate.
     * @see #outputEncoding
     */
    public String getOutputEncoding() {
        return outputEncoding;
    }

    /**
     * Gibt die Referenz auf die Datei zurück, in der das auszuführende Template gespeichert ist.
     * 
     * @return Referenz auf die Templata-Datei.
     * @see #templateFile
     */
    public File getTemplateFile() {
        return templateFile;
    }

    /**
     * Gibt den Namen des auszuführenden Templates aus. (Z.B: 'root')
     * 
     * @return Name des auszuführenden Templates.
     * @see #templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Gibt an, zu welchem Scope im Build-Prozess die generierten Artefakte hinzugefügt werden sollen.
     * 
     * @return Zugehörigkeit der generierten Dateien zum gewünschten Scope.
     */
    public ArtifactScope getArtifactScope() {
        return artifactScope;
    }

    /**
     * Gibt zurück, wie der Generator sich verhalten soll, wenn er beim Generieren einer Datei eine vorhandene Datei
     * überschreiben soll.
     * 
     * @return Gewünschtes Verhalten des Generators beim Überschreiben.
     * @see OverwritePreferences
     */
    public OverwritePreferences getOverwritePreferences() {
        return overwritePreferences;
    }
}