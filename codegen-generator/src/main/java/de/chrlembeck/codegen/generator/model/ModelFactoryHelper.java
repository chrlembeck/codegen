package de.chrlembeck.codegen.generator.model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.chrlembeck.util.io.ClassLoaderObjectInputStream;

/**
 * Hilfklasse zum Einlesen von Modellen aus verschiedenen Datenquellen.
 * 
 * @author Christoph Lembeck
 */
@SuppressWarnings("PMD.UseUtilityClass")
public class ModelFactoryHelper {

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
        return byMethodCall(className, methodName, null);
    }

    /**
     * Erzeugt ein Modell über das Ergebnis eines Methodenaufrufs unter Verwendung eines speziellen Classloaders.
     * 
     * @param className
     *            Name der Klasse, deren Methode das Modell erzeugt.
     * @param methodName
     *            Methode, die aufgerufen werden soll, um das Modell zu erzeugen. Statische Methoden können direkt
     *            aufgerufen werden, zum Aufruf von Instanzmethoden wird angenommen, dass die Klasse einen öffentlichen
     *            Default-Konstruktor besitzt.
     * @param classLoader
     *            Zum Laden der Modellklassen zu verwendender Classloader.
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
    public static Object byMethodCall(final String className, final String methodName, final ClassLoader classLoader)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException, InstantiationException {
        final Class<?> creatorClass = classLoader == null ? Class.forName(className)
                : Class.forName(className, true, classLoader);
        final Method creatorMethod = creatorClass.getMethod(methodName);
        Object modell;
        if (Modifier.isStatic(creatorMethod.getModifiers())) {
            modell = creatorMethod.invoke(null);
        } else {
            modell = creatorMethod.invoke(creatorClass.getDeclaredConstructor().newInstance());
        }
        return modell;

    }

    public static Object byDeserialization(final InputStream inputStream)
            throws ClassNotFoundException, IOException {
        return byDeserialization(inputStream, null);
    }

    public static Object byDeserialization(final InputStream inputStream, final ClassLoader classLoader)
            throws ClassNotFoundException, IOException {
        try (BufferedInputStream bufIn = new BufferedInputStream(inputStream);
                ObjectInputStream objectIn = classLoader == null ? new ObjectInputStream(bufIn)
                        : new ClassLoaderObjectInputStream(bufIn, classLoader)) {
            final Object model = objectIn.readObject();
            return model;
        }
    }
}