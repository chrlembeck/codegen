package de.chrlembeck.codegen.generator.lang;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.Generator;
import de.chrlembeck.codegen.generator.GeneratorException;
import de.chrlembeck.codegen.grammar.CodeGenParser.UserCodeContext;

/**
 * Enthält einen Code-Block, der die Zeichen enthält, die später in die zu generierenden Artefakte hinein geschrieben
 * werden sollen.
 * 
 * @author Christoph Lembeck
 */
public class UserCode extends AbstractTemplateMember<UserCodeContext> implements UserCodeOrStatements<UserCodeContext> {

    /**
     * Der Logger für die Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCode.class);

    /**
     * Text, der in die generierten Artefkate hineingeschrieben werden soll.
     */
    private String code;

    /**
     * Erstellt das Statement mit den übergebenen Daten.
     * 
     * @param ctx
     *            ANTLR-Kontext, wie er vom Parser beim Lesen des Dokumentes erzeugt wurde.
     * @param code
     *            Text, der beim Generieren in die Artefakte geschrieben werden soll.
     */
    public UserCode(final UserCodeContext ctx, final String code) {
        super(ctx);
        this.code = code;
    }

    /**
     * Gibt den Text zurück, der in die zu generierenden Artefakte geschrieben werden soll.
     * 
     * @return Text, der in die Generate kopiert werden soll.
     */
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "UserCode[" + code + ']';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Generator generator, final Object model, final Environment environment)
            throws IOException {
        final Writer currentWriter = generator.getCurrentWriter();
        if (currentWriter == null) {
            if (code != null && code.trim().length() > 0) {
                LOGGER.info("Kein Writer zur Ausgabe gefunden. " + this + ": " + this.getContext());
                throw new GeneratorException("Kein Writer zur Ausgabe gefunden.", this, environment);
            }
        } else {
            currentWriter.append(code);
        }
    }
}