package de.chrlembeck.codegen.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.model.ModelFactoryHelper;
import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.util.swing.action.DefaultAction;

public class CreateModelDialog extends AbstractDialog {

    private static final long serialVersionUID = 2515965069925565716L;

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateModelDialog.class);

    private CodeGenGui codeGenGui;

    private JTextField tfClassName;

    private JTextField tfMethodName;

    public CreateModelDialog(final CodeGenGui codeGenGui) {
        super(codeGenGui, "Modellerzeugung", ModalityType.APPLICATION_MODAL);
        this.codeGenGui = codeGenGui;
        addButton(new DefaultAction("OK", "OK", "Erzeugt das Modell durch Aufruf der ausgewählten Methode",
                KeyEvent.VK_O, null, null, IconFactory.OK_22.icon(), null, this::okPressed));
        addCancelButton();
    }

    @Override
    protected JPanel createMainPanel() {
        final JPanel panel = new JPanel();
        tfClassName = new JTextField(30);
        tfMethodName = new JTextField(30);
        final JLabel lbClassName = new JLabel("Klassenname");
        final JLabel lbMethodName = new JLabel("Methode");
        lbClassName.setLabelFor(tfClassName);
        lbMethodName.setLabelFor(tfMethodName);
        lbClassName.setDisplayedMnemonic(KeyEvent.VK_K);
        lbMethodName.setDisplayedMnemonic(KeyEvent.VK_M);

        panel.setLayout(new GridBagLayout());
        panel.add(lbClassName, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(tfClassName, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(lbMethodName, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(tfMethodName, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        return panel;
    }

    void okPressed(final ActionEvent actionEvent) {
        final String className = tfClassName.getText().trim();
        final String methodName = tfMethodName.getText().trim();
        Object modell;
        try {
            modell = ModelFactoryHelper.byMethodCall(className, methodName,
                    codeGenGui.getModelClassLoader());
            codeGenGui.setModel(className + "." + methodName, modell);
            setResult(RESULT_OK);
            dispose();
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                | SecurityException | InstantiationException e1) {
            LOGGER.info("IOException beim Laden eines Modells: " + e1.getMessage(), e1);
            JOptionPane.showMessageDialog(codeGenGui, "Fehler beim Laden des Modells: " + e1.getMessage(), "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}