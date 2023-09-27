package de.chrlembeck.codegen.model;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import de.chrlembeck.codegen.model.impl.*;

public class ModelRepository {

    public static void main(final String[] args) throws Exception {
        final Path dir = FileSystems.getDefault().getPath("D:", "temp", "codegen");
        final Path path = dir.resolve("database.smodel");
        final Object model = createTestModel();
        try (FileOutputStream fileOut = new FileOutputStream(path.toFile());
             ObjectOutputStream oOut = new ObjectOutputStream(fileOut)) {
            oOut.writeObject(model);
        }

    }

    public static Model createTestModel() {
        final Model model = new ModelImpl();
        final Catalog catalog = new CatalogImpl();
        model.addCatalog(catalog);
        catalog.setCatalogName("catalog");
        final Schema schema = new SchemaImpl();
        catalog.addSchema(schema);
        schema.setPackageName("de.test");
        schema.setSchemaName("schema");

        createKunde(schema);
        createAuftrag(schema);

        return model;
    }

    private static Table createAuftrag(final Schema schema) {
        final Table auftrag = new TableImpl();
        schema.addTable(auftrag);
        auftrag.setJavaName("Auftrag");
        final Column aId = new ColumnImpl();
        auftrag.addColumn(aId);
        aId.setJavaName("auftragId");
        aId.setJavaType("long");
        aId.setPrimaryKeyColumn(true);
        final Column aNo = new ColumnImpl();
        auftrag.addColumn(aNo);
        aNo.setJavaName("auftragsnummer");
        aNo.setJavaType("java.lang.String");
        return auftrag;
    }

    public static Table createKunde(final Schema schema) {
        final Table kunde = new TableImpl();
        schema.addTable(kunde);
        kunde.setJavaName("Kunde");
        final Column a1 = new ColumnImpl();
        kunde.addColumn(a1);
        a1.setJavaName("id");
        a1.setJavaType("int");
        a1.setPrimaryKeyColumn(Boolean.TRUE);
        final Column aName = new ColumnImpl();
        kunde.addColumn(aName);
        aName.setJavaName("name");
        aName.setJavaType("String");
        return kunde;
    }
}