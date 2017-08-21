package de.chrlembeck.codegen.generator.lang;

/**
 * Wrapper-Klasse für Objekte, die bei der Ausführung eines Ausdrucks eines Templates im Generator verarbeitet werden.
 * Neben den eigentlichen Werten, die verarbeitet werden, wird zusätzlich noch der Typ des Wertes gespeichert. Diese
 * Trennung ist notwendig, da sonst keine Unterscheidung zwischen den primitiven Java-Datentypen und ihren
 * Wrapper-Objekten vorgenommen werden kann.
 *
 * @author Christoph Lembeck
 */
public class ObjectWithType<T> {

    /**
     * Eigentlich zu verarbeitender Wert oder zu verarbeitendes Objekt.
     */
    private T object;

    /**
     * Laufzeittyp des enthaltenen Objekts (kann auch ein primitiver Typ sein).
     */
    private Class<T> type;

    /**
     * Legt einen neuen Wrapper für das Objekt mit dem entsprechenden Typ an.
     * 
     * @param object
     *            Enthaltener Wert für die weiteren Berechnungen
     * @param type
     *            Aktueller Typ des Objekts.
     */
    public ObjectWithType(final T object, final Class<T> type) {
        this.object = object;
        this.type = type;
    }

    /**
     * Gibt des Typ des enthaltenen Objekts zurück. Dabei kann es sich auch um einen Primitiven Typ handeln, der hier in
     * sein Wrapper-Objekt verpackt ist.
     * 
     * @return Typ des enthaltenen Objekts.
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Gibt das eigentlich zu verarbeitende Objekt zurück
     * 
     * @return Im Wrapper enthaltenes Objekt für die weitere Verarbeitung.
     */
    public T getObject() {
        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ObjectWithType[object=" + object + ", type=" + type + "]";
    }
}