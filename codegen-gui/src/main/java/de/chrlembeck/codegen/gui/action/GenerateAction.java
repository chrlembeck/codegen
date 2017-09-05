package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.Generator;
import de.chrlembeck.codegen.generator.GeneratorException;
import de.chrlembeck.codegen.generator.ParserException;
import de.chrlembeck.codegen.generator.SimpleTemplateResolver;
import de.chrlembeck.codegen.generator.lang.TemplateFile;
import de.chrlembeck.codegen.generator.lang.TemplateStatement;
import de.chrlembeck.codegen.generator.output.BasicOutputPreferences;
import de.chrlembeck.codegen.generator.output.FileOutput;
import de.chrlembeck.codegen.generator.output.OverwritePreferences;
import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.TabComponent;
import de.chrlembeck.codegen.gui.TemplatePanel;
import de.chrlembeck.codegen.gui.UserSettings;
import de.chrlembeck.codegen.gui.dialog.GenerateDialog;
import de.chrlembeck.codegen.gui.dialog.GenerateDialog.OutputSelection;

/**
 * Action zum Starten des Code-Generators.
 *
 * @author Christoph Lembeck
 */
public class GenerateAction extends AbstractAction {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 8779361839732256455L;

    /**
     * Der Logger für diese Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateAction.class);

    /**
     * Referenz auf die Anwendung, in der der Generator gestartet werden soll.
     */
    private CodeGenGui codeGenGui;

    /**
     * Erstellt eine neue Action mit den übergebenen Daten.
     * 
     * @param codeGenGui
     *            Referenz auf die Anwendung, in der der Generator gestartet werden soll.
     */
    public GenerateAction(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
        putValue(NAME, "Template ausführen");
        putValue(SMALL_ICON, IconFactory.GENERATE_32.icon());
        putValue(SHORT_DESCRIPTION, "Startet den Generator");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        if (getModel() == null) {
            JOptionPane.showMessageDialog(codeGenGui, "Es wurde noch kein Modell geladen.", "Modell fehlt",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        final TemplatePanel templatePanel = getSelectedTemplatePanel();
        if (templatePanel == null) {
            codeGenGui.showErrorMessage("Es ist keine Template-Datei ausgewählt.", "Keine Template-Datei");
            return;
        }
        templatePanel.validateTemplate();
        if (templatePanel.hasErrors()) {
            codeGenGui.showErrorMessage("Bitte erst die Fehler beheben.", "Fehler in Templates");
            return;
        }
        if (templatePanel.isNewArtifact()) {
            codeGenGui.showErrorMessage(
                    "Vor dem Generieren muss die Template-Datei mindestens einmal gespeichert worden sein.",
                    "Bitte erst speichern");
            return;
        }
        final TemplateFile templateFile = templatePanel.getTemplateFile();
        final List<TemplateStatement> templates = templateFile.getTemplateStatements();
        if (templates == null || templates.isEmpty()) {
            codeGenGui.showErrorMessage("Die Template-Date enthält keine Template-Definitionen.", "Keine Templates");
            return;
        }

        final GenerateDialog generateDialog = new GenerateDialog(codeGenGui, templates, TemplateStatement::getName);
        generateDialog.setVisible(true);
        if (generateDialog.getResult() == GenerateDialog.RESULT_OK) {
            final OutputSelection outputSelection = generateDialog.getOutputSelection();
            Generator generator;
            switch (outputSelection) {
                case FILE_OUTPUT:
                    generator = generateToFile(templateFile, generateDialog.getOutputDirectory(),
                            generateDialog.getOverwritePreferences());
                    new UserSettings().setLastOutputDirectory(generateDialog.getOutputDirectory());
                    break;
                case APPLICATION_OUTPUT:
                    generator = generateToGui(templateFile);
                    break;
                default:
                    throw new IllegalStateException();

            }
            final TemplateStatement selectedTemplate = generateDialog.getSelectedTemplate();
            generate(generator, templateFile, selectedTemplate.getName());
        }
    }

    /**
     * Führt die eigentliche Generierung der Artefakte durch.
     * 
     * @param generator
     *            Generator, der die Generierung vornehmen soll.
     * @param templateFile
     *            Template-Datei, aus der das Template gelesen werden soll.
     * @param templateName
     *            Name des Templates, welches angewandt werden soll.
     */
    private void generate(final Generator generator, final TemplateFile templateFile, final String templateName) {
        try {
            generator.generate(templateFile, templateName, getModel());
        } catch (final IOException e) {
            LOGGER.error("IO-Fehler bei der Generierung: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(codeGenGui, e.getMessage(), "IO-Fehler", JOptionPane.ERROR_MESSAGE);
        } catch (final GeneratorException ge) {
            // TODO: Call-Stack mit ausgeben
            JOptionPane.showMessageDialog(codeGenGui,
                    ge.getMessage() + "\n" + ge.getStartPosition().toShortString(),
                    "Generator-Fehler", JOptionPane.ERROR_MESSAGE);
            LOGGER.info(ge.getLocalizedMessage() + ": " + ge.printGeneratorStack(), ge);
        } catch (final ParserException pe) {
            JOptionPane.showMessageDialog(codeGenGui,
                    pe.getMessage() + "\n" + pe.getStartPosition().toShortString(),
                    "Parser-Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Konfiguriert den Generator für die Ausgabe der Artefakte in das Dateisystem.
     * 
     * @param templateFile
     *            TemplateFile, aus dem die Templates gelesen werden sollen.
     * @param outputDirectory
     *            Verzeichnis, in das die Ausgabe erfolgen soll.
     * @param overwritePreferences
     *            Verhalten des Generators beim Überschreiben existierender Dateien.
     * @return Konfiguriertwer Generator für die Ausgabe in das Dateisystem.
     */
    private Generator generateToFile(final TemplateFile templateFile,
            final Path outputDirectory, final OverwritePreferences overwritePreferences) {
        final FileOutput out = new FileOutput(outputDirectory);
        final SimpleTemplateResolver templateResolver = new SimpleTemplateResolver(templateFile);
        final BasicOutputPreferences outputPreferences = new BasicOutputPreferences();
        outputPreferences.setDefaultCharset(Charset.forName("UTF-8"));
        outputPreferences.setDefaultOverwritePreferences(overwritePreferences);
        final Generator generator = new Generator(templateResolver, out,
                outputPreferences);
        return generator;
    }

    /**
     * Konfiguriert einen Generator für die Ausgabe der Artefakte in die grafische Oberfläche.
     * 
     * @param templateFile
     *            Template-Datei, aus der die Templates gelesen werden sollen.
     * @return Konfiurierter Generator für die Ausgabe in die GUI.
     */
    private Generator generateToGui(final TemplateFile templateFile) {
        final GuiOutput out = new GuiOutput(codeGenGui);
        final URI templateResourceLocator = templateFile.getResourceIdentifier();
        LOGGER.debug("templateResourceLocator=" + templateResourceLocator);
        final SimpleTemplateResolver templateResolver = new SimpleTemplateResolver(templateFile);
        final Generator generator = new Generator(templateResolver, out,
                new BasicOutputPreferences());
        codeGenGui.removeAllOutputPanels();
        return generator;
    }

    /**
     * Gibt das aktuell ausgewählte Editor-Fenster zurück.
     * 
     * @return Aktuell ausgewähltes Editor-Fenster.
     */
    private TemplatePanel getSelectedTemplatePanel() {
        final TabComponent document = codeGenGui.getSelectedDocument();
        return document instanceof TemplatePanel ? (TemplatePanel) document : null;
    }

    /**
     * Gibt das zuletzte geladene, also gerade aktiv Modell zurück.
     * 
     * @return Aktuelles Modell für die Generierung.
     */
    private Object getModel() {
        return codeGenGui.getModel();
    }
}