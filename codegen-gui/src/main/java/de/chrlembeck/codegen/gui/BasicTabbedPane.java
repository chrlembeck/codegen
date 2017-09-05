package de.chrlembeck.codegen.gui;

import java.awt.Component;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

/**
 * Erweiterung der JTabbedPane um Reiter mit einer Schaltfläche zum Schließen und der Möglichkeit, die Inhalte der
 * Reiter speichern zu können.
 * 
 * @author Christoph Lembeck
 */
public class BasicTabbedPane extends JTabbedPane {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -374367653947864618L;

    /**
     * Referenz auf den gerade ausgewählten Editor. Wird benötigt, um bei einem Wechsel der aktiven Editoren die
     * Referenz auf den jeweils vorher aktiven Editor an die Listener übermitteln zu können.
     */
    private Component selectedTabComponent;

    /**
     * Liste der Listener für die Information über die Zustandsänderungen der einzelnen Editorfenster.
     */
    private List<TabListener> tabListeners = new ArrayList<>();

    /**
     * Erzeugt einen neuen TabbedPane.
     * 
     * @param tabPlacement
     *            the placement for the tabs relative to the content
     * @param tabLayoutPolicy
     *            the policy for laying out tabs when all tabs will not fit on one run
     * @exception IllegalArgumentException
     *                if tab placement or tab layout policy are not one of the above supported values
     * @see JTabbedPane#JTabbedPane(int, int)
     */
    public BasicTabbedPane(final int tabPlacement, final int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
        addChangeListener(this::tabChanged);
    }

    /**
     * Wird aufgerufen, wenn ein anderer Tab aktiviert wird. Informiert danach die eingetragenen TabListener über dieses
     * Event.
     * 
     * @param event
     *            Event, welches den Wechsel zwischen zwei Tabs angezeigt hat.
     */
    protected void tabChanged(final ChangeEvent event) {
        final Component newSelection = getSelectedComponent();
        tabListeners.stream().forEach(listener -> listener.tabChanged(selectedTabComponent, newSelection));
        selectedTabComponent = newSelection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTabAt(final int index) {
        final TabComponent tabComponent = (TabComponent) getSelectedComponent();
        super.removeTabAt(index);
        tabListeners.stream().forEach(listener -> listener.tabClosed(tabComponent));
        if (getTabCount() == 0) {
            tabListeners.stream().forEach(listener -> listener.lastTabClosed(tabComponent));
        }
    }

    /**
     * Speichert das in dem aktuell aktiven Tab angezeigt Dokument auf die Festplatte.
     * 
     * @param path
     *            Pfad zu der Datei, in die das Dokument gespeichert werden soll.
     * @param charset
     *            Encoding zum Speichern der Datei.
     */
    public void saveDocument(final Path path, final Charset charset) {
        final Component comp = getSelectedComponent();
        if (comp instanceof TabComponent) {
            final TabComponent tabComponent = (TabComponent) comp;
            try {
                final boolean success = tabComponent.saveDocument(path, charset);
                if (success) {
                    final int idx = getSelectedIndex();
                    final TabHeader tabLabel = (TabHeader) getTabComponentAt(idx);
                    tabLabel.notifyDocumentSaved(path);
                    tabListeners.stream().forEach(listener -> listener.tabSaved(tabComponent));
                }
            } catch (final IOException ioe) {
                JOptionPane.showMessageDialog(this, ioe.getLocalizedMessage());
            }
        }
    }

    /**
     * Füg einen neuen TabListener in die Liste der Listener für die Information über die Zustandsänderungen der
     * einzelnen Editorfenster ein.
     * 
     * @param listener
     *            Hinzuzufügender Listener.
     */
    public void addTabListener(final TabListener listener) {
        tabListeners.add(listener);
    }

    /**
     * Entfernt einen TabListener aus die Liste der Listener für die Information über die Zustandsänderungen der
     * einzelnen Editorfenster.
     * 
     * @param listener
     *            Zu löschender Listener.
     */
    public void removeTabListener(final TabListener listener) {
        tabListeners.remove(listener);
    }

    /**
     * Wird einmalig aufgerufen, wenn nach dem Neuerstellen, Laden oder Speichern eines Dokumentes ungespeicherte
     * Änderungen an dem Dokument vorgenommen werden.
     * 
     * @param tabComponent
     *            Template, an dem de Änderungen vorgenommen wurden.
     */
    public void onTemplateWasModified(final TabComponent tabComponent) {
        final int index = indexOfComponent((Component) tabComponent);
        final TabHeader label = (TabHeader) getTabComponentAt(index);
        label.notifyTemplateWasModified();
        tabListeners.stream().forEach(listener -> listener.tabContentHasUnsavedModifications(tabComponent));
    }

    /**
     * Fügt einen neuen Template-Editor hinzu.
     * 
     * @param tabComponent
     *            Referenz auf den Editor, der hinzugefügt werden soll.
     * @param closeable
     *            {@code true} falls der neue Reiter einen Button zum Schließen erhalten soll, {@code false} falls der
     *            Reiter nicht geschlossen können werden soll.
     */
    public void addTabComponent(final TabComponent tabComponent, final String name, final boolean closeable) {
        final int newIndex = getTabCount();
        insertTab(null, null, (Component) tabComponent, null, newIndex);
        setSelectedIndex(newIndex);

        final TabHeader pnTab = new TabHeader(this, name, closeable);

        if (tabComponent.isNewArtifact()) {
            pnTab.setNew(true);
        } else {
            pnTab.notifyDocumentSaved(tabComponent.getPath());
        }
        setTabComponentAt(newIndex, pnTab);
        tabComponent.addModificationListener(this::onTemplateWasModified);
        if (getTabCount() == 1) {
            tabListeners.stream().forEach(listener -> listener.firstTabOpened(tabComponent));
        }
        tabListeners.stream().forEach(listener -> listener.tabOpened(tabComponent));
    }

    /**
     * Wird aufgerufen, wenn ein Benutzer den Button zum Schließen eines Tabs betätigt hat. Prüft, ob noch Änderungen
     * gespeichert werden müssen und schließt das Fenster.
     * 
     * @param idx
     *            Indexposition des Fensters, welches geschlossen werden soll.
     */
    protected void performCloseTabAction(final int idx) {
        final Component component = getComponentAt(idx);
        if (component instanceof TabComponent) {
            final TabComponent tabComponent = (TabComponent) component;
            if (tabComponent.hasUnsavedModifications()) {
                final String OPTION_CLOSE = "Änderungen verwerfen";
                final String OPTION_CANCEL = "Abbrechen";
                final int selection = JOptionPane.showOptionDialog(this,
                        "Das Dokument enthält noch ungespeicherte Änderungen.\nWollen Sie es wirklich schließen ohne zu speichern?",
                        "Nicht Speichern?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                        new String[] { OPTION_CLOSE, OPTION_CANCEL }, OPTION_CANCEL);
                if (selection == 0) {
                    removeTabAt(idx);
                }
            } else {
                removeTabAt(idx);
            }
        }
    }

    /**
     * Gibt den aktuell ausgewählten Tab-Inhalt zurück.
     * 
     * @return Aktuell ausgewählter Tab-Inhalt.
     */
    public TabComponent getSelectedDocument() {
        final Component comp = getSelectedComponent();
        if (comp instanceof TabComponent) {
            return (TabComponent) comp;
        } else {
            return null;
        }
    }

    /**
     * Gibt den Namen der Datei zurück, in oder aus dem das Dokument in dem gerade aktiven Editorfenster zuletzt
     * geschrieben oder gelesen wurde.
     * 
     * @return Name der Datei, aus der das aktive Dokument stammt.
     */
    public Path getSelectedDocumentPath() {
        return getSelectedDocument().getPath();
    }

    /**
     * Gibt das Encoding der Datei zurück, mit dem das Dokument in dem gerade aktiven Editorfenster zuletzt in eine
     * Datei geschrieben oder gelesen wurde.
     * 
     * @return Encoding der Datei, aus der das aktive Dokument stammt.
     */
    public Charset getSelectedDocumentCharset() {
        return getSelectedDocument().getCharset();
    }

    /**
     * Prüft, ob es noch einen Tab gibt, welcher noch nicht gespeicherte Änderungen enthält.
     * 
     * @return {@code true}, falls noch ein Tab geöffnet ist, in dem die Änderungen noch nicht gespeichert wurden,
     *         {@code false} falls alle Änderungen gespeichert sind.
     */
    public boolean containsUnsavedModifications() {
        for (int i = 0; i < getTabCount(); i++) {
            final Component component = getComponentAt(i);
            if (component instanceof TabComponent) {
                final TabComponent tabComponent = (TabComponent) component;
                if (tabComponent.hasUnsavedModifications()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Prüft, ob das gerade aktive Dokument noch neu ist oder bereits als Datei existiert.
     * 
     * @return {@code true} falls das Dokument neu ist und noch nie gepseichert wurde, {@code false} falls das Dokument
     *         aus einer Datei gelesen oder bereits einmal gespeichert wurde.
     */
    public boolean isSelectedDocumentNew() {
        final TabComponent document = getSelectedDocument();
        return document != null && document.isNewArtifact();
    }

    /**
     * Prüft, ob aktuell ein Dokument geöffnet ist.
     * 
     * @return {@code true} falls ein Tab ausgewählt ist, {@code false} falls kein Tab ausgewählt ist.
     */
    public boolean isDocumentSelected() {
        return getSelectedDocument() != null;
    }
}