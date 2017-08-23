package de.chrlembeck.codegen.generator.visitor;

import de.chrlembeck.codegen.generator.lang.ImportStatement;
import lang.CodeGenParser.ImportStatementContext;
import lang.CodeGenParserBaseVisitor;

/**
 * Visitor für die Verarbeitung des Context-Baumes des ANTLR-Parsers. Dieser Visitor enthält alle Konvertierungen von
 * Kontext-Objekten zu ImportStatements der CodeGen-Grammatik.
 * 
 * Christoph Lembeck
 */
public class ImportStatementVisitor extends CodeGenParserBaseVisitor<ImportStatement> {

    /**
     * Transformiert einen {@link ImportStatementContext} des Parsers in ein vom Generator verarbeitbares
     * {@link ImportStatement}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public ImportStatement visitImportStatement(final ImportStatementContext ctx) {
        final String prefix = ctx.prefix.getText();
        final String url = ctx.url.getText();
        return new ImportStatement(ctx, prefix, url);
    }
}
