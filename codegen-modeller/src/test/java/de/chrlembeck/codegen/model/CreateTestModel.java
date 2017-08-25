package de.chrlembeck.codegen.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Erzeugt eine neue Testdatei mit einer serialisierten Liste von Strings.
 * 
 * @author Christoph Lembeck
 */
public class CreateTestModel {

    /**
     * Erzeugt eine neue Testdatei mit einer serialisierten Liste von Strings.
     * 
     * @param args
     *            Wird nicht verwendet.
     * @throws IOException
     *             Falls die Datei nicht geschrieben werden kann.
     */
    public static void main(final String[] args) throws IOException {
        final Path dir = FileSystems.getDefault().getPath("D:", "eclipse", "codegen", "codegen-gui", "src", "test",
                "resources");
        final Path path = dir.resolve("listOfStrings.smodel");
        final List<String> list = Arrays.asList(new String[] { "One", "Two", "Three" });
        try (FileOutputStream fileOut = new FileOutputStream(path.toFile());
                ObjectOutputStream oOut = new ObjectOutputStream(fileOut)) {
            oOut.writeObject(list);
        }
    }
}