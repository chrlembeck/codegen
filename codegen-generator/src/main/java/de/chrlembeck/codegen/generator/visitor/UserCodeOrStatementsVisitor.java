package de.chrlembeck.codegen.generator.visitor;

import java.util.List;
import java.util.stream.Collectors;

import de.chrlembeck.codegen.generator.lang.UserCodeOrStatements;
import lang.CodeGenParser.UserCodeOrStatementsContext;
import lang.CodeGenParserBaseVisitor;

/**
 * Visitor für die Verarbeitung des Context-Baumes des ANTLR-Parsers. Dieser Visitor enthält alle Konvertierungen von
 * Kontext-Objekten zu einer Liste von UserCodeOrStatements der CodeGen-Grammatik.
 * 
 * Christoph Lembeck
 */
public class UserCodeOrStatementsVisitor extends CodeGenParserBaseVisitor<List<UserCodeOrStatements<?>>> {

    /**
     * Transformiert einen {@link UserCodeOrStatementsContext} des Parsers in eine vom Generator verarbeitbare Liste von
     * {@link UserCodeOrStatements}.
     * 
     * @param ctx
     *            Vom Parser erzeugtes Kontext-Objekt.
     * @return Zum Kontext-Objekt passendes und vom Generator verarbeitbares Element der Template-Datei.
     */
    @Override
    public List<UserCodeOrStatements<?>> visitUserCodeOrStatements(final UserCodeOrStatementsContext ctx) {
        final UserCodeOrStatementVisitor visitor = new UserCodeOrStatementVisitor();
        return ctx.children.stream().map(c -> (UserCodeOrStatements<?>) c.accept(visitor)).collect(Collectors.toList());
    }
}