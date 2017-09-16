package de.chrlembeck.codegen.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import de.chrlembeck.codegen.generator.output.HTMLDebugGeneratorWriter;
import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.action.GuiDebugOutput;
import de.chrlembeck.util.objects.ToStringWrapper;
import de.chrlembeck.util.swing.SwingUtil;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class DebugDialog extends AbstractDialog {

    private static final long serialVersionUID = -6820063175007906902L;

    private GuiDebugOutput debugOutput;

    private JComboBox<ToStringWrapper<Entry<String, HTMLDebugGeneratorWriter>>> cbChannel;

    private WebView webView;

    public DebugDialog(final CodeGenGui codeGenGui, final GuiDebugOutput debugOutput) {
        super(codeGenGui, "Debug-View", ModalityType.MODELESS);
        this.debugOutput = debugOutput;
        initCBchannel();
        addCancelButton("Schließen", KeyEvent.VK_C, IconFactory.CANCEL_22.icon());
        setVisible(true);
        pack();
        SwingUtil.centerToScreen(this);
    }

    private void initCBchannel() {
        for (final Entry<String, HTMLDebugGeneratorWriter> entry : debugOutput.getWriters().entrySet()) {
            cbChannel.addItem(new ToStringWrapper<Entry<String, HTMLDebugGeneratorWriter>>(entry, e -> e.getKey()));
        }
        cbChannel.addItemListener(this::channelChanged);
        channelChanged(new ItemEvent(cbChannel, 0, cbChannel.getSelectedItem(), ItemEvent.SELECTED));
    }

    private void initWebView(final JFXPanel jxPanel) {
        Platform.runLater(() -> {
            webView = new WebView();
            jxPanel.setScene(new Scene(webView));
        });
    }

    @Override
    protected JPanel createMainPanel() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        cbChannel = new JComboBox<>();
        mainPanel.add(cbChannel, BorderLayout.NORTH);

        final JFXPanel jxPanel = new JFXPanel();
        jxPanel.setLayout(new BorderLayout());
        add(jxPanel, BorderLayout.CENTER);
        initWebView(jxPanel);
        final JScrollPane sp = new JScrollPane(jxPanel);
        sp.setPreferredSize(new Dimension(400, 300));
        mainPanel.add(sp, BorderLayout.CENTER);
        return mainPanel;
    }

    public void channelChanged(final ItemEvent event) {
        @SuppressWarnings("unchecked")
        final ToStringWrapper<Entry<String, HTMLDebugGeneratorWriter>> item = (ToStringWrapper<Entry<String, HTMLDebugGeneratorWriter>>) event
                .getItem();
        final HTMLDebugGeneratorWriter writer = item.getObject().getValue();
        final File file = writer.getOutputFile();
        Platform.runLater(() -> {
            try {
                final String url = file.toURI().toURL().toString();
                webView.getEngine().load(url);
            } catch (final MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> new DebugDialog(null, null));
    }
}