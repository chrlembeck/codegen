module de.chrlembeck.codegen.generator {

    exports de.chrlembeck.codegen.generator;
    exports de.chrlembeck.codegen.generator.lang;
    exports de.chrlembeck.codegen.generator.output;
    exports de.chrlembeck.codegen.generator.visitor;
    exports de.chrlembeck.codegen.generator.model;
    exports de.chrlembeck.codegen.grammar;

    requires slf4j.api;
    requires antlr4.runtime;
    requires java.compiler;
    requires chrlembeck.util;
    requires java.desktop;
}