package de.chrlembeck.codegen.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.function.Function;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.UserSettings;
import de.chrlembeck.util.objects.ToStringWrapper;
import de.chrlembeck.util.swing.action.DefaultAction;

public class ConfigureClasspathDialog extends AbstractDialog {

    private static final long serialVersionUID = -562850804514146804L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureClasspathDialog.class);

    private JList<ToStringWrapper<URL>> liPaths;

    private CodeGenGui codeGenGui;

    private DefaultListModel<ToStringWrapper<URL>> classpathModel;

    final static Function<URL, String> TO_STRING_FUNCTION = URL::toString;

    public ConfigureClasspathDialog(final CodeGenGui codeGenGui) {
        super(codeGenGui, "Classpath-Konfiguration");
        this.codeGenGui = codeGenGui;
        addOKButton("OK", KeyEvent.VK_O, IconFactory.OK_22.icon());
        addCancelButton();

        Arrays.stream(codeGenGui.getModelClasspath()).forEach(this::addClasspathElement);
    }

    @Override
    protected JPanel createMainPanel() {
        final JPanel panel = new JPanel();
        classpathModel = new DefaultListModel<>();
        liPaths = new JList<>(classpathModel);
        liPaths.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        panel.setLayout(new GridBagLayout());
        final JScrollPane spList = new JScrollPane(liPaths);

        final JButton btAddJar = new JButton(new DefaultAction("JAR hinzufügen", "JAR hinzufügen",
                "Fügt ein JAR-Archiv zum Classpath hinzu", KeyEvent.VK_J, null, null, null, null, this::addJar));
        final JButton btAddDir = new JButton(new DefaultAction("Verzeichnis hinzufügen", "Verzeichnis hinzufügen",
                "Fügt ein Klassen-Verzeichniszum Classpath hinzu", KeyEvent.VK_V, null, null, null, null,
                this::addDirectory));
        final JButton btRemove = new JButton(
                new DefaultAction("Entfernen", "Entfernen", "Löscht den oder die ausgewählten Einträge vom Classpath",
                        KeyEvent.VK_E, null, null, null, null, this::removeEntry));
        final JPanel pnButtons = new JPanel();
        panel.add(spList, new GridBagConstraints(0, 0, 1, GridBagConstraints.REMAINDER, 1, 1,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(pnButtons, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        pnButtons.setLayout(new GridBagLayout());
        pnButtons.add(btAddJar, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH,
                GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        pnButtons.add(btAddDir, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.NORTH,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
        pnButtons.add(btRemove, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.NORTH,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

        return panel;
    }

    void addJar(final ActionEvent event) {
        final FileNameExtensionFilter filter = new FileNameExtensionFilter("JAR-Archive", "jar", "zip");
        final JFileChooser chooser = CodeGenGui.createFileChooser(new UserSettings().getLastClasspathDirectory(),
                filter, JFileChooser.FILES_ONLY);
        handleFileChooser(chooser);
    }

    void addDirectory(final ActionEvent event) {
        final JFileChooser chooser = CodeGenGui.createFileChooser(new UserSettings().getLastClasspathDirectory(),
                null, JFileChooser.DIRECTORIES_ONLY);
        handleFileChooser(chooser);
    }

    private void handleFileChooser(final JFileChooser chooser) {
        final int selection = chooser.showOpenDialog(codeGenGui);

        if (selection == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();
            new UserSettings().setLastClasspathDirectory(file.toPath());
            try {
                addClasspathElement(file.toURI().toURL());
            } catch (final MalformedURLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    void removeEntry(final ActionEvent event) {
        final int[] selection = liPaths.getSelectedIndices();
        if (selection != null) {
            for (int i = selection.length - 1; i >= 0; i--) {
                classpathModel.remove(selection[i]);
            }
        }
    }

    private void addClasspathElement(final URL pathElement) {
        classpathModel.addElement(new ToStringWrapper<URL>(pathElement, TO_STRING_FUNCTION));
    }

    public URL[] getClasspathElements() {
        final URL[] classpath = new URL[classpathModel.size()];
        for (int i = 0; i < classpath.length; i++) {
            classpath[i] = classpathModel.get(i).getObject();
        }
        return classpath;
    }
}