package de.chrlembeck.codegen.generator.lang;

import java.net.URI;
import java.net.URISyntaxException;

import de.chrlembeck.codegen.grammar.CodeGenParser.ImportStatementContext;

/**
 * Über Import-Statements können einem Template die Templates-Definitionen aus anderen Template-Dateien zur Verfügung
 * gestellt werden. Jeder Import beinhaltet die Referenz auf eine andere Datei und einen Kurzbezeichner, über den die
 * Template-Dateien in Execute-Statements verwendet werden können.
 * 
 * @author Christoph Lembeck
 */
public class ImportStatement extends AbstractTemplateMember<ImportStatementContext> {

    /**
     * Ressource identifier, der den Alageort der referenzierten Template-Datei beschreibt.
     */
    private String uri;

    /**
     * Innerhalb der Execute-Statements verwendbarer Bezeichner des Imports, der auf die zu referenzierende
     * Template-Datei verweist.
     */
    private String prefix;

    /**
     * Erstellt das ForStatement mit den übergebenen Daten.
     * 
     * @param ctx
     *            ANTLR-Kontext, wie er vom Parser beim Lesen des Dokumentes erzeugt wurde.
     * @param prefix
     *            Innerhalb der Execute-Statements verwendbarer Bezeichner des Imports, der auf die zu referenzierende
     *            Template-Datei verweist.
     * @param uri
     *            Ressource identifier, der den Ablageort der referenzierten Template-Datei beschreibt.
     */
    public ImportStatement(final ImportStatementContext ctx, final String prefix, final String uri) {
        super(ctx);
        this.prefix = prefix;
        this.uri = uri;
    }

    /**
     * Gibt den Prefix des Imports zurück.
     * 
     * @return Innerhalb der Execute-Statements verwendbarer Bezeichner des Imports, der auf die zu referenzierende
     *         Template-Datei verweist.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gibt den resource identifier zur beschreibung der Template-Datei zurück.
     * 
     * @return Ressource identifier, der den Alageort der referenzierten Template-Datei beschreibt.
     * @throws URISyntaxException
     *             Falls die im Import hinterlegte URI ungültig ist.
     */
    public URI getUri() throws URISyntaxException {
        return new URI(uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "import " + prefix + " AS " + uri + ";";
    }
}