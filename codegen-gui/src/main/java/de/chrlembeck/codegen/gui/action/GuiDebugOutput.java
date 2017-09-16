package de.chrlembeck.codegen.gui.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.chrlembeck.codegen.generator.output.GeneratorOutput;
import de.chrlembeck.codegen.generator.output.HTMLDebugGeneratorWriter;
import de.chrlembeck.codegen.generator.output.OutputPreferences;
import de.chrlembeck.codegen.gui.CodeGenGui;

public class GuiDebugOutput implements GeneratorOutput {

    private CodeGenGui codeGenGui;

    private Map<String, HTMLDebugGeneratorWriter> writers = new TreeMap<>();

    public GuiDebugOutput(final CodeGenGui codeGenGui) {
        this.codeGenGui = codeGenGui;
    }

    @Override
    public HTMLDebugGeneratorWriter getWriter(final String channelName, final OutputPreferences prefs)
            throws IOException {
        final File tempFile = File.createTempFile("codegen_debug_", ".html");
        tempFile.deleteOnExit();
        final Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile),
                prefs.getCharsetForChannel(channelName));
        final String tokenCSS = codeGenGui.getTokenStyles().toCSS();
        final HTMLDebugGeneratorWriter debugWriter = new HTMLDebugGeneratorWriter(writer, channelName, tempFile,
                tokenCSS);
        writers.put(channelName, debugWriter);
        return debugWriter;
    }

    @Override
    public void closeAll() throws IOException {
        IOException exception = null;
        for (final HTMLDebugGeneratorWriter writer : writers.values()) {
            try {
                writer.close();
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    public Map<String, HTMLDebugGeneratorWriter> getWriters() {
        return writers;
    }

    public File writeSummary() throws IOException {
        final File tempFile = File.createTempFile("codegen_debug_summary_", ".html");
        tempFile.deleteOnExit();
        try (final Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), Charset.forName("UTF-8"))) {
            writer.append("<!DOCTYPE html>\n<html>\n<head>\n");
            writer.append("<body>\n");
            writer.append("<ul>\n");
            for (final Entry<String, HTMLDebugGeneratorWriter> entry : getWriters().entrySet()) {
                final HTMLDebugGeneratorWriter debugWriter = entry.getValue();
                writer.append("<li><a href=\"");
                writer.append(debugWriter.getOutputFile().toURI().toURL().toString());
                writer.append("\">");
                writer.append(entry.getKey() + " (" + debugWriter.getCharacterCount() + " Zeichen, "
                        + debugWriter.getLineCount() + " Zeilen)");
                writer.append("</a></li>\n");
            }
            writer.append("</ul>\n");
            writer.append("</body>\n</html>");
        }
        return tempFile;
    }
}