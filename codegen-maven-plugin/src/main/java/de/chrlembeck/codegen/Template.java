package de.chrlembeck.codegen;

import java.io.File;

import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

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
     * Legt fest, dass die erzeugten Dateien in als Sourcen für die Tests verwendet werden sollen. true = Sourcen weren
     * für die Tests benötigt, false = Sourcen werden zur Laufzeit benötigt.
     */
    @Parameter(property = "generate.generateTestSources", defaultValue = "false")
    private boolean generateTestSources = false;

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
                + ", outputPath=" + outputPath + ", generateTestSources="
                + generateTestSources + "]";
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
     * Gibt an, ob die generierten Dateien zum test- oder compile-Scope hinzugefügt werden sollen.
     * 
     * @return Zugehörigkeit der generierten Dateien zum test- oder compile-Scope. (true = test, false = compile)
     */
    public boolean isGenerateTestSources() {
        return generateTestSources;
    }
}