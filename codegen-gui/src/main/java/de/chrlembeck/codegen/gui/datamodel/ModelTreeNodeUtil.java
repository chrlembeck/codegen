package de.chrlembeck.codegen.gui.datamodel;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.treetable.TreeTableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hilfsklasse zur Erzeugung von TreeNodes für die Anzeige des Datenmodells.
 *
 * @author Christoph Lembeck
 */
public class ModelTreeNodeUtil {

    /**
     * Leere Child-Liste für die Verwendung in den Blatt-Knoten.
     */
    private static final TreeTableNode[] LEAF_NODE = new TreeTableNode[0];

    /**
     * Liste von Klassen, deren Child-Elemente im Modell nicht angezeigt werden sollen.
     */
    private static Set<Class<?>> leafClasses = new HashSet<>();

    /**
     * Liste der Prüfungen, die nicht anzuzeigende Felder erkennen.
     */
    private static List<Predicate<Field>> blockedFields = new ArrayList<>(1);

    /**
     * Der Logger für diese Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelTreeNodeUtil.class);

    /**
     * Initialisiert die Liste der Klassen, deren Elementen nicht im Modell angezeigt werden sollen und die Liste der
     * Felder, die durch Filter geblockt werden.
     */
    static {
        leafClasses.add(Boolean.class);
        leafClasses.add(Byte.class);
        leafClasses.add(Character.class);
        leafClasses.add(Short.class);
        leafClasses.add(Integer.class);
        leafClasses.add(Float.class);
        leafClasses.add(Long.class);
        leafClasses.add(Double.class);
        leafClasses.add(String.class);
        leafClasses.add(BigDecimal.class);
        leafClasses.add(BigInteger.class);
        blockedFields.add(field -> long.class == field.getType() && Modifier.isFinal(field.getModifiers())
                && "serialVersionUID".equals(field.getName()));
        blockedFields.add(
                field -> field.getDeclaringClass() == DefaultMutableTreeNode.class
                        && "EMPTY_ENUMERATION".equals(field.getName()));
    }

    public boolean allowsChildren(final Object userObject) {
        return true;
    }

    private void collectChildrenForArray(final ModelTreeTableNode node, final Object userObject,
            final List<TreeTableNode> children,
            final Class<? extends Object> objectType) {
        for (int i = 0; i < Array.getLength(userObject); i++) {
            final Object arrayMember = Array.get(userObject, i);
            children.add(new ModelTreeTableNode(node, "[" + i + ']', objectType.getComponentType(), arrayMember));
        }
    }

    private void collectChildrenForList(final ModelTreeTableNode node, final List<?> userObject,
            final List<TreeTableNode> children) {
        for (int i = 0; i < userObject.size(); i++) {
            final Object listItem = userObject.get(i);
            children.add(new ModelTreeTableNode(node, "[" + i + ']', listItem.getClass(), listItem));
        }
    }

    private void collectChildrenForObject(final ModelTreeTableNode node, final Object userObject,
            final List<TreeTableNode> children,
            final Class<? extends Object> objectType) {
        Class<? extends Object> classRef = objectType;
        while (classRef != null) {
            nextField: for (final Field field : classRef.getDeclaredFields()) {
                if (field.isSynthetic()) {
                    continue nextField;
                }
                for (final Predicate<Field> isBlocked : blockedFields) {
                    if (isBlocked.test(field)) {
                        continue nextField;
                    }
                }

                try {
                    field.setAccessible(true);
                    final ModelTreeTableNode child = new ModelTreeTableNode(node, field.getName(), field.getType(),
                            field.get(userObject));
                    children.add(child);
                } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            classRef = classRef.getSuperclass();
        }

    }

    public TreeTableNode createRootNode(final String name, final Object userObject) {
        final ModelTreeTableNode rootNode = new ModelTreeTableNode(null, name, userObject.getClass(), userObject);
        rootNode.setHelper(this);
        return rootNode;
    }

    public TreeTableNode[] getChildren(final ModelTreeTableNode node) {
        final Object userObject = node.getUserObject();
        if (userObject == null) {
            return LEAF_NODE;
        } else {
            final List<TreeTableNode> children = new ArrayList<>();

            // Der wirkliche Typ des Elements
            final Class<? extends Object> realType = userObject.getClass();
            // Der Typ der Variable
            final Class<?> fieldType = node.getType();
            if (fieldType.isPrimitive()) {
                return LEAF_NODE;
            } else if (realType.isArray()) {
                collectChildrenForArray(node, userObject, children, realType);
            } else if (leafClasses.contains(realType)) {
                // do nothing
            } else if (List.class.isAssignableFrom(realType)) {
                collectChildrenForList(node, (List<?>) userObject, children);
            } else {
                collectChildrenForObject(node, userObject, children, realType);
            }
            return children.toArray(new TreeTableNode[children.size()]);
        }
    }

    private String getVisibleTypeName(final Class<?> type) {
        if (type.isArray()) {
            return getVisibleTypeName(type.getComponentType()) + "[]";
        }
        return type.getName();
    }

    public String getVisibleTypeName(final Class<?> fieldType, final Class<?> realType) {
        if (realType == null || fieldType.isPrimitive()) {
            return getVisibleTypeName(fieldType);
        }
        return getVisibleTypeName(realType);
    }

    public String toVisibleString(final Object userObject) {
        if (userObject == null) {
            return "null";
        }
        if (userObject.getClass().isArray()) {
            if (boolean[].class.equals(userObject.getClass())) {
                return Arrays.toString((boolean[]) userObject);
            } else if (byte[].class.equals(userObject.getClass())) {
                return Arrays.toString((byte[]) userObject);
            } else if (char[].class.equals(userObject.getClass())) {
                return Arrays.toString((char[]) userObject);
            } else if (short[].class.equals(userObject.getClass())) {
                return Arrays.toString((short[]) userObject);
            } else if (int[].class.equals(userObject.getClass())) {
                return Arrays.toString((int[]) userObject);
            } else if (float[].class.equals(userObject.getClass())) {
                return Arrays.toString((float[]) userObject);
            } else if (long[].class.equals(userObject.getClass())) {
                return Arrays.toString((long[]) userObject);
            } else if (double[].class.equals(userObject.getClass())) {
                return Arrays.toString((double[]) userObject);
            }

            return Arrays.deepToString((Object[]) userObject);
        }
        return String.valueOf(userObject);
    }
}