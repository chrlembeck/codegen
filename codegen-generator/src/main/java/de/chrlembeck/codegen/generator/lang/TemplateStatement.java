package de.chrlembeck.codegen.generator.lang;

import java.io.IOException;
import java.util.List;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.Generator;
import de.chrlembeck.codegen.generator.ParserException;
import lang.CodeGenParser.TemplateStatementContext;

/**
 * Enthält eine Template-Definition innerhalb einer Template-Datei. Templates können vom Generator aufgerufen werden, so
 * dass alle in ihnen enthaltenen Statements und Code-Blöcke nacheinander abgearbeitet und ggf. in die zu generierenden
 * Artefakte geschrieben werden.
 * 
 * @author Christoph Lembeck
 */
public class TemplateStatement extends AbstractTemplateMember<TemplateStatementContext>
        implements Executable<TemplateStatementContext> {

    /**
     * Liste von Code-Blöcken und Statements, die das Template enthält.
     */
    private List<UserCodeOrStatements<?>> codeOrStatements;

    /**
     * Typ der Modelle zurück, die das Template verarbeiten kann. Dies sollten voll qualifizierte Namen von Klassen
     * sein.
     */
    private String type;

    /**
     * Name des Templates.
     */
    private String name;

    /**
     * Erstellt das TemplateStatement mit den übergebenen Daten.
     * 
     * @param ctx
     *            ANTLR-Kontext, wie er vom Parser beim Lesen des Dokumentes erzeugt wurde.
     * @param name
     *            Name des Templates.
     * @param type
     *            Typ der Modelle, die das Template verarbeiten kann.
     * @param cos
     *            Liste von Code-Blöcken und Statements, die das Template enthalten soll.
     */
    public TemplateStatement(final TemplateStatementContext ctx, final String name, final String type,
            final List<UserCodeOrStatements<?>> cos) {
        super(ctx);
        this.name = name;
        this.type = type;
        this.codeOrStatements = cos;
        this.codeOrStatements.forEach(codeOrStatement -> codeOrStatement.setParent(this));
    }

    /**
     * Gibt den Namen des Templates zurück.
     * 
     * @return Name des Templates.
     */
    public String getName() {
        return name;
    }

    /**
     * Gibt den Typ der Modelle zurück, die das Template verarbeiten kann. Dies sollten voll qualifizierte Namen von
     * Klassen sein.
     * 
     * @return Typ der verarbeitbaren Modelle.
     */
    public String getType() {
        return type;
    }

    /**
     * Versucht den vom Template akzeptierten Modelltyp als Class-Referenz zu laden und gibt diese zurück.
     * 
     * @return Class-Referenz des vom Template akzeptierten Modelltyp.
     */
    public Class<?> getTypeAsClass() {
        try {
            return Class.forName(type);
        } catch (final ClassNotFoundException cnfe) {
            throw new ParserException("Can not find class " + type + ". (" + getStartPosition() + ")", getContext(),
                    cnfe);
        }
    }

    /**
     * Gibt die Liste der enthaltenen Code-Blöcke und Statements zurück.
     * 
     * @return Liste der enthaltenen Code-Blöcke und Statements.
     */
    public List<UserCodeOrStatements<?>> getCodeOrStatements() {
        return codeOrStatements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Template[name=" + name + ", type=" + type + ", codeOrStatements=" + codeOrStatements + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Generator generator, final Object model, final Environment environment)
            throws IOException {
        for (final UserCodeOrStatements<?> ucost : codeOrStatements) {
            environment.createFrame(false);
            environment.execute(ucost, generator, model);
            environment.dropFrame();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TemplateFile getTemplateFile() {
        return getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TemplateFile getParent() {
        return (TemplateFile) super.getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParent(final AbstractTemplateMember<?> templateFile) {
        if (!(templateFile instanceof TemplateFile)) {
            throw new IllegalArgumentException("The parent of a TemplateStatement has to be a FemplateFile.");
        }
        super.setParent(templateFile);
    }
}