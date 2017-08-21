package de.chrlembeck.codegen.generator.lang;

/**
 * Wrapper-Objekt für die Klasse, deren statisches Attribut ausgelesen oder deren statische Methode bei der Ausführung
 * eines Templates aufgerufen werden soll.
 * 
 * @author Christoph Lembeck
 *
 */
public class StaticCallSource implements CallSource {

    /**
     * Referenz auf die Klasse, deren statisches Attribut ausgelesen oder deren statische Methode aufgerufen werden
     * soll.
     */
    private Class<?> classRef;

    /**
     * Erzeugt ein neues Wrapper-Objekt für die übergebene Klasse.
     * 
     * @param classRef
     *            Klasse, die als CallSource verwendet werden soll.
     */
    public StaticCallSource(final Class<?> classRef) {
        this.classRef = classRef;
    }

    /**
     * Gibt die in dem Wrapper enthaltene Klasse zurück.
     * 
     * @return Klasse, deren statisches Attribut ausgelesen oder deren statische Methode aufgerufen werden soll.
     */
    public Class<?> getClassRef() {
        return classRef;
    }
}