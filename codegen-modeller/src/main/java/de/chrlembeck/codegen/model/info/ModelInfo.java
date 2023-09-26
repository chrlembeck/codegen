package de.chrlembeck.codegen.model.info;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelInfo<C> {

    /**
     * Der Logger für diese Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelInfo.class);

    public List<PropertyInfo<C, ?>> propertyInfos = new ArrayList<>();

    public ModelInfo(final Class<C> classRef) {
        for (final Field field : classRef.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            final PropertyInfo<C, ?> info = new PropertyInfo<>(classRef, field);
            addPropertyInfo(info);
        }
    }

    public Collection<PropertyInfo<C, ?>> getPropertyInfos() {
        return propertyInfos;
    }

    public final void addPropertyInfo(final PropertyInfo<C, ?> info) {
        LOGGER.debug("addPropertyInfo(" + info + ")");
        propertyInfos.add(info);
    }
}