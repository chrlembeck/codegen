package de.chrlembeck.codegen.gui.dialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.util.objects.ToStringWrapper;
import de.chrlembeck.util.swing.action.DefaultAction;
import de.chrlembeck.util.swing.icon.RectangleIcon;

public class SettingsDialog extends AbstractDialog {

    private static final long serialVersionUID = 686994149235060167L;

    public SettingsDialog(final CodeGenGui codeGenGui) {
        super(codeGenGui, "Einstellungen");

        addButton(new DefaultAction("OK", "OK", "Übernimmt die Einstellungen und schließt den Dialog", KeyEvent.VK_O,
                null, null, IconFactory.OK_22.icon(), null, this::okButtonPressed));
        addCancelButton();
    }

    @Override
    protected JComponent createMainPanel() {
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(true);
        tabbedPane.addTab("Editorfarben", createEditorColorsTab());
        tabbedPane.setBackgroundAt(0, Color.RED);
        return tabbedPane;
    }

    private JPanel createEditorColorsTab() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final JPanel pnColors = new JPanel();
        final JPanel pnExample = new JPanel();
        pnColors.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Farben"));
        pnExample.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Beispiel"));
        panel.add(pnColors, new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        panel.add(pnExample, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        final JLabel lbKeyword = new JLabel("Schlüsselwörter");
        final JLabel lbPrimitiveType = new JLabel("Primitive Datentypen");
        final JLabel lbStringLiteral = new JLabel("Zeichenketten");
        final JLabel lbError = new JLabel("Syntaxfehler");
        final JLabel lbComment = new JLabel("Kommentare");
        final JButton btKeywordColor = new JButton(
                new RectangleIcon(20, 10, 20, 10, new BasicStroke(1), Color.DARK_GRAY, Color.YELLOW, Color.WHITE, 1));
        final JButton btPrimitiveTypeColor = new JButton();
        final JButton btStringLiteralColor = new JButton();
        final JButton btErrorColor = new JButton();
        final JButton btCommentColor = new JButton();
        final JComboBox<ToStringWrapper<Integer>> cbKeywordStyle = new JComboBox<>();
        final JComboBox<ToStringWrapper<Integer>> cbPrimitiveTypeStyle = new JComboBox<>();
        final JComboBox<ToStringWrapper<Integer>> cbStringLiteralStyle = new JComboBox<>();
        final JComboBox<ToStringWrapper<Integer>> cbErrorStyle = new JComboBox<>();
        final JComboBox<ToStringWrapper<Integer>> cbCommentStyle = new JComboBox<>();
        btKeywordColor.setPreferredSize(
                new Dimension(btKeywordColor.getPreferredSize().width, cbKeywordStyle.getPreferredSize().height));
        btPrimitiveTypeColor.setPreferredSize(
                new Dimension(btPrimitiveTypeColor.getPreferredSize().width, cbKeywordStyle.getPreferredSize().height));
        btStringLiteralColor.setPreferredSize(
                new Dimension(btStringLiteralColor.getPreferredSize().width, cbKeywordStyle.getPreferredSize().height));
        btErrorColor.setPreferredSize(
                new Dimension(btErrorColor.getPreferredSize().width, cbKeywordStyle.getPreferredSize().height));
        btCommentColor.setPreferredSize(
                new Dimension(btCommentColor.getPreferredSize().width, cbKeywordStyle.getPreferredSize().height));
        btKeywordColor.setBorderPainted(false);

        pnColors.setLayout(new GridBagLayout());
        pnColors.add(lbKeyword, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btKeywordColor, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbKeywordStyle, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pnColors.add(lbPrimitiveType, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btPrimitiveTypeColor, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbPrimitiveTypeStyle, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pnColors.add(lbStringLiteral, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btStringLiteralColor, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbStringLiteralStyle, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pnColors.add(lbError, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btErrorColor, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbErrorStyle, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pnColors.add(lbComment, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHEAST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btCommentColor, new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbCommentStyle, new GridBagConstraints(2, 4, 1, 1, 0, 1, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        return panel;
    }

    void okButtonPressed(final ActionEvent event) {

        setResult(RESULT_OK);
        dispose();
    }
}