package de.chrlembeck.codegen.generator.output;

import java.io.BufferedReader;
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
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.chrlembeck.codegen.generator.Position;
import de.chrlembeck.codegen.generator.lang.Executable;
import de.chrlembeck.codegen.generator.lang.TemplateFile;

public class HTMLDebugGeneratorWriter implements GeneratorWriter {

    private final Writer writer;

    private boolean headerWritten = false;

    private final static String DEBUG_HTML_JAVASCRIPT = "/debug/debug_html_javascript.js";

    private final static String DEBUG_HTML_STYLE = "/debug/debug_html_style.css";

    private Map<URI, Pair<Integer, TemplateFile>> templateFiles = new TreeMap<>();

    private String channelName;

    public HTMLDebugGeneratorWriter(final Writer writer, final String channelName) {
        this.writer = Objects.requireNonNull(writer);
        this.channelName = channelName;
    }

    @Override
    public void append(final String text, final Executable<?> sourceExecutable, final ParserRuleContext context)
            throws IOException {
        final TemplateFile templateFile = sourceExecutable.getTemplateFile();
        if (!templateFiles.containsKey(templateFile.getResourceIdentifier())) {
            templateFiles.put(templateFile.getResourceIdentifier(),
                    new Pair<Integer, TemplateFile>(templateFiles.size(), templateFile));
        }
        final StringBuilder infoText = new StringBuilder();
        final Token startToken = context.start;
        final int length = context.stop.getStopIndex() - startToken.getStartIndex() + 1;
        infoText.append("TemplateURI=" + sourceExecutable.getTemplateFile().getResourceIdentifier() + " ");
        infoText.append(new Position(startToken).toShortString() + " length=" + length + "("
                + startToken.getStartIndex() + ".." + context.stop.getStopIndex() + ")");
        infoText.append("[" + context.getText() + "]");
        infoText.append(" token=" + context.start.getTokenIndex() + "-" + context.stop.getTokenIndex());
        append(text, infoText.toString(), getTemplateFileIndex(templateFile), startToken);
    }

    @Override
    public void append(final String text, final Executable<?> sourceExecutable, final TerminalNode terminal)
            throws IOException {
        final TemplateFile templateFile = sourceExecutable.getTemplateFile();
        if (!templateFiles.containsKey(templateFile.getResourceIdentifier())) {
            templateFiles.put(templateFile.getResourceIdentifier(),
                    new Pair<Integer, TemplateFile>(templateFiles.size(), templateFile));
        }
        final StringBuilder infoText = new StringBuilder();
        final Token token = terminal.getSymbol();
        final int length = token.getStopIndex() - token.getStartIndex() + 1;
        infoText.append("TemplateURI=" + sourceExecutable.getTemplateFile().getResourceIdentifier() + " ");
        infoText.append(new Position(token).toShortString() + " length=" + length + "("
                + token.getStartIndex() + ".." + token.getStopIndex() + ")");
        infoText.append(" token=" + token.getTokenIndex());

        append(text, infoText.toString(), getTemplateFileIndex(templateFile), token);
    }

    public int getTemplateFileIndex(final TemplateFile templateFile) {
        return templateFiles.get(templateFile.getResourceIdentifier()).a.intValue();
    }

    private void append(final String text, final String infoText, final int templateFileIdx, final Token token)
            throws IOException {
        if (!headerWritten) {
            writeHeader();
            headerWritten = true;
        }
        writer.append("<a title=\"");
        writer.append(infoText);
        writer.append("\"");
        writer.append(" onclick=\"selectToken(" + templateFileIdx + ", " + token.getTokenIndex() + ")\"");
        writer.append(" onmouseover=\"highlightToken(" + templateFileIdx + ", " + token.getTokenIndex() + ")\"");
        writer.append(" onmouseout=\"unhighlight()\"");
        writer.append(">");
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
        writer.append("<!DOCTYPE html>\n<html>\n<head>\n");
        writer.append("<style>\n");
        inlineFile(DEBUG_HTML_STYLE);
        writer.append("</style>\n");
        writer.append("<script>\n");
        inlineFile(DEBUG_HTML_JAVASCRIPT);
        writer.append("</script>\n<body>\n");
        writer.append("<div class=\"panelHeader\">Channel: " + channelName + "</div>\n");
        writer.append("<div class=\"generatedFilePanelContent\">\n");
        writer.append("<pre>");
    }

    private void inlineFile(final String fileLocation) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(fileLocation);
                InputStreamReader inputReader = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(inputReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.append(line);
                writer.append('\n');
            }
        }
    }

    private void writeFooter() throws IOException {
        writer.append("</pre>\n");
        writer.append("</div>\n");
        for (final Pair<Integer, TemplateFile> idTemplateFile : templateFiles.values()) {
            final TemplateFile templateFile = idTemplateFile.b;
            writer.append("<div class=\"panelHeader\">" + templateFile.getResourceIdentifier() + "</div>\n");
            writer.append("<div class=\"templatePanelContent\">");
            writer.append("<pre>");
            try (InputStream input = templateFile.getResourceIdentifier().toURL().openStream();
                    InputStreamReader reader = new InputStreamReader(input, Charset.forName("UTF-8"))) {
                insertParseTree(reader, 0, templateFile.getContext(), idTemplateFile.a);
            }
            writer.append("</pre>\n");
            writer.append("</div>\n");
        }
        writer.append("</body>\n</html>");
    }

    private int insertParseTree(final Reader input, int pos, final ParseTree parseTree, final int templateIdx)
            throws IOException {
        if (parseTree.getChildCount() > 0) {
            for (int i = 0; i < parseTree.getChildCount(); i++) {
                pos = insertParseTree(input, pos, parseTree.getChild(i), templateIdx);
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
            appendSourceToken(token, tokenText.toString(), templateIdx);

            return pos;
        }
    }

    private void appendSourceToken(final Token token, final String tokenText, final int templateIdx)
            throws IOException {
        writer.append("<span id=\"token_" + templateIdx + "_" + token.getTokenIndex() + "\">");
        writer.append(tokenText + "</span>");
    }

    private CharSequence htmlEscape(final char nextChar) {
        if (nextChar > 127) {
            return "&#x" + Integer.toHexString(nextChar) + ";";
        }
        if (nextChar == ' ') {
            return "&#x22C5;";
        }
        if (nextChar == '\n') {
            return "&#x21B5;\n";
        }
        if (nextChar == '&') {
            return "&amp;";
        }
        if (nextChar == '<') {
            return "&lt;";
        }
        if (nextChar == '>') {
            return "&gt;";
        }
        if (nextChar == '"') {
            return "&quot;";
        }
        if (nextChar == '\'') {
            return "&apos;";
        }
        if (nextChar == 'ä') {
            return "&auml;";
        }
        if (nextChar == 'Ä') {
            return "&Auml;";
        }
        if (nextChar == 'ö') {
            return "&ouml;";
        }
        if (nextChar == 'Ö') {
            return "&Ouml;";
        }
        if (nextChar == 'ü') {
            return "&uuml;";
        }
        if (nextChar == 'Ü') {
            return "&Uuml;";
        }
        if (nextChar == '\u00ab') {
            return "&laquo;";
        }
        if (nextChar == '\u00bb') {
            return "&raquo;";
        }
        return Character.toString(nextChar);
    }
}