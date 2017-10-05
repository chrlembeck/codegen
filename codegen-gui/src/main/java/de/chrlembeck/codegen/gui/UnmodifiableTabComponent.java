package de.chrlembeck.codegen.gui;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

public class UnmodifiableTabComponent extends JScrollPane implements TabComponent {

    private static final long serialVersionUID = 3838149106330826649L;

    public UnmodifiableTabComponent(final JComponent component) {
        super(component);
    }

    @Override
    public boolean isNewArtifact() {
        return false;
    }

    @Override
    public boolean hasUnsavedModifications() {
        return false;
    }

    @Override
    public boolean saveDocument(final Path path, final Charset charset) throws IOException {
        return false;
    }

    @Override
    public Path getPath() {
        return null;
    }

    @Override
    public void addModificationListener(final ModificationListener listener) {
        // nothing to do
    }

    @Override
    public Charset getCharset() {
        return null;
    }
}