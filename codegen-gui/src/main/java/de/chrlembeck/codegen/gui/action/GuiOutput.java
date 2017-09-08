package de.chrlembeck.codegen.gui.action;

import java.io.IOException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.lang.Executable;
import de.chrlembeck.codegen.generator.output.GeneratorOutput;
import de.chrlembeck.codegen.generator.output.GeneratorWriter;
import de.chrlembeck.codegen.generator.output.OutputPreferences;
import de.chrlembeck.codegen.gui.CodeGenGui;

public class GuiOutput implements GeneratorOutput {

    private final Logger LOGGER = LoggerFactory.getLogger(GuiOutput.class);

    private CodeGenGui codeGenGui;

    public GuiOutput(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
    }

    @Override
    public GeneratorWriter getWriter(final String channelName, final OutputPreferences prefs) throws IOException {
        final JTextComponent textComponent = codeGenGui.createOutputPanel(channelName);
        return new GuiWriter(textComponent);
    }

    @Override
    public void closeAll() {
        // nothing to do
    }

    static class GuiWriter implements GeneratorWriter {

        private final Logger LOGGER = LoggerFactory.getLogger(GuiWriter.class);

        private JTextComponent textComponent;

        public GuiWriter(final JTextComponent textComponent) {
            this.textComponent = textComponent;
        }

        @Override
        public void append(final String text, final Executable<?> sourceExecutable, final TerminalNode node)
                throws IOException {
            append(text);
        }

        @Override
        public void append(final String text, final Executable<?> sourceExecutable, final ParserRuleContext context)
                throws IOException {
            append(text);
        }

        private void append(final String text) {
            final Document document = textComponent.getDocument();
            try {
                document.insertString(document.getLength(), text, null);
            } catch (final BadLocationException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        @Override
        public void close() throws IOException {
            // nothing to do
        }
    }
}