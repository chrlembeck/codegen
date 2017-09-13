package de.chrlembeck.codegen.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import de.chrlembeck.antlr.editor.AntlrDocument;
import de.chrlembeck.codegen.grammar.CodeGenParser.TemplateFileContext;
import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.TemplateEditorPane;
import de.chrlembeck.util.swing.SwingUtil;

public class FindDialog extends AbstractDialog {

    private TemplateEditorPane<TemplateFileContext> editorPane;

    private JTextField tfFindExpression;

    private JCheckBox cbCaseSensitive;

    private JButton findButton;

    public FindDialog(final CodeGenGui codeGenGui, final TemplateEditorPane<TemplateFileContext> editorPane) {
        super(codeGenGui, "Suchen", ModalityType.MODELESS);
        this.setAlwaysOnTop(true);
        this.editorPane = editorPane;

        findButton = addButton(new FindButtonAction());
        getRootPane().setDefaultButton(findButton);

        addCancelButton("Schließen", KeyEvent.VK_C, IconFactory.CANCEL_32.icon());
        pack();
        SwingUtil.centerOnScreen(this);
    }

    private AntlrDocument<TemplateFileContext> getDocument() {
        return editorPane.getAntlrDocument();
    }

    private static final long serialVersionUID = -872271966685866731L;

    @Override
    protected JPanel createMainPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final JLabel lbFindExpression = new JLabel("Suche");
        tfFindExpression = new JTextField("");
        cbCaseSensitive = new JCheckBox("Groß-/Kleinschreibung beachten");

        final JPanel pnOptions = new JPanel();
        pnOptions.setLayout(new GridBagLayout());
        pnOptions.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Optionen", TitledBorder.LEFT,
                        TitledBorder.DEFAULT_POSITION));
        pnOptions.add(cbCaseSensitive, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        panel.add(lbFindExpression, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        panel.add(tfFindExpression, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        panel.add(pnOptions,
                new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));

        return panel;
    }

    class FindButtonAction extends AbstractAction {

        private static final long serialVersionUID = -5870921414122493596L;

        public FindButtonAction() {
            putValue(NAME, "Suchen");
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            putValue(SMALL_ICON, IconFactory.FIND_32.icon());
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            final String text = tfFindExpression.getText();
            final int start = editorPane.getCaretPosition();
            final int length = getDocument().getLength() - start;
            final Matcher matcher = getDocument().find(text, start, length, cbCaseSensitive.isSelected());
            performSearch(text, start, matcher);
        }

        private void performSearch(final String text, final int start, Matcher matcher) {
            if (matcher.find()) {
                editorPane.setCaretPosition(start + matcher.end());
                editorPane.setSelectionStart(start + matcher.start());
                editorPane.setSelectionEnd(start + matcher.end());
            } else {
                final int option = JOptionPane.showConfirmDialog(FindDialog.this,
                        "Soll am Anfang des Dokumentes weitergesucht werden?", "Weitersuchen?",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
                if (option == JOptionPane.YES_OPTION) {
                    matcher = getDocument().find(text, 0, getDocument().getLength(), cbCaseSensitive.isSelected());
                    performSearch(text, 0, matcher);
                }
            }
        }
    }
}