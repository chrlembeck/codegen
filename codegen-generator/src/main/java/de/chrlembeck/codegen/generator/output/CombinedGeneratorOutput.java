package de.chrlembeck.codegen.generator.output;

import java.io.IOException;
import java.util.Objects;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.chrlembeck.codegen.generator.lang.Executable;

public class CombinedGeneratorOutput implements GeneratorOutput {

    private GeneratorOutput firstOutput;

    private GeneratorOutput secondOutput;

    public CombinedGeneratorOutput(final GeneratorOutput first, final GeneratorOutput second) {
        this.firstOutput = Objects.requireNonNull(first);
        this.secondOutput = Objects.requireNonNull(second);
    }

    @Override
    public GeneratorWriter getWriter(final String channelName, final OutputPreferences prefs) throws IOException {
        return new CombinedWriter(firstOutput.getWriter(channelName, prefs),
                secondOutput.getWriter(channelName, prefs));
    }

    @Override
    public void closeAll() throws IOException {
        try {
            firstOutput.closeAll();
        } finally {
            secondOutput.closeAll();
        }
    }

    static class CombinedWriter implements GeneratorWriter {

        private GeneratorWriter firstWriter;

        private GeneratorWriter secondWriter;

        public CombinedWriter(final GeneratorWriter firstWriter, final GeneratorWriter secondWriter) {
            this.firstWriter = firstWriter;
            this.secondWriter = secondWriter;
        }

        @Override
        public void append(final String text, final Executable<?> sourceExecutable, final ParserRuleContext context)
                throws IOException {
            firstWriter.append(text, sourceExecutable, context);
            secondWriter.append(text, sourceExecutable, context);
        }

        @Override
        public void append(final String text, final Executable<?> sourceExecutable, final TerminalNode terminalNode)
                throws IOException {
            firstWriter.append(text, sourceExecutable, terminalNode);
            secondWriter.append(text, sourceExecutable, terminalNode);
        }

        @Override
        public void close() throws IOException {
            try {
                firstWriter.close();
            } finally {
                secondWriter.close();
            }
        }
    }
}