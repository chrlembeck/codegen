package de.chrlembeck.codegen.generator.lang;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.chrlembeck.codegen.generator.TemplateResolver;
import lang.CodeGenParser.TemplateFileContext;

/**
 * Enthält die geparste und zur Ausführung fertige Repräsentation einer Template-Datei. Template-Dateien sollten in der
 * Regel über {@link TemplateResolver} geladen werden.
 *
 * @author Christoph Lembeck
 */
public class TemplateFile extends AbstractTemplateMember<TemplateFileContext> {

    /**
     * Resource identifier zur Spezifikation des ablageortes des Templates.
     */
    private URI resourceIdentifier;

    /**
     * Liste der in der Datei enthaltenen Imports zu anderen Template-Dateien.
     */
    private List<ImportStatement> importStatements;

    /**
     * Liste der in der Datei enthaltenen Template-Definitionen.
     */
    private List<TemplateStatement> templates;

    /**
     * Liste aller in der Datei enthaltenen Imports, TemplateStatements und CommentStatements in der Reihenfolge ihres
     * Vorkommens.
     */
    private List<AbstractTemplateMember<?>> members;

    /**
     * Erstellt eine Template-Datei aus den übergebenen Daten.
     * 
     * @param resourceIdentifier
     *            Resource identifier zur Spezifikation des ablageortes des Templates.
     * @param ctx
     *            Resource identifier zur Spezifikation des ablageortes des Templates. ANTLR-Kontext, wie er vom Parser
     *            beim Lesen des Dokumentes erzeugt wurde.
     * @param members
     *            Liste aller in der Datei enthaltenen Imports, TemplateStatements und CommentStatements in der
     *            Reihenfolge ihres Vorkommens.
     */
    public TemplateFile(final URI resourceIdentifier, final TemplateFileContext ctx,
            final List<AbstractTemplateMember<?>> members) {
        super(ctx);
        this.resourceIdentifier = resourceIdentifier;
        this.members = members;
        this.importStatements = new ArrayList<>();
        this.templates = new ArrayList<>();
        for (final AbstractTemplateMember<?> member : members) {
            if (member instanceof ImportStatement) {
                importStatements.add((ImportStatement) member);
            } else if (member instanceof TemplateStatement) {
                templates.add((TemplateStatement) member);
            }
        }
        templates.stream().forEach(templateStatement -> templateStatement.setParent(this));
    }

    /**
     * Gibt die Liste der enthaltenen Imprt-Statements zurück.
     * 
     * @return Liste der enthaltenen Imports.
     */
    public List<ImportStatement> getImportStatements() {
        return importStatements;
    }

    /**
     * Gibt die Liste der enthaltenen Template-Definitionen zurück.
     * 
     * @return Liste aller enthaltenen Template-Definitionen.
     */
    public List<TemplateStatement> getTemplateStatements() {
        return templates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return importStatements + ": " + templates;
    }

    /**
     * Gibt den resource identifier dieser Template-Datei zurück.
     * 
     * @return Resource identifier dieser Datei.
     */
    public URI getResourceIdentifier() {
        return resourceIdentifier;
    }

    /**
     * Sucht nach dem Import-Statement mit dem übergebenen Namen und gibt den dazu passenden resource identifier zurück.
     * Handelt es sich dabei um einen relativen identifier, wird dieser gegenüber des identifiers dieser Datei
     * aufgelöst.
     * 
     * @param prefix
     *            Name des zu suchenden Imports.
     * @return Aufgelöster identifier zum Laden der referenzierten Template-Datei.
     * @throws URISyntaxException
     *             Falls die URI in dem Import ungültig ist.
     * @see URI#resolve(URI)
     * @see URI#isAbsolute()
     */
    public URI resolveImportPrefix(final String prefix) throws URISyntaxException {
        for (final ImportStatement statement : getImportStatements()) {
            if (statement.getPrefix().equals(prefix)) {
                URI uri = statement.getUri();
                if (!uri.isAbsolute()) {
                    uri = this.resourceIdentifier.resolve(uri);
                }
                return uri;
            }
        }
        return null;
    }

    /**
     * Sucht innerhalb dieser Datei nach einem Template mit dem angegebenen Namen, welches den Typ des Modells
     * verarbeiten kann. Das erste passende Template innerhalb der Datei wird zurückgegeben.
     * 
     * @param templateName
     *            Name des zu suchenden Templates.
     * @param modelType
     *            Typ des Modells, dass das Template verarbeiten soll.
     * @return Template mit dem gewünschten Namen und passendem Modelltyp.
     */
    public TemplateStatement findTemplate(final String templateName, final Class<?> modelType) {
        for (final TemplateStatement templateStatement : getTemplateStatements()) {
            if (templateName.equals(templateStatement.getName())
                    && templateStatement.getTypeAsClass().isAssignableFrom(modelType)) {
                return templateStatement;
            }
        }
        return null;
    }

    /**
     * Gibt eine Liste aller in der Template-Datei-Definition enthaltenen Kommentare, Imports und Templates in der
     * Reihenfolge ihres Vorkommens zurück.
     * 
     * @return Liste aller in der Datei enthaltenen Imports, TemplateStatements und CommentStatements in der Reihenfolge
     *         ihres Vorkommens.
     */
    public List<AbstractTemplateMember<?>> getMembers() {
        return members;
    }
}