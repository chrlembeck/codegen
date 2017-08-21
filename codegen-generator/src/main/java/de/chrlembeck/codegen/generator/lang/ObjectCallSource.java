package de.chrlembeck.codegen.generator.lang;

/**
 * Referenz auf ein Objekt, dessen Attribut ausgelesen soll oder dessen Methode aufgerufen werden soll.
 *
 * @author Christoph Lembeck
 */
public class ObjectCallSource implements CallSource {

    /**
     * Enthaltenes Objekt mit dazugehörigem Typ für die Auswertung zur Template-Generator-Laufzeit.
     */
    private ObjectWithType<?> objectWithType;

    /**
     * Erzeugt ein neues Wrapper-Objekt mit dem übergebenen Objekt als Quelle.
     * 
     * @param objectRef
     *            Objekt, dessen Attribut ausgelesen soll oder dessen Methode aufgerufen werden soll.
     */
    public ObjectCallSource(final ObjectWithType<?> objectRef) {
        this.objectWithType = objectRef;
    }

    /**
     * Gibt das Objekt zurück, dass als Quelle für den Aufruf verwendet werden soll.
     * 
     * @return Objekt, dessen Attribut ausgelesen soll oder dessen Methode aufgerufen werden soll.
     */
    public Object getObjectRef() {
        return objectWithType.getObject();
    }
}