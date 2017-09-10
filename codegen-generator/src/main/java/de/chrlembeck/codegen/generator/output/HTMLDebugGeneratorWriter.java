package de.chrlembeck.codegen.generator.output;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.chrlembeck.codegen.generator.Position;
import de.chrlembeck.codegen.generator.lang.Executable;
import de.chrlembeck.codegen.generator.lang.TemplateFile;

public class HTMLDebugGeneratorWriter implements GeneratorWriter {

    private final Writer writer;

    private boolean headerWritten = false;

    private Map<URI, TemplateFile> templateFiles = new TreeMap<>();

    public HTMLDebugGeneratorWriter(final Writer writer) {
        this.writer = Objects.requireNonNull(writer);
    }

    @Override
    public void append(final String text, final Executable<?> sourceExecutable, final ParserRuleContext context)
            throws IOException {
        final TemplateFile templateFile = sourceExecutable.getTemplateFile();
        if (!templateFiles.containsKey(templateFile.getResourceIdentifier())) {
            templateFiles.put(templateFile.getResourceIdentifier(), templateFile);
        }
        final StringBuilder infoText = new StringBuilder();
        final Token startToken = context.start;
        final int length = context.stop.getStopIndex() - startToken.getStartIndex() + 1;
        infoText.append("TemplateURI=" + sourceExecutable.getTemplateFile().getResourceIdentifier() + " ");
        infoText.append(new Position(startToken).toShortString() + " length=" + length + "("
                + startToken.getStartIndex() + ".." + context.stop.getStopIndex() + ")");
        infoText.append("[" + context.getText() + "]");
        infoText.append(" token=" + context.start.getTokenIndex() + "-" + context.stop.getTokenIndex());
        append(text, infoText.toString());

    }

    @Override
    public void append(final String text, final Executable<?> sourceExecutable, final TerminalNode terminal)
            throws IOException {
        final TemplateFile templateFile = sourceExecutable.getTemplateFile();
        if (!templateFiles.containsKey(templateFile.getResourceIdentifier())) {
            templateFiles.put(templateFile.getResourceIdentifier(), templateFile);
        }
        final StringBuilder infoText = new StringBuilder();
        final Token token = terminal.getSymbol();
        final int length = token.getStopIndex() - token.getStartIndex() + 1;
        infoText.append("TemplateURI=" + sourceExecutable.getTemplateFile().getResourceIdentifier() + " ");
        infoText.append(new Position(token).toShortString() + " length=" + length + "("
                + token.getStartIndex() + ".." + token.getStopIndex() + ")");
        infoText.append(" token=" + token.getTokenIndex());

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
        // TODO ToolTip-Text wie hier anzeigen:
        // https://www.w3schools.com/howto/tryit.asp?filename=tryhow_css_tooltip
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
        writer.append("</pre>\n");
        writer.append("<hr/>");
        for (final TemplateFile templateFile : templateFiles.values()) {
            writer.append("<p>");
            writer.append("<h3>" + templateFile.getResourceIdentifier() + "</h3>\n");
            writer.append("<pre>");
            try (InputStream input = templateFile.getResourceIdentifier().toURL().openStream();
                    InputStreamReader reader = new InputStreamReader(input, Charset.forName("UTF-8"))) {
                insertParseTree(reader, 0, templateFile.getContext());

            }
            writer.append("</pre>");
            writer.append("</p>");
        }
        writer.append("</body>\n</html>");
    }

    private int insertParseTree(final Reader input, int pos, final ParseTree parseTree) throws IOException {
        if (parseTree.getChildCount() > 0) {
            for (int i = 0; i < parseTree.getChildCount(); i++) {
                pos = insertParseTree(input, pos, parseTree.getChild(i));
            }
            return pos;
        } else {
            final Token token = (Token) parseTree.getPayload();
            final int tokenStart = token.getStartIndex();
            while (pos < tokenStart) {
                final int nextChar = input.read();
                writer.append(htmlEscape((char) nextChar));
                pos++;
            }
            final StringBuilder tokenText = new StringBuilder();
            for (int i = tokenStart; i <= token.getStopIndex(); i++) {
                tokenText.append(htmlEscape((char) input.read()));
                pos++;
            }
            appendSourceToken(token, tokenText.toString());

            return pos;
        }
    }

    private void appendSourceToken(final Token token, final String tokenText) throws IOException {
        writer.append("<span id=\"token_" + token.getTokenIndex() + "\">");
        writer.append(tokenText + "</span>");

    }

    private CharSequence htmlEscape(final char nextChar) {
        if (nextChar > 127) {
            return "&#x" + Integer.toHexString(nextChar) + ";";
        }

        return Character.toString(nextChar);
    }
}