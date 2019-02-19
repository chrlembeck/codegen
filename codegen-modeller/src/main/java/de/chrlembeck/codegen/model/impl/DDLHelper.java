package de.chrlembeck.codegen.model.impl;

import java.sql.Types;

import de.chrlembeck.codegen.model.Attribute;

public class DDLHelper {

    public static String getTypeDef(final Attribute attribute) {
        String type = attribute.getTypeName();
        final int dataType = attribute.getDataType();
        switch (dataType) {
            case Types.NUMERIC:
            case Types.DECIMAL:
                if (attribute.getColumnSize() != 0) {
                    type = type + "(" + attribute.getColumnSize() + "," + attribute.getDecimalDigits() + ")";
                }
                break;
            case Types.CHAR:
            case Types.VARCHAR:
                type = type + "(" + attribute.getCharOctetLength() + ")";
        }

        return type;
    }
}
