package de.chrlembeck.codegen.gui.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
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
import de.chrlembeck.codegen.generator.output.BufferedOutput;
import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.TabComponent;
import de.chrlembeck.codegen.gui.TemplatePanel;
import de.chrlembeck.codegen.gui.util.ToStringWrapper;

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
    private static Logger LOGGER = LoggerFactory.getLogger(GenerateAction.class);

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
            JOptionPane.showMessageDialog(codeGenGui, "Es ist kein Template ausgewählt.", "Kein Template",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        final Path templatePath = templatePanel.getPath();
        final URI templateResourceLocator = templatePath.toUri();
        final SimpleTemplateResolver templateResolver = new SimpleTemplateResolver(templateResourceLocator);

        templatePanel.validateTemplate();
        if (templatePanel.hasErrors()) {
            JOptionPane.showMessageDialog(codeGenGui, "Bitte erst die Fehler beheben.");
            return;
        }

        final TemplateFile templateFile = templatePanel.getTemplateFile();

        final List<TemplateStatement> templates = templateFile.getTemplateStatements();
        @SuppressWarnings("unchecked")
        final ToStringWrapper<TemplateStatement>[] templateWrappers = templates.stream()
                .map(t -> new ToStringWrapper<>(t, TemplateStatement::getName)).toArray(ToStringWrapper[]::new);
        @SuppressWarnings("unchecked")
        final ToStringWrapper<TemplateStatement> selection = (ToStringWrapper<TemplateStatement>) JOptionPane
                .showInputDialog(codeGenGui,
                        "Bitte ein Template auswälen.", "Template wählen", JOptionPane.QUESTION_MESSAGE, null,
                        templateWrappers,
                        templateWrappers[0]);
        if (selection != null) {
            final BufferedOutput out = new BufferedOutput();
            final Generator generator = new Generator(templateResolver, out,
                    new BasicOutputPreferences());
            try {
                generator.generate(templateResourceLocator, selection.getObject().getName(), getModel());
                for (final String channel : out.getChannelNames()) {
                    System.out.println("*** " + channel + "***");
                    System.out.println(out.getContent(channel));
                    System.out.println("*** end of " + channel + "***");
                }
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