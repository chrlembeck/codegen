package de.chrlembeck.codegen.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import de.chrlembeck.codegen.generator.lang.TemplateStatement;
import de.chrlembeck.codegen.generator.output.OverwritePreferences;
import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.UserSettings;
import de.chrlembeck.codegen.gui.util.DirectoryTextField;
import de.chrlembeck.util.objects.ToStringWrapper;

public class GenerateDialog extends AbstractDialog {

    public enum OutputSelection {
        FILE_OUTPUT, APPLICATION_OUTPUT;
    }

    private static final long serialVersionUID = 3262269958341495414L;

    private JLabel lbOverwrite;

    private JLabel lbDirectory;

    private JLabel lbDebugDirectory;

    private DirectoryTextField tfDirectory;

    private DirectoryTextField tfDebugDirectory;

    private JCheckBox cbDebug;

    private JRadioButton rbFileOutput;

    private JRadioButton rbGuiOutput;

    private JComboBox<ToStringWrapper<TemplateStatement>> cbTemplate;

    private JRadioButton rbExistingException;

    private JRadioButton rbExistingKeep;

    private JRadioButton rbExistingReplace;

    public GenerateDialog(final CodeGenGui codeGenGui, final List<TemplateStatement> templates,
            final Function<TemplateStatement, String> toStringFunction) {
        super(codeGenGui, "Generieren");
        addOKButton("Generieren", KeyEvent.VK_G, IconFactory.OK_22.icon());
        addCancelButton();
        for (final TemplateStatement statement : templates) {
            cbTemplate.addItem(new ToStringWrapper<TemplateStatement>(statement, toStringFunction));
        }
    }

    @Override
    protected JPanel createMainPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final JLabel lbTemplate = new JLabel("Template");
        cbTemplate = new JComboBox<>();
        cbTemplate.setPreferredSize(new Dimension(200, cbTemplate.getPreferredSize().height));
        cbTemplate.setMinimumSize(cbTemplate.getPreferredSize());
        final JLabel lbOutputMethod = new JLabel("Ausgabeart");
        rbFileOutput = new JRadioButton("Dateisystem", false);
        rbGuiOutput = new JRadioButton("Ausgabebereich", true);
        final ButtonGroup bgOutputMethod = new ButtonGroup();
        bgOutputMethod.add(rbFileOutput);
        bgOutputMethod.add(rbGuiOutput);
        lbOverwrite = new JLabel("Existierende Dateien");
        lbDirectory = new JLabel("Ausgabeverzeichnis");
        lbDebugDirectory = new JLabel("Debug-Verzeichnis");
        tfDirectory = new DirectoryTextField();
        tfDebugDirectory = new DirectoryTextField();
        cbDebug = new JCheckBox("Debug-Ausgabe", false);
        rbExistingException = new JRadioButton("Fehlermeldung erzeugen");
        rbExistingKeep = new JRadioButton("Behalten");
        rbExistingReplace = new JRadioButton("Überschreiben");
        final ButtonGroup bgExisting = new ButtonGroup();
        bgExisting.add(rbExistingException);
        bgExisting.add(rbExistingKeep);
        bgExisting.add(rbExistingReplace);
        rbExistingException.setSelected(true);

        lbDirectory.setLabelFor(tfDirectory);
        lbTemplate.setLabelFor(cbTemplate);

        panel.add(lbTemplate, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(cbTemplate, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(lbOutputMethod, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(rbGuiOutput, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(rbFileOutput, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        panel.add(cbDebug, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        panel.add(lbDirectory, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(tfDirectory, new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        panel.add(lbDebugDirectory, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(tfDebugDirectory, new GridBagConstraints(1, 5, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        panel.add(lbOverwrite, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(rbExistingException, new GridBagConstraints(1, 6, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(rbExistingReplace, new GridBagConstraints(1, 7, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        panel.add(rbExistingKeep, new GridBagConstraints(1, 8, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        rbFileOutput.addItemListener(this::fileOutputSelectionChanged);
        cbDebug.addItemListener(this::debugSelectionChanged);
        setFileOutputPreferencesEnabled(rbFileOutput.isSelected());
        rbFileOutput.setMnemonic(KeyEvent.VK_D);
        rbGuiOutput.setMnemonic(KeyEvent.VK_A);
        rbExistingException.setMnemonic(KeyEvent.VK_F);
        rbExistingKeep.setMnemonic(KeyEvent.VK_B);
        rbExistingReplace.setMnemonic(KeyEvent.VK_S);
        lbDirectory.setDisplayedMnemonic(KeyEvent.VK_V);
        lbDebugDirectory.setDisplayedMnemonic(KeyEvent.VK_G);
        tfDirectory.setMnemonic(KeyEvent.VK_V);
        tfDebugDirectory.setMnemonic(KeyEvent.VK_G);
        lbTemplate.setDisplayedMnemonic(KeyEvent.VK_T);

        tfDirectory.setDirectory(new UserSettings().getLastOutputDirectory());
        tfDebugDirectory.setDirectory(new UserSettings().getLastDebugOutputDirectory());
        return panel;
    }

    public void fileOutputSelectionChanged(final ItemEvent itemEvent) {
        final boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;
        setFileOutputPreferencesEnabled(selected);
    }

    public void debugSelectionChanged(final ItemEvent itemEvent) {
        final boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;
        tfDebugDirectory.setEnabled(selected && rbFileOutput.isSelected());
    }

    private void setFileOutputPreferencesEnabled(final boolean enabled) {
        lbDirectory.setEnabled(enabled);
        lbOverwrite.setEnabled(enabled);
        rbExistingException.setEnabled(enabled);
        rbExistingKeep.setEnabled(enabled);
        rbExistingReplace.setEnabled(enabled);
        tfDirectory.setEnabled(enabled);
        tfDebugDirectory.setEnabled(enabled && cbDebug.isSelected());
    }

    public static void main(final String[] args) {
        CodeGenGui.setSystemLookAndFeel();
        SwingUtilities
                .invokeLater(() -> new GenerateDialog(null, Collections.emptyList(), item -> "").setVisible(true));
    }

    public Path getOutputDirectory() {
        return tfDirectory.getSelectedDirectory();
    }

    public OutputSelection getOutputSelection() {
        if (rbFileOutput.isSelected()) {
            return OutputSelection.FILE_OUTPUT;
        }
        if (rbGuiOutput.isSelected()) {
            return OutputSelection.APPLICATION_OUTPUT;
        }
        throw new IllegalStateException("Illegal output selection.");
    }

    public TemplateStatement getSelectedTemplate() {
        @SuppressWarnings("unchecked")
        final ToStringWrapper<TemplateStatement> selectedItem = (ToStringWrapper<TemplateStatement>) cbTemplate
                .getSelectedItem();
        return selectedItem == null ? null : selectedItem.getObject();
    }

    public OverwritePreferences getOverwritePreferences() {
        if (rbExistingException.isSelected()) {
            return OverwritePreferences.THROW_EXCEPTION;
        }
        if (rbExistingReplace.isSelected()) {
            return OverwritePreferences.OVERWRITE;
        }
        if (rbExistingKeep.isSelected()) {
            return OverwritePreferences.KEEP_EXISTING;
        }
        throw new IllegalStateException();
    }

    public boolean debugEnabled() {
        return cbDebug.isSelected();
    }

    public Path getDebugOutputDirectory() {
        return tfDebugDirectory.getSelectedDirectory();
    }
}