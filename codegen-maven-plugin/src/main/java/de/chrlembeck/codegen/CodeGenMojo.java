package de.chrlembeck.codegen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import de.chrlembeck.codegen.generator.AbstractCodeGenException;
import de.chrlembeck.codegen.generator.Generator;
import de.chrlembeck.codegen.generator.SimpleTemplateResolver;
import de.chrlembeck.codegen.generator.TemplateResolver;
import de.chrlembeck.codegen.generator.model.ModelFactoryHelper;
import de.chrlembeck.codegen.generator.output.BasicOutputPreferences;
import de.chrlembeck.codegen.generator.output.CombinedGeneratorOutput;
import de.chrlembeck.codegen.generator.output.FileOutput;
import de.chrlembeck.codegen.generator.output.GeneratorOutput;
import de.chrlembeck.codegen.generator.output.HTMLDebugGeneratorWriter;
import de.chrlembeck.codegen.generator.output.OverwritePreferences;

/**
 * Führt die angegebenen Templates aus und erzeugt daraus den entsprechenden Output.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
@Execute()
public class CodeGenMojo extends AbstractMojo {

    /**
     * Liste von Templates, die im Laufe des Builds ausgeführt werden sollen.
     */
    @Parameter(property = "generate.templates")
    private List<Template> templates;

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    /**
     * Führt days Plugin aus und erzeugt die in der Konfiguration hinterlegten Artefakte.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Log log = getLog();
        log.info("base dir: " + project.getBasedir().getAbsolutePath());
        log.info("target dir: " + project.getBuild().getDirectory());
        log.info("source directory: " + project.getBuild().getSourceDirectory());
        log.info("test source directory: " + project.getBuild().getTestSourceDirectory());
        log.info("templates: " + templates);
        log.info("project: " + project);
        log.info("compile source roots: " + project.getCompileSourceRoots());
        log.info("file: " + project.getFile());

        if (templates == null || templates.isEmpty()) {
            log.warn("Keine Template-Definitionen in der " + project.getFile().getName()
                    + " gefunden. Es wird nichts generiert. (" + project.getFile().getAbsolutePath() + ")");
            return;
        }

        for (int templateIndex = 0; templateIndex < templates.size(); templateIndex++) {
            log.info("Verarbeite Template " + (templateIndex + 1) + " von " + templates.size());
            final Template template = templates.get(templateIndex);
            final String outputPath = template.getOutputPath(project);
            final File outputBaseDir = new File(project.getBasedir(), outputPath);
            log.info("outputPath=" + outputPath + ". Ausgabe erfolgt in " + outputBaseDir.getAbsolutePath());
            GeneratorOutput generatorOutput = FileOutput.simpleTextOutput(outputBaseDir.toPath());
            if (template.isGenerateDebugHtml()) {
                final String debugOutputPath = template.getDebugOutputPath();
                final File debugOutputBaseDir = new File(project.getBasedir(), debugOutputPath);
                log.info("debugOutputPath=" + debugOutputPath + ". Debug-Ausgabe erfolgt in "
                        + debugOutputBaseDir.getAbsolutePath());
                final FileOutput<HTMLDebugGeneratorWriter> debugOutput = new FileOutput<>(
                        debugOutputBaseDir.toPath(),
                        (writer, channelName, path) -> new HTMLDebugGeneratorWriter(writer, channelName,
                                path.toFile(), null));
                debugOutput.setSuffix(".html");
                generatorOutput = new CombinedGeneratorOutput(generatorOutput, debugOutput);
            }

            addSourceRoot(outputBaseDir, template.getArtifactScope());

            final File templateFile = template.getTemplateFile();
            log.info("templateFile=" + templateFile.getAbsolutePath());
            final URI rootResourceIdentifier = templateFile.toURI();
            log.info("rootResourceIdentifier=" + rootResourceIdentifier);
            final TemplateResolver resolver = new SimpleTemplateResolver(rootResourceIdentifier);

            final Object model = ladeModel(template);
            final BasicOutputPreferences preferences = new BasicOutputPreferences();
            log.info("outputEncoding=" + template.getOutputEncoding());
            preferences.setDefaultCharset(Charset.forName(template.getOutputEncoding()));
            final OverwritePreferences overwritePreferences = template.getOverwritePreferences();
            log.info("overwritePreferences=" + overwritePreferences);
            preferences.setDefaultOverwritePreferences(overwritePreferences);
            final Generator generator = new Generator(resolver, generatorOutput, preferences);
            try {
                generator.generate(rootResourceIdentifier, template.getTemplateName(), model);
            } catch (IOException | AbstractCodeGenException e) {
                fail("Bei der Codegenerierung ist ein Fehler aufgetreten.", e);
            }
        }
    }

    /**
     * Lädt das zum Generieren benötigte Modell.
     * 
     * @param template
     *            Template-Konfiguration, zu der das passende Modell zur Generierung geladen werden soll.
     * @return Das für die Generierung zu verwendende Modell.
     * @throws MojoFailureException
     *             Bei allen Fehlern, die beim Laden des Modells auftreten können.
     */
    private Object ladeModel(final Template template) throws MojoFailureException {
        getLog().debug("Lade Modell");
        final String modelCreatorClass = template.getModelCreatorClass();
        if (modelCreatorClass == null || modelCreatorClass.trim().isEmpty()) {
            throw new MojoFailureException("Das Feld modelCreatorClass ist nicht gefüllt.");
        }
        final String modelCreatorMethod = template.getModelCreatorMethod();
        if (modelCreatorMethod == null || modelCreatorMethod.trim().isEmpty()) {
            throw new MojoFailureException("Das Feld modelCreatorMethod ist nicht gefüllt.");
        }
        final Object modell;
        try {
            modell = ModelFactoryHelper.byMethodCall(modelCreatorClass, modelCreatorMethod);
            getLog().debug("Modell geladen: " + modell);
            return modell;
        } catch (final ClassNotFoundException e) {
            final String message = "Die Klasse zur Erzeugung des Modells konnte nicht geladen werden. ("
                    + modelCreatorClass + ")";
            fail(message, e);
        } catch (final NoSuchMethodException e) {
            final String message = "Die Methode '" + modelCreatorMethod
                    + "()' zur Erzeugung des Modells konnte in der Klasse '"
                    + modelCreatorClass + "' nicht gefunden werden.";
            fail(message, e);
        } catch (final SecurityException e) {
            final String message = "Der SecurityManager verweigert den Zugriff auf die Methode '" + modelCreatorClass
                    + "."
                    + modelCreatorMethod + "()' zur Erzeugung des Modells.";
            fail(message, e);
        } catch (final IllegalAccessException e) {
            final String message = "Auf die Methode '" + modelCreatorMethod
                    + "' zur Erstellung des Modells kann nicht zugegriffen werden.";
            fail(message, e);
        } catch (final IllegalArgumentException e) {
            final String message = "Die Methode '" + modelCreatorMethod + "' erwartet andere Parameter.";
            fail(message, e);
        } catch (final InvocationTargetException e) {
            final String message = "Fehler bei der Ausführung der Methode zur Erstellung des Modells ("
                    + modelCreatorMethod + ").";
            fail(message, e);
        } catch (final InstantiationException e) {
            final String message = "Die ModellCreatorClass '" + modelCreatorClass
                    + "' kann nicht instantiiert werden. Besitzt sie einen public Konstruktor ohne Parameter?";
            fail(message, e);
        }
        return null;
    }

    /**
     * Bricht die Ausführung mit einem Fehler ab und protokolliert diesen.
     * 
     * @param message
     *            Fehlermeldung, die protokolliert und ausgegeben werden soll.
     * @param cause
     *            Ursprüngliche Exception, die zu dem Abbruch geführt hat.
     * @throws MojoFailureException
     *             Exception, die im Build-Prozess angezeigt wird.
     */
    private void fail(final String message, final Throwable cause) throws MojoFailureException {
        getLog().error(message, cause);
        throw new MojoFailureException(message, cause);
    }

    /**
     * Fügt das angegebene Verzeichnis zu einem Compile-Root des Build-Prozesses hinzu.
     * 
     * @param outputDir
     *            Verzeichnis, in das die Artefakte geschrieben werden.
     * @param artifactScope
     *            Scope, zu dem das Verzeichnis hinzugefügt werden soll.
     * @see ArtifactScope
     */
    private void addSourceRoot(final File outputDir, final ArtifactScope artifactScope) {
        final String path = outputDir.getPath();
        switch (artifactScope) {
            case COMPILE:
                getLog().info("Füge '" + path + "' zum den Runtime-Sourcen hinzu.");
                project.addCompileSourceRoot(path);
                break;
            case TEST:
                getLog().info("Füge '" + path + "' zum den Test-Sourcen hinzu.");
                project.addTestCompileSourceRoot(path);
                break;
            case SCRIPT:
                getLog().info("Füge '" + path + "' zum den Script-Sourcen hinzu.");
                project.addScriptSourceRoot(path);
                break;
            default:
                throw new IllegalStateException("unknown scope: " + artifactScope);
        }
    }
}