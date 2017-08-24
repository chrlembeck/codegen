package de.chrlembeck.codegen.generator.lang;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Interface für alle innerhalb eines Templates verwendbaren Statements und Code-Blöcke.
 * 
 * @author Christoph Lembeck
 *
 * @param <T>
 *            Typ des ParserRuleContexts für das Statement oder den Code-Block.
 */
public interface UserCodeOrStatements<T extends ParserRuleContext> extends Executable<T> {

    /**
     * Setzt das Parent-Objekt, also das Objekt, in welches dieses Objekt in der Template-Definition direkt eingebettet
     * ist.
     * 
     * @param templateMember
     *            Parent-Objekt nach obiger Beschreibung.
     */
    public void setParent(AbstractTemplateMember<?> templateMember);
}