package de.chrlembeck.codegen.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.util.SwingUtil;

public abstract class AbstractDialog extends JDialog implements ComponentListener {

    public static final int RESULT_CANCEL = 0;

    public static final int RESULT_OK = 1;

    public static final int RESULT_UNKNOWN = -1;

    private static final long serialVersionUID = -2099847345062045877L;

    private JPanel buttonPanel;

    private int result = RESULT_UNKNOWN;

    public AbstractDialog(final Frame owner, final String title) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        init();
    }

    public AbstractDialog(final Window owner, final String title) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        init();
    }

    public int getResult() {
        return result;
    }

    private void init() {
        setLayout(new BorderLayout());
        final JPanel mainPanel = createMainPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("0xcacaca")),
                        BorderFactory.createMatteBorder(1, 0, 0, 0, Color.WHITE)));

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        getRootPane().registerKeyboardAction(new CloseAction(null, RESULT_CANCEL, 0, null),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        setMinimumSize(getPreferredSize());
        pack();
        addComponentListener(this);
        SwingUtil.centerOnScreen(this);
    }

    protected abstract JPanel createMainPanel();

    public void addButton(final Action action) {
        buttonPanel.add(new JButton(action));
    }

    public void addCancelButton() {
        addCancelButton("Abbrechen", KeyEvent.VK_C, IconFactory.CANCEL_22.icon());
    }

    public void addCancelButton(final String text, final int mnemonic, final Icon icon) {
        addButton(new CloseAction(text, RESULT_CANCEL, mnemonic, icon));
    }

    public void addOKButton(final String title, final int mnemonic, final Icon icon) {
        addButton(new CloseAction(title, RESULT_OK, mnemonic, icon));
    }

    class CloseAction extends AbstractAction {

        private static final long serialVersionUID = 6249414823221156794L;

        private int resultWhenClicked;

        public CloseAction(final String title, final int result, final int mnemonic, final Icon icon) {
            putValue(NAME, title);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(SMALL_ICON, icon);
            this.resultWhenClicked = result;
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            setResult(resultWhenClicked);
            dispose();
        }
    }

    public void setResult(final int newResult) {
        this.result = newResult;
    }

    @Override
    public void componentHidden(final ComponentEvent event) {
        // nothing to do
    }

    @Override
    public void componentMoved(final ComponentEvent event) {
        // nothing to do
    }

    @Override
    public void componentResized(final ComponentEvent event) {
        // nothing to do
    }

    /**
     * Passt die größe des Dialog an seine gewünscht Mindestgrößen an. Diese kann leider erst dann richtig berechnet
     * werden, wenn der Dialog bereits angezeigt wird. Daher wird dies über den ComponentListener organisiert.
     * 
     * @param event
     *            Event, welches über das Sichtbarwerden des Dialog informiert.
     */
    @Override
    public void componentShown(final ComponentEvent event) {
        final Dimension preferredSize = getPreferredSize();
        setMinimumSize(preferredSize);
        setSize(Math.max(getWidth(), preferredSize.width), Math.max(getHeight(), preferredSize.height));
    }
}
