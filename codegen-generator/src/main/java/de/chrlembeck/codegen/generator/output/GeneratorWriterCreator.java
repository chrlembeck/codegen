package de.chrlembeck.codegen.generator.output;

import java.io.Writer;
import java.nio.file.Path;

@FunctionalInterface
public interface GeneratorWriterCreator<T extends GeneratorWriter> {

    T createWriter(Writer outputWriter, String channelName, Path outputPath);
}