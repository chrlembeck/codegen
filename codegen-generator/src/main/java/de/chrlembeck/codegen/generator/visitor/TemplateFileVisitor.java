package de.chrlembeck.codegen.generator.visitor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.chrlembeck.codegen.generator.ParserException;
import de.chrlembeck.codegen.generator.lang.AbstractTemplateMember;
import de.chrlembeck.codegen.generator.lang.CommentStatement;
import de.chrlembeck.codegen.generator.lang.TemplateFile;
import de.chrlembeck.codegen.grammar.CodeGenParser.CommentStatementContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.ImportStatementContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.TemplateFileContext;
import de.chrlembeck.codegen.grammar.CodeGenParser.TemplateStatementContext;
import de.chrlembeck.codegen.grammar.CodeGenParserBaseVisitor;

import static de.chrlembeck.codegen.grammar.CodeGenLexer.AnyChar;

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
        final List<AbstractTemplateMember<?>> statements = new ArrayList<>();
        final UserCodeOrStatementVisitor commentVisitor = new UserCodeOrStatementVisitor();
        final TemplateStatementVisitor templateStatementVisitor = new TemplateStatementVisitor();
        for (int idx = 0; idx < ctx.getChildCount(); idx++) {
            final ParseTree child = ctx.getChild(idx);
            if (child instanceof CommentStatementContext) {
                statements.add((CommentStatement) child.accept(commentVisitor));
            } else if (child instanceof TemplateStatementContext) {
                statements.add(child.accept(templateStatementVisitor));
            } else if (child instanceof ImportStatementContext) {
                statements.add(child.accept(impVisitor));
            } else if (child instanceof TerminalNode tn) {
                if (idx != ctx.getChildCount() - 1 && !isWhitespace(tn)) {
                    throw new IllegalArgumentException("TerminalNode in the middle of an ParseTree.");
                }
            } else {
                throw new ParserException(
                        "Unerwarteter child type " + child.getClass().getName() + " in der Template-Datei.", ctx);
            }
        }
        final TemplateFile templateFile = new TemplateFile(resourceIdentifier, ctx, statements);
        return templateFile;
    }

    private static boolean isWhitespace(TerminalNode terminal) {
        return terminal.getSymbol().getType() == AnyChar && terminal.getText().isBlank();
    }
}