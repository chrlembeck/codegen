package de.chrlembeck.codegen.gui.action;

import java.io.IOException;
import java.io.Writer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.output.GeneratorOutput;
import de.chrlembeck.codegen.generator.output.OutputPreferences;
import de.chrlembeck.codegen.gui.CodeGenGui;

public class GuiOutput implements GeneratorOutput {

    private final Logger LOGGER = LoggerFactory.getLogger(GuiOutput.class);

    private CodeGenGui codeGenGui;

    public GuiOutput(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
    }

    @Override
    public Writer getWriter(final String channelName, final OutputPreferences prefs) throws IOException {
        final JTextComponent textComponent = codeGenGui.createOutputPanel(channelName);
        return new GuiWriter(textComponent);
    }

    @Override
    public void closeAll() {
        // nothing to do
    }

    static class GuiWriter extends Writer {

        private final Logger LOGGER = LoggerFactory.getLogger(GuiWriter.class);

        private JTextComponent textComponent;

        public GuiWriter(final JTextComponent textComponent) {
            this.textComponent = textComponent;
        }

        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            final Document document = textComponent.getDocument();
            try {
                document.insertString(document.getLength(), String.valueOf(cbuf, off, len), null);
            } catch (final BadLocationException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        @Override
        public void flush() throws IOException {
            // nothing to do
        }

        @Override
        public void close() throws IOException {
            // nothing to do
        }
    }
}