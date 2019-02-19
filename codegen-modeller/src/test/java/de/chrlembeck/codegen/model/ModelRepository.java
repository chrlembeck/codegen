package de.chrlembeck.codegen.model;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import de.chrlembeck.codegen.model.impl.DBRootTreeNode;

public class ModelRepository {

    public static Model createTestModel() {
        final Model model = new DBRootTreeNode();
        final Catalog catalog = model.createCatalog();
        catalog.setCatalogName("catalog");
        final Schema schema = catalog.createSchema();
        schema.setPackageName("de.test");
        schema.setSchemaName("schema");

        createKunde(schema);
        createAuftrag(schema);

        return model;
    }

    private static Entity createAuftrag(final Schema schema) {
        final Entity auftrag = schema.createEntity();
        auftrag.setJavaName("Auftrag");
        final Attribute aId = auftrag.createAttribute();
        aId.setJavaName("auftragId");
        aId.setJavaType("long");
        aId.setPrimaryKeyColumn(true);
        final Attribute aNo = auftrag.createAttribute();
        aNo.setJavaName("auftragsnummer");
        aNo.setJavaType("java.lang.String");
        return auftrag;
    }

    public static Entity createKunde(final Schema schema) {
        final Entity kunde = schema.createEntity();
        kunde.setJavaName("Kunde");
        final Attribute a1 = kunde.createAttribute();
        a1.setJavaName("id");
        a1.setJavaType("int");
        a1.setPrimaryKeyColumn(Boolean.TRUE);
        final Attribute aName = kunde.createAttribute();
        aName.setJavaName("name");
        aName.setJavaType("String");
        return kunde;
    }

    public static void main(final String[] args) throws Exception {
        final Path dir = FileSystems.getDefault().getPath("D:", "eclipse", "codegen", "codegen-gui", "src", "test",
                "resources");
        final Path path = dir.resolve("database.smodel");
        final Object model = createTestModel();
        try (FileOutputStream fileOut = new FileOutputStream(path.toFile());
                ObjectOutputStream oOut = new ObjectOutputStream(fileOut)) {
            oOut.writeObject(model);
        }

    }
}