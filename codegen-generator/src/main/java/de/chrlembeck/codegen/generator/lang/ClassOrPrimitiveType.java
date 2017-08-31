package de.chrlembeck.codegen.generator.lang;

import javax.lang.model.type.PrimitiveType;

/**
 * Entspricht einem voll qualifiziertem Klassen- oder Interfacenamen.
 *
 * @author Christoph Lembeck
 * @see PrimitiveType
 * @see ClassOrInterfaceType
 */
public interface ClassOrPrimitiveType {

    /**
     * Gibt die Referenz auf den Dargestellten Typen zurück. Dieser wird versucht anhand des Namens zu laden. Kann er
     * nicht gefunden werden, gibt es eine Exception.
     * 
     * @return Referenz auf den dargestellten Typ.
     * @throws ClassNotFoundException
     *             Falls die gewünschte Klasse nicht geladen werden kann.
     */
    Class<?> getClassRef() throws ClassNotFoundException;

    /**
     * Gibt den Klassennamen zurück, so wie er in der Template-Datei steht.
     * 
     * @return Klassenname aus der Template-Datei.
     */
    String getClassName();
}