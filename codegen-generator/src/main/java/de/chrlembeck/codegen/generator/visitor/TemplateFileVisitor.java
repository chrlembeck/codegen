package de.chrlembeck.codegen.generator.visitor;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import de.chrlembeck.codegen.generator.lang.ImportStatement;
import de.chrlembeck.codegen.generator.lang.TemplateFile;
import de.chrlembeck.codegen.generator.lang.TemplateStatement;
import lang.CodeGenParser.TemplateFileContext;
import lang.CodeGenParserBaseVisitor;

/**
 * Visitor für die Verarbeitung des Context-Baumes des ANTLR-Parsers. Dieser Visitor enthält die Konvertierung von
 * Kontext-Objekten zu einem TemplateFile der CodeGen-Grammatik.
 * 
 * Christoph Lembeck
 */
public class TemplateFileVisitor extends CodeGenParserBaseVisitor<TemplateFile> {

    /**
     * Pfad zur Datei des TemplateFiles, welches durch diesen Visitor erzeugt werden soll.
     */
    private URI resourceIdentifier;

    /**
     * Erstellt einen neuen Visitor und hinterlegt den resourceIdentifier für von ihm erzeugte TemplateFiles.
     * 
     * @param resourceIdentifier
     *            Pfad zur Datei des TemplateFiles, welches durch diesen Visitor erzeugt werden soll.
     */
    public TemplateFileVisitor(final URI resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }

    /**
     * Transformiert einen {@link TemplateFileContext} des Parsers in ein vom Generator verarbeitbares
     * {@link TemplateFile}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public TemplateFile visitTemplateFile(final TemplateFileContext ctx) {
        final ImportStatementVisitor impVisitor = new ImportStatementVisitor();
        final List<ImportStatement> imports = ctx.importStatement()
                .stream()
                .map(imp -> imp.accept(impVisitor))
                .collect(Collectors.toList());
        final List<TemplateStatement> templates = ctx.templateStatement()
                .stream()
                .map(t -> t.accept(new TemplateStatementVisitor()))
                .collect(Collectors.toList());
        final TemplateFile templateFile = new TemplateFile(resourceIdentifier, ctx, imports, templates);
        return templateFile;
    }
}