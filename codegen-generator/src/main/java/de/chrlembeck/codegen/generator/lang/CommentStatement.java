package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.Generator;
import lang.CodeGenParser.CommentStatementContext;

/**
 * Enthält Kommentare, die innerhalb der Template-Datei einthalten sind.
 * 
 * @author Christoph Lembeck
 */
public class CommentStatement extends AbstractTemplateMember<CommentStatementContext>
        implements UserCodeOrStatements<CommentStatementContext> {

    /**
     * Text des Kommentars.
     */
    private String comment;

    /**
     * Erzeugt einen neuen Kommentar aus den Daten des Parsers.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen der Datei erzeugt hat.
     */
    public CommentStatement(final CommentStatementContext ctx) {
        super(ctx);
        this.comment = ctx.BlockComment().getText();
    }

    /**
     * Gibt den Text des Kommentars zurück.
     * 
     * @return Text des Kommentars.
     */
    public String getComment() {
        return comment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Comment[" + comment + ']';
    }

    /**
     * Macht nix...
     */
    @Override
    public void execute(final Generator generator, final Object model, final Environment environment) {
        // nothing to do here
    }
}