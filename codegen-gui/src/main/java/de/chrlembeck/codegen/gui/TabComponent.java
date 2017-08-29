package de.chrlembeck.codegen.gui;

/**
 * Interface für alle Reiterinhalte, die in der TabbedPane der Editoren angezeigt werden können.
 *
 * @author Christoph Lembeck
 */
public interface TabComponent {

    /**
     * Gibt an, ob das Dokument in dem Tab neu ist und noch nie gespeichert wurde.
     * 
     * @return true, falls das Dokument neu ist, sonst false
     */
    public boolean isNewArtifact();

    /**
     * Gibt an, ob das Dokument in dem Tab ungespeicherte Änderungen enthält.
     * 
     * @return true, falls das Dokument noch nicht gespeicherte Änderungen enthält, sonst false.
     */
    boolean hasUnsavedModifications();
}