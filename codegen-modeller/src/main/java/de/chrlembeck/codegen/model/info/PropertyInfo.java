package de.chrlembeck.codegen.model.info;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.JavaUtil;
import de.chrlembeck.util.lang.StringUtils;

public class PropertyInfo<ClassType, PropertyType> {

    /**
     * Der Logger für diese Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyInfo.class);

    static final String ADD_PREFIX = "add";

    static final String REMOVE_PREFIX = "remove";

    static final String GET_PREFIX = "get";

    static final String SET_PREFIX = "set";

    static final String IS_PREFIX = "is";

    private Function<ClassType, PropertyType> getter;

    private BiConsumer<ClassType, PropertyType> setter;

    private ElementGetter<ClassType, PropertyType> elementGetter;

    private ElementSetter<ClassType, PropertyType> elementSetter;

    private BiConsumer<ClassType, PropertyType> elementAdder;

    private BiConsumer<ClassType, PropertyType> elementRemover;

    private Class<ClassType> classRef;

    private String name;

    @SuppressWarnings("unchecked")
    public PropertyInfo(final Class<ClassType> classRef, final Field field) {
        final Class<PropertyType> propertyType = (Class<PropertyType>) field.getType();
        Class<?> memberType = Object.class;
        try {
            final Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType) genericType;
                final Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length == 1) {
                    memberType = (Class<?>) typeArguments[0];
                }
            }
        } catch (final TypeNotPresentException tnpe) {
        }

        this.classRef = classRef;
        this.name = field.getName();

        final Method readMethod = findGetter(classRef, name, propertyType);
        if (readMethod != null) {
            getter = new ReflectionGetter(readMethod);
        }
        final Method writeMethod = findSetter(classRef, name, propertyType);
        if (writeMethod != null) {
            setter = new ReflectionSetter(writeMethod);
        }
        final Method getMethod = findElementGetter(classRef, name, propertyType);
        if (getMethod != null) {
            elementGetter = new ReflectionElementGetter(getMethod);
        }
        final Method setMethod = findElementSetter(classRef, name, propertyType);
        if (setMethod != null) {
            elementSetter = new ReflectionElementSetter(setMethod);
        }
        final String colName = name.endsWith("s") ? name.substring(0, name.length() - 1) : name;
        final Method addMethod = findElementAdder(classRef, colName, memberType);
        if (addMethod != null) {
            elementAdder = new ReflectionElementAdder(addMethod);
        }
        final Method removeMethod = findElementRemover(classRef, colName, memberType);
        if (removeMethod != null) {
            elementRemover = new ReflectionElementRemover(removeMethod);
        }
    }

    public Function<ClassType, PropertyType> getGetter() {
        return getter;
    }

    public BiConsumer<ClassType, PropertyType> getSetter() {
        return setter;
    }

    public ElementGetter<ClassType, PropertyType> getElementGetter() {
        return elementGetter;
    }

    public ElementSetter<ClassType, PropertyType> getElementSetter() {
        return elementSetter;
    }

    public BiConsumer<ClassType, PropertyType> getElementAdder() {
        return elementAdder;
    }

    public BiConsumer<ClassType, PropertyType> getElementRemover() {
        return elementRemover;
    }

    public Class<ClassType> getClassRef() {
        return classRef;
    }

    class ReflectionGetter implements Function<ClassType, PropertyType> {

        private final Method readMethod;

        ReflectionGetter(final Method readMethod) {
            this.readMethod = Objects.requireNonNull(readMethod);
        }

        @Override
        public PropertyType apply(final ClassType object) {
            try {
                @SuppressWarnings("unchecked")
                final PropertyType invoke = (PropertyType) readMethod.invoke(object);
                return invoke;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                final String message = "Fehler beim Aufruf eines getters (" + readMethod.getName() + ").";
                LOGGER.error(message, e);
                throw new ModelManipulationException(message, e);
            }
        }
    }

    class ReflectionSetter implements BiConsumer<ClassType, PropertyType> {

        private final Method writeMethod;

        ReflectionSetter(final Method writeMethod) {
            this.writeMethod = Objects.requireNonNull(writeMethod);
        }

        @Override
        public void accept(final ClassType object, final PropertyType value) {
            try {
                writeMethod.invoke(object, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                final String message = "Fehler beim Aufruf eines setters (" + writeMethod.getName() + ").";
                LOGGER.error(message, e);
                throw new ModelManipulationException(message, e);
            }
        }
    }

    class ReflectionElementAdder implements BiConsumer<ClassType, PropertyType> {

        private final Method addMethod;

        ReflectionElementAdder(final Method addMethod) {
            this.addMethod = Objects.requireNonNull(addMethod);
        }

        @Override
        public void accept(final ClassType object, final PropertyType value) {
            try {
                addMethod.invoke(object, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                final String message = "Fehler beim Aufruf eines elementAdders (" + addMethod.getName() + ").";
                LOGGER.error(message, e);
                throw new ModelManipulationException(message, e);
            }
        }
    }

    class ReflectionElementGetter implements ElementGetter<ClassType, PropertyType> {

        private final Method getMethod;

        ReflectionElementGetter(final Method getMethod) {
            this.getMethod = Objects.requireNonNull(getMethod);
        }

        @Override
        public PropertyType getElement(final ClassType object, final int index) {
            try {
                @SuppressWarnings("unchecked")
                final PropertyType invoke = (PropertyType) getMethod.invoke(object, index);
                return invoke;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                final String message = "Fehler beim Aufruf eines elementGettrts (" + getMethod.getName() + ").";
                LOGGER.error(message, e);
                throw new ModelManipulationException(message, e);
            }
        }
    }

    class ReflectionElementSetter implements ElementSetter<ClassType, PropertyType> {

        private final Method setMethod;

        ReflectionElementSetter(final Method setMethod) {
            this.setMethod = Objects.requireNonNull(setMethod);
        }

        @Override
        public void setElement(final ClassType object, final int index, final PropertyType value) {
            try {
                setMethod.invoke(object, index, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                final String message = "Fehler beim Aufruf eines elementSetters (" + setMethod.getName() + ").";
                LOGGER.error(message, e);
                throw new ModelManipulationException(message, e);
            }
        }
    }

    class ReflectionElementRemover implements BiConsumer<ClassType, PropertyType> {

        private final Method removeMethod;

        ReflectionElementRemover(final Method removeMethod) {
            this.removeMethod = Objects.requireNonNull(removeMethod);
        }

        @Override
        public void accept(final ClassType object, final PropertyType value) {
            try {
                removeMethod.invoke(object, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                final String message = "Fehler beim Aufruf eines elementRemovers (" + removeMethod.getName() + ").";
                LOGGER.error(message, e);
                throw new ModelManipulationException(message, e);
            }
        }
    }

    public final Method findGetter(final Class<ClassType> classRef, final String name,
            final Class<PropertyType> propertyType) {
        for (final Method method : classRef.getMethods()) {
            if (method.getParameterTypes().length == 0
                    && propertyType.isAssignableFrom(method.getReturnType())
                    && ((method.getName().equals(GET_PREFIX + StringUtils.toFirstUpper(name)))
                            || (JavaUtil.isBooleanType(method.getReturnType())
                                    && method.getName().equals(IS_PREFIX + StringUtils.toFirstUpper(name))))) {
                return method;
            }
        }
        return null;
    }

    public final Method findSetter(final Class<ClassType> classRef, final String name,
            final Class<PropertyType> propertyType) {
        for (final Method method : classRef.getMethods()) {
            if (method.getParameterTypes().length == 1
                    && method.getParameterTypes()[0].isAssignableFrom(propertyType)
                    && method.getName().equals(SET_PREFIX + StringUtils.toFirstUpper(name))) {
                return method;
            }
        }
        return null;
    }

    public final Method findElementSetter(final Class<ClassType> classRef, final String name,
            final Class<PropertyType> propertyType) {
        for (final Method method : classRef.getMethods()) {
            if (method.getParameterTypes().length == 2
                    && method.getName().equals(SET_PREFIX + StringUtils.toFirstUpper(name))
                    && method.getParameterTypes()[1].isAssignableFrom(propertyType)
                    && JavaUtil.isIntegerType(method.getParameterTypes()[0])) {
                return method;
            }
        }
        return null;
    }

    public final Method findElementGetter(final Class<ClassType> classRef, final String name,
            final Class<PropertyType> propertyType) {
        for (final Method method : classRef.getMethods()) {
            if (method.getParameterTypes().length == 1
                    && method.getName().equals(GET_PREFIX + StringUtils.toFirstUpper(name))
                    && propertyType.isAssignableFrom(method.getReturnType())
                    && JavaUtil.isIntegerType(method.getParameterTypes()[0])) {
                return method;
            }
        }
        return null;
    }

    public final Method findElementAdder(final Class<ClassType> classRef, final String name,
            final Class<?> elementType) {
        for (final Method method : classRef.getMethods()) {
            if (method.getParameterTypes().length == 1
                    && method.getName().equals(ADD_PREFIX + StringUtils.toFirstUpper(name))
                    && method.getParameterTypes()[0].isAssignableFrom(elementType)) {
                return method;
            }
        }
        return null;
    }

    public final Method findElementRemover(final Class<ClassType> classRef, final String name,
            final Class<?> elementType) {
        for (final Method method : classRef.getMethods()) {
            if (method.getParameterTypes().length == 1
                    && method.getName().equals(REMOVE_PREFIX + StringUtils.toFirstUpper(name))
                    && method.getParameterTypes()[0].isAssignableFrom(elementType)) {
                return method;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "PropertyInfo[name=" + name + ", getter=" + getter + ", setter=" + setter + ", elementGetter="
                + elementGetter + ", elementSetter=" + elementSetter + ", elementAdder=" + elementAdder
                + ", elementRemover=" + elementRemover + "]";

    }
}