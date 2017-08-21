package de.chrlembeck.codegen.generator.lang;

import org.antlr.v4.runtime.ParserRuleContext;

import de.chrlembeck.codegen.generator.Position;

/**
 * Abstrakte Oberklasse für alle Elemente, aus denen eine Template-Datei nach dem Parsen aufgebaut ist.
 * 
 * @author Christoph Lembeck
 *
 * @param <T>
 *            Typ des ParserRuleContextes, wie er vom ANTLR-Parser für das Element erzeugt wird.
 */
public abstract class AbstractTemplateMember<T extends ParserRuleContext> {

    /**
     * Referenz auf den ursprünglich von ANTLR erzeugten Kontext für das vom Parser erkannte Element.
     */
    private T context;

    /**
     * Element, in das dieses Element als Child eingebettet ist.
     */
    private AbstractTemplateMember<?> parent;

    /**
     * Erstellt ein neues TemplateMember und speichert den übergebenen ParserRuleKontext.
     * 
     * @param ctx
     *            ParserRuleKontext, wie er von ANTLR zu diesem Element zurückgegeben wurde.
     */
    public AbstractTemplateMember(final T ctx) {
        this.context = ctx;
    }

    /**
     * Gibt den ursprünglichen ParserRuleContext des ANTLR-Parsers für dieses Element zurück.
     * 
     * @return ParserRuleContext für dieses Element.
     */
    public T getContext() {
        return context;
    }

    /**
     * Gibt die Startposition dieses Elements in der Template-Datei zurück.
     * 
     * @return Position des ersten Zeichens dieses Elements in der Template-Datei.
     */
    public final Position getStartPosition() {
        return new Position(context.getStart());
    }

    /**
     * Speichert die Referenz auf das Element, in das dieses Element eingebettet ist.
     * 
     * @param templateMember
     *            Referenz auf das Element, in dem dieses Element als child vorhanden ist.
     */
    public void setParent(final AbstractTemplateMember<?> templateMember) {
        this.parent = templateMember;
    }

    /**
     * Gibt das Element zurück, in das dieses Element direkt eingebettet ist.
     * 
     * @return Element, in dem dieses Element als child vorhanden ist.
     */
    public AbstractTemplateMember<?> getParent() {
        return parent;
    }

    /**
     * Verfolgt die Kette der Parent-Beziehungen so lange, bis das TemplateFile gefunden wurde, in dem dieses Element
     * eingebettet ist.
     * 
     * @return TemplateFile, in das dieses Element eingebettet ist.
     */
    public TemplateFile getTemplateFile() {
        AbstractTemplateMember<?> current = parent;
        while (current != null && !(current instanceof TemplateStatement)) {
            current = current.getParent();
        }
        return current == null ? null : ((TemplateStatement) current).getTemplateFile();
    }
}