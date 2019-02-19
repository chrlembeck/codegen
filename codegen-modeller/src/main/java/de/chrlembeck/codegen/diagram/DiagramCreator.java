package de.chrlembeck.codegen.diagram;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.chrlembeck.codegen.model.Attribute;
import de.chrlembeck.codegen.model.Catalog;
import de.chrlembeck.codegen.model.Entity;
import de.chrlembeck.codegen.model.Model;
import de.chrlembeck.codegen.model.Reference;
import de.chrlembeck.codegen.model.ReferenceMapping;
import de.chrlembeck.codegen.model.Schema;
import de.chrlembeck.codegen.model.impl.DDLHelper;

public class DiagramCreator {

    private Map<Entity, String> tableNameMap = new HashMap<>();

    private Map<Attribute, String> columnNameMap = new HashMap<>();

    public void createGraphvizDiagram(final Model model, final Writer writer) throws IOException {
        writer.append("digraph structs {\n");
        writer.append("  splines=\"spline\";\n\n");
        for (final Catalog catalog : model.getCatalogs()) {
            for (final Schema schema : catalog.getSchemas()) {
                for (final Entity entity : schema.getEntities()) {
                    appendEntity(entity, writer);
                }
            }
        }

        for (final Catalog catalog : model.getCatalogs()) {
            for (final Schema schema : catalog.getSchemas()) {
                for (final Entity entity : schema.getEntities()) {
                    for (final Reference reference : entity.getReferences()) {
                        appendReference(reference, writer);
                    }
                }
            }
        }
        writer.append("}");
    }

    private void appendReference(final Reference reference, final Writer writer) throws IOException {
        final List<ReferenceMapping> mappings = reference.getReferenceMappings();
        final ReferenceMapping firstMapping = mappings.get(0);
        writer.append("  " + getNameForEntity(reference.getForeignKeyTable()) + ":"
                + getPortName(firstMapping.getForeignKeyColumn()));
        writer.append(" -> " + getNameForEntity(reference.getPrimaryKeyTable()) + ":"
                + getPortName(firstMapping.getPrimaryKeyColumn()));
        if (mappings.size() > 1) {
            writer.append(" [fontname=Calibri, fontsize=8, label=\"");
            for (int i = 0; i < mappings.size(); i++) {
                final ReferenceMapping mapping = mappings.get(i);
                writer.append(mapping.getPrimaryKeyColumn().getColumnName());
                if (i < mappings.size() - 1) {
                    writer.append(",");
                }
            }
            writer.append("\"]");
        }

        writer.append(";\n");

    }

    private void appendEntity(final Entity entity, final Writer writer) throws IOException {
        writer.append("\n  " + getNameForEntity(entity) + " [\n");
        writer.append("        shape=plaintext,\n");
        writer.append("        fontname=Calibri,\n");
        writer.append("        fontsize=9,\n");
        writer.append("        label=< <TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"0\">\n");
        writer.append("                  <TR><TD BGCOLOR=\"#C0C0C0\" COLSPAN=\"2\">" + entity.getTableName()
                + "</TD></TR>\n");
        final int attributeCount = entity.getAttributeCount();
        int attributeIdx = 0;
        for (final Attribute attribute : entity.getAttributes()) {
            writer.append("                  <TR>");

            writer.append("<TD PORT=\"" + getPortName(attribute) + "\" ");
            writer.append("BGCOLOR=\""
                    + (attribute.isPrimaryKeyColumn() != null && attribute.isPrimaryKeyColumn().booleanValue()
                            ? "#E0E0E0"
                            : "#FFFFFF")
                    + "\" ");
            writer.append("SIDES=\"" + leftSides(attributeCount, attributeIdx) + "\" ");
            writer.append(" ALIGN=\"LEFT\">" + attribute.getColumnName() + "</TD>");

            writer.append("<TD ");
            writer.append("BGCOLOR=\""
                    + (attribute.isPrimaryKeyColumn() != null && attribute.isPrimaryKeyColumn().booleanValue()
                            ? "#E0E0E0"
                            : "#FFFFFF")
                    + "\" ");
            writer.append("SIDES=\"" + rightSides(attributeCount, attributeIdx) + "\" ");
            writer.append(" ALIGN=\"LEFT\">" + DDLHelper.getTypeDef(attribute) + "</TD>");

            writer.append("</TR>\n");

            // <TR><TD PORT="r1" SIDES="LT" ALIGN="LEFT" BGCOLOR="#E0E0E0">kundeID</TD><TD SIDES="RT" ALIGN="LEFT"
            // BGCOLOR="#E0E0E0">int not null</TD></TR>
            // <TR><TD PORT="r1" SIDES="L" ALIGN="LEFT">name</TD><TD SIDES="R" ALIGN="LEFT">varchar(32) not
            // null</TD></TR>
            // <TR><TD PORT="r2" SIDES="LB" ALIGN="LEFT">vorname</TD><TD SIDES="BR" ALIGN="LEFT">varchar(32) not
            // null</TD></TR>
            attributeIdx++;
        }

        writer.append("                </TABLE>>\n");
        writer.append("  ];\n");
    }

    private String leftSides(final int attributeCount, final int attributeIdx) {
        final String sides = "L" + (attributeIdx == 0 ? "T" : "") + (attributeIdx == attributeCount - 1 ? "B" : "");
        return sides;
    }

    private String rightSides(final int attributeCount, final int attributeIdx) {
        final String sides = (attributeIdx == 0 ? "T" : "") + (attributeIdx == attributeCount - 1 ? "B" : "") + "R";
        return sides;
    }

    private String getPortName(final Attribute attribute) {
        String name = columnNameMap.get(attribute);
        if (name == null) {
            name = "col" + columnNameMap.size();
            columnNameMap.put(attribute, name);
        }
        return name;
    }

    public String getNameForEntity(final Entity entity) {
        String name = tableNameMap.get(entity);
        if (name == null) {
            name = "tab" + tableNameMap.size();
            tableNameMap.put(entity, name);
        }
        return name;
    }
}
