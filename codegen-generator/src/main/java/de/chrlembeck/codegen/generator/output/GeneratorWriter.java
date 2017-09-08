package de.chrlembeck.codegen.generator.output;

import java.io.IOException;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.chrlembeck.codegen.generator.lang.Executable;

public interface GeneratorWriter extends AutoCloseable {

    void append(String text, Executable<?> sourceExecutable, TerminalNode terminalNode)
            throws IOException;

    void append(String text, Executable<?> sourceExecutable, ParserRuleContext parserRuleContext)
            throws IOException;

    @Override
    void close() throws IOException;
}