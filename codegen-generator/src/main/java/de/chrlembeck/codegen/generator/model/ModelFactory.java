package de.chrlembeck.codegen.generator.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Hilfklasse zum Einlesen von Modellen aus verschiedenen Datenquellen.
 * 
 * @author Christoph Lembeck
 */
@SuppressWarnings("PMD.UseUtilityClass")
public class ModelFactory {

    /**
     * Erzeugt ein Modell über das Ergebnis eines Methodenaufrufs.
     * 
     * @param className
     *            Name der Klasse, deren Methode das Modell erzeugt.
     * @param methodName
     *            Methode, die aufgerufen werden soll, um das Modell zu erzeugen. Statische Methoden können direkt
     *            aufgerufen werden, zum Aufruf von Instanzmethoden wird angenommen, dass die Klasse einen öffentlichen
     *            Default-Konstruktor besitzt.
     * @return Modell als Ergebnis des Methodenaufrufs.
     * @throws ClassNotFoundException
     *             Falls die angegebene Klasse vom Classloader dieser Klasse nicht gefunden werden kann.
     * @throws IllegalAccessException
     *             Falls die Methode oder der Default-Konstruktor nicht aufgerufen werden kann.
     * @throws InvocationTargetException
     *             Wird innerhalb der aufgerufenen Methode eine Exception erzeugt, wird diese in einer
     *             InvocationTargetException weiter gereicht.
     * @throws NoSuchMethodException
     *             Wird die Methode in der Klasse nicht gefunden oder ist diese nicht frei von Argumenten, führt dies zu
     *             einer NoSuchMethodException.
     * @throws SecurityException
     *             Falls ein SecurityManager die Ausführung der Methode oder die Instantiierung der Klasse verhindert.
     * @throws InstantiationException
     *             Falls die Methode eine Instanzmethode ist und es bei der Instantiierung der Klasse ein Problem
     *             gegeben hat.
     */
    public static Object byMethodCall(final String className, final String methodName)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException, InstantiationException {
        final Class<?> creatorClass = Class.forName(className);
        final Method creatorMethod = creatorClass.getMethod(methodName);
        Object modell;
        if (Modifier.isStatic(creatorMethod.getModifiers())) {
            modell = creatorMethod.invoke(null);
        } else {
            modell = creatorMethod.invoke(creatorClass.newInstance());
        }
        return modell;
    }
}