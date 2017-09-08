package de.chrlembeck.codegen.generator.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.chrlembeck.codegen.generator.Position;
import de.chrlembeck.codegen.generator.lang.Executable;

public class HTMLDebugGeneratorWriter implements GeneratorWriter {

    private final Writer writer;

    private boolean headerWritten = false;

    public HTMLDebugGeneratorWriter(final Writer writer) {
        this.writer = Objects.requireNonNull(writer);
    }

    @Override
    public void append(final String text, final Executable<?> sourceExecutable, final ParserRuleContext context)
            throws IOException {
        final StringBuilder infoText = new StringBuilder();

        final Token startToken = context.start;
        final int length = context.stop.getStopIndex() - startToken.getStartIndex() + 1;
        infoText.append("TemplateURI=" + sourceExecutable.getTemplateFile().getResourceIdentifier() + " ");
        infoText.append(new Position(startToken).toShortString() + " length=" + length + "("
                + startToken.getStartIndex() + ".." + context.stop.getStopIndex() + ")");
        infoText.append("[" + context.getText() + "]");
        append(text, infoText.toString());

    }

    @Override
    public void append(final String text, final Executable<?> sourceExecutable, final TerminalNode terminal)
            throws IOException {
        final StringBuilder infoText = new StringBuilder();

        final Token token = terminal.getSymbol();
        final int length = token.getStopIndex() - token.getStartIndex() + 1;
        infoText.append("TemplateURI=" + sourceExecutable.getTemplateFile().getResourceIdentifier() + " ");
        infoText.append(new Position(token).toShortString() + " length=" + length + "("
                + token.getStartIndex() + ".." + token.getStopIndex() + ")");
        append(text, infoText.toString());

    }

    private void append(final String text, final String infoText) throws IOException {
        if (!headerWritten) {
            writeHeader();
            headerWritten = true;
        }
        writer.append("<a title=\"");
        writer.append(infoText);
        writer.append("\">");
        writer.append("<span style=\"border-style:solid; border-color: #800000; border-width: 1px;\">");
        writer.append(escape(text));
        writer.append("</span>");
        writer.append("</a>");
    }

    private CharSequence escape(final String text) {
        return text.replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("<", "&lt;").replaceAll(">", "&gt;")
                .replaceAll("'", "&apos;").replaceAll(" ", "&#x22C5;").replaceAll("\n", "&#x21B5;\n");

    }

    @Override
    public void close() throws IOException {
        writeFooter();
        writer.close();
    }

    private void writeHeader() throws IOException {
        writer.append("<html>\n<body>\n<pre>");
    }

    private void writeFooter() throws IOException {
        writer.append("</pre>\n</body>\n</html>");
    }
}