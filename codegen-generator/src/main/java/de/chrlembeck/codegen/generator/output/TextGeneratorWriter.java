package de.chrlembeck.codegen.generator.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.chrlembeck.codegen.generator.lang.Executable;

public class TextGeneratorWriter implements GeneratorWriter {

    private final Writer writer;

    public TextGeneratorWriter(final Writer writer) {
        this.writer = Objects.requireNonNull(writer);
    }

    @Override
    public void append(final String text, final Executable<?> sourceExecutable, final TerminalNode terminalNode)
            throws IOException {
        writer.append(text);
    }

    @Override
    public void append(final String text, final Executable<?> sourceExecutable, final ParserRuleContext context)
            throws IOException {
        writer.append(text);
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    public Writer getWriter() {
        return writer;
    }
}