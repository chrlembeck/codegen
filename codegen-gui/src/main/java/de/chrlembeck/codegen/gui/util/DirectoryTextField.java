package de.chrlembeck.codegen.gui.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.chrlembeck.codegen.gui.IconFactory;

public class DirectoryTextField extends JPanel {

    private JTextField tfDirectory;

    private JButton btChoose;

    private Path selectedDirectory;

    public DirectoryTextField() {
        setLayout(new GridBagLayout());
        btChoose = new JButton(IconFactory.FOLDER_16.icon());
        btChoose.setIconTextGap(0);
        final Insets oldMargin = btChoose.getMargin();
        btChoose.setMargin(new Insets(oldMargin.top, oldMargin.top, oldMargin.bottom, oldMargin.bottom)); // quadratic
        btChoose.setOpaque(false);
        btChoose.setContentAreaFilled(false);
        tfDirectory = new JTextField(30);
        tfDirectory.setEditable(false);
        add(tfDirectory, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(btChoose, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 0));
        tfDirectory.setPreferredSize(
                new Dimension(tfDirectory.getPreferredSize().width, btChoose.getPreferredSize().height));
        btChoose.addActionListener(this::btChooseClicked);
    }

    public void btChooseClicked(final ActionEvent actionEvent) {
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setCurrentDirectory(selectedDirectory == null ? null : selectedDirectory.toFile());
        final int exitState = chooser.showDialog(this, "Verzeichnis wählen");
        if (exitState == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = chooser.getSelectedFile().toPath().toAbsolutePath();
            tfDirectory.setText(selectedDirectory.toString());
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        tfDirectory.setEnabled(enabled);
        btChoose.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public void setMnemonic(final int mnemonic) {
        btChoose.setMnemonic(mnemonic);
    }

    public Path getSelectedDirectory() {
        return selectedDirectory;
    }

    public void setDirectory(final Path lastOutputDirectory) {
        selectedDirectory = lastOutputDirectory;
        tfDirectory.setText(lastOutputDirectory.toString());
    }
}