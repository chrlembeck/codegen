package de.chrlembeck.codegen.gui;

import java.awt.Component;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import org.antlr.v4.runtime.Token;

import de.chrlembeck.antlr.editor.ErrorListener;

/**
 * TabbedPane, welche die Fenster der Editoren für die Template-Dateien verwaltet und anzeigt.
 *
 * @author Christoph Lembeck
 */
public class EditorTabs extends JTabbedPane implements CaretPositionChangeListener {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -7702736682576253059L;

    /**
     * Liste der Listener, die über neue Cursor-Positionen des gerade aktiven Dokuments informiert werden sollen.
     */
    private List<CaretPositionChangeListener> caretPositionChangeListeners = new ArrayList<>();

    /**
     * Liste der Listener für die Information über die Zustandsänderungen der einzelnen Editorfenster.
     */
    private List<TabListener> tabListeners = new ArrayList<>();

    /**
     * Liste der Listener, die über Änderungen an den im gerade aktiven Dokument enthaltenen Fehlern informiert werden
     * wollen.
     */
    private List<ErrorListener> errorListeners = new ArrayList<>();

    /**
     * Referenz auf den gerade ausgewählten Editor. Wird benötigt, um bei einem Wechsel der aktiven Editoren die
     * Referenz auf den jeweils vorher aktiven Editor an die Listener übermitteln zu können.
     */
    private TabComponent selectedTabComponent;

    /**
     * Erstellt das Objekt und initialisiert es.
     */
    public EditorTabs() {
        super(TOP, WRAP_TAB_LAYOUT);
        addChangeListener(this::tabChanged);
    }

    /**
     * Öffnet einen neuen, leeren Template-Editor.
     */
    public void newTemplate() {
        final TemplatePanel templatePanel = new TemplatePanel(null, Charset.forName("UTF-8"));
        addTemplatePanel(templatePanel);
        templatePanel.getEditorPane().addErrorListener(this::errorsChanged);
    }

    /**
     * Fügt einen neuen Template-Editor hinzu.
     * 
     * @param templatePanel
     *            Referenz auf den Editor, der hinzugefügt werden soll.
     */
    private void addTemplatePanel(final TemplatePanel templatePanel) {
        final int newIndex = getTabCount();
        insertTab(null, null, templatePanel, null, newIndex);
        setSelectedIndex(newIndex);

        final TabHeader pnTab = new TabHeader(this);

        if (templatePanel.isNewArtifact()) {
            pnTab.setNew(true);
        } else {
            pnTab.notifyTemplateSaved(templatePanel.getPath());
        }
        setTabComponentAt(newIndex, pnTab);
        templatePanel.getEditorPane().addModificationListener(this::onTemplateWasModified);
        if (getTabCount() == 1) {
            tabListeners.stream().forEach(l -> l.firstTabOpened(templatePanel));
        }
        tabListeners.stream().forEach(l -> l.tabOpened(templatePanel));
    }

    /**
     * Wird einmalig aufgerufen, wenn nach dem Neuerstellen, Laden oder Speichern eines Dokumentes ungespeicherte
     * Änderungen an dem Dokument vorgenommen werden.
     * 
     * @param templatePanel
     *            Template, an dem de Änderungen vorgenommen wurden.
     */
    public void onTemplateWasModified(final TemplatePanel templatePanel) {
        final int index = indexOfComponent(templatePanel);
        final TabHeader label = (TabHeader) getTabComponentAt(index);
        label.notifyTemplateWasModified();
        tabListeners.stream().forEach(l -> l.tabContentHasUnsavedModifications((TabComponent) getComponentAt(index)));
    }

    /**
     * Wird aufgerufen, wenn ein anderes Editorfenster aktiviert wird. Informiert danach die eingetragenen TabListener
     * über dieses Event.
     * 
     * @param event
     *            Event, welches den Wechsel zwischen zwei Editorfenstern angezeigt hat.
     */
    public void tabChanged(final ChangeEvent event) {
        resetCaretListeners();
        final TabComponent newSelection = (TabComponent) getSelectedComponent();
        tabListeners.stream().forEach(l -> l.tabChanged(selectedTabComponent, newSelection));
        selectedTabComponent = newSelection;
    }

    /**
     * Entfernt den Listener für die Cursor-Position von allen Editorfenstern und setzt einen neuen Listener auf genau
     * das gerade aktive Fenster. Danach werden alle eingetragenen CaretPositionChangeListener über den Wechsel der
     * Cursor-Position informiert.
     */
    private void resetCaretListeners() {
        for (int i = 0; i < getTabCount(); i++) {
            final Component comp = getComponentAt(i);
            if (comp instanceof TemplatePanel) {
                final TemplatePanel tp = (TemplatePanel) comp;
                tp.getEditorPane().removeCaretPositionChangeListener(this);
                if (i == getSelectedIndex()) {
                    tp.getEditorPane().addCaretPositionChangeListener(this);
                    tp.getEditorPane().fireStatusEvent();
                }
            }
        }
    }

    /**
     * Wird aufgerufen, wenn ein Benutzer den Button zum Schließen eines Editorfensters betätigt hat. Prüft, ob noch
     * Änderungen gespeichert werden müssen und schließt das Fenster.
     * 
     * @param idx
     *            Indexposition des Fensters, welches geschlossen werden soll.
     */
    protected void performCloseTabAction(final int idx) {
        final Component component = getComponentAt(idx);
        if (component instanceof TemplatePanel) {
            final TemplatePanel tp = (TemplatePanel) component;
            if (tp.hasUnsavedModifications()) {
                final String OPTION_CLOSE = "Änderungen verwerfen";
                final String OPTION_CANCEL = "Abbrechen";
                final int selection = JOptionPane.showOptionDialog(this,
                        "Das Template enthält noch ungespeicherte Änderungen.\nWollen Sie das Template wirklich schließen ohne zu speichern?",
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
     * {@inheritDoc}
     */
    @Override
    public void removeTabAt(final int index) {
        final TabComponent tabComponent = (TabComponent) getSelectedComponent();
        super.removeTabAt(index);
        tabListeners.stream().forEach(l -> l.tabClosed(tabComponent));
        if (getTabCount() == 0) {
            tabListeners.stream().forEach(l -> l.lastTabClosed(tabComponent));
        }
    }

    /**
     * {@inheritDoc} Informiert alle eingetragenen CaretPositionChangeListener über die neue Cursorposition.
     */
    @Override
    public void caretPositionChanged(final CaretPositionChangeEvent event) {
        for (final CaretPositionChangeListener editorStatusListener : caretPositionChangeListeners) {
            editorStatusListener.caretPositionChanged(event);
        }
    }

    /**
     * Fügt den übergebenen Listener zu der Liste der Listener hinzu, die über neue Cursor-Positionen im gerade aktiven
     * Dokument informiert werden sollen.
     * 
     * @param listener
     *            Neuer Listener für Cursor-Benachristigungen.
     */
    public void addCaretPositionChangeListener(final CaretPositionChangeListener listener) {
        caretPositionChangeListeners.add(listener);
    }

    /**
     * Entfernt den übergebenen Listener von der Liste der Listener, die über Cursorbewegungen im gerade aktiven
     * Dokument informiert werden.
     * 
     * @param listener
     *            Listener, der von der Liste gestrichen werden soll.
     */
    public void removeCaretPositionChangeListener(final CaretPositionChangeListener listener) {
        caretPositionChangeListeners.remove(listener);
    }

    /**
     * Lädt den Inhalt der übergebenen Datei in das Editorfenster.
     * 
     * @param path
     *            Pfad zu der zu ladenden Datei.
     * @param charset
     *            Encoding, mit dem die Datei gelesen werden soll.
     */
    public void loadTemplate(final Path path, final Charset charset) {
        final TemplatePanel templatePanel = new TemplatePanel(path, charset);
        addTemplatePanel(templatePanel);
        templatePanel.getEditorPane().addErrorListener(this::errorsChanged);
    }

    /**
     * Wird aufgerufen, wenn bei einer Validierung innerhalb des Editors neue Fehler innerhalb des Dokuments gefunden
     * wurden.
     * 
     * @param errors
     *            Liste der aktuell im Dokument erkannten Fehler. Die Liste enthält die Token, bei denen Probleme
     *            erkannt wurden zusammen mit einer entsprechenden Beschreibung des Problems.
     */
    private void errorsChanged(final Map<Token, String> errors) {
        errorListeners.forEach(l -> l.errorsChanged(errors));
    }

    /**
     * Speichert das in dem aktuell aktiven Editorfenster angezeigt Dokument auf die Festplatte.
     * 
     * @param path
     *            Pfad zu der Datei, in die das Dokument gespciehert werden soll.
     * @param charset
     *            Encoding zum Speichern der Datei.
     */
    public void saveTemplate(final Path path, final Charset charset) {
        final Component comp = getSelectedComponent();
        if (comp instanceof TemplatePanel) {
            final TemplatePanel tp = (TemplatePanel) comp;
            try {
                tp.getEditorPane().saveTemplatePanel(path, charset);
                final int idx = getSelectedIndex();
                final TabHeader tabLabel = (TabHeader) getTabComponentAt(idx);
                tabLabel.notifyTemplateSaved(path);
                tabListeners.stream().forEach(l -> l.tabSaved(tp));
            } catch (final IOException ioe) {
                JOptionPane.showMessageDialog(this, ioe.getLocalizedMessage());
            }
        }
    }

    /**
     * Gibt das aktuell ausgewählte Editor-Fenster zurück.
     * 
     * @return Aktuell ausgewähltes Editor-Fenster.
     */
    public TemplatePanel getSelectedTemplatePanel() {
        final Component comp = getSelectedComponent();
        if (comp instanceof TemplatePanel) {
            return (TemplatePanel) comp;
        } else {
            return null;
        }
    }

    /**
     * Prüft, ob das gerade aktive Editorfenster eine Template-Datei enthält.
     * 
     * @return {@code true} falls das aktive Editorfenster eine Template-Datei enthält, {@code false} falls das
     *         Editorfenster eine andere Datei enthält.
     */
    public boolean isTemplateSelected() {
        return getSelectedTemplatePanel() != null;
    }

    /**
     * Prüft, ob das gerade aktive Dokument noch neu ist oder bereits als Datei existiert.
     * 
     * @return {@code true} falls das Dokument neu ist und noch nie gepseichert wurde, {@code false} falls das Dokument
     *         aus einer Datei gelesen oder bereits einmal gespeichert wurde.
     */
    public boolean isSelectedTemplateNew() {
        return isTemplateSelected() && getSelectedTemplatePanel().isNewArtifact();
    }

    /**
     * Gibt den Namen der Datei zurück, in oder aus dem das Dokument in dem gerade aktiven Editorfenster zuletzt
     * geschrieben oder gelesen wurde.
     * 
     * @return Name der Datei, aus der das aktive Dokument stammt.
     */
    public Path getSelectedTemplatePath() {
        return isTemplateSelected() ? getSelectedTemplatePanel().getPath() : null;
    }

    /**
     * Gibt das Encoding der Datei zurück, mit dem das Dokument in dem gerade aktiven Editorfenster zuletzt in eine
     * Datei geschrieben oder gelesen wurde.
     * 
     * @return Encoding der Datei, aus der das aktive Dokument stammt.
     */
    public Charset getSelectedTemplateCharset() {
        return isTemplateSelected() ? getSelectedTemplatePanel().getCharset() : null;
    }

    /**
     * Prüft, ob es noch ein Editor-Fenster gibt, welches noch nicht gespeicherte Änderungen enthält.
     * 
     * @return {@code true}, falls noch ein Editor geöffnet ist, in dem die Änderungen noch nicht gespeichert wurden,
     *         {@code false} falls alle Änderungen gespeichert sind.
     */
    public boolean containsUnsavedModifications() {
        for (int i = 0; i < getTabCount(); i++) {
            final Component component = getComponentAt(i);
            if (component instanceof TemplatePanel) {
                final TemplatePanel tp = (TemplatePanel) component;
                if (tp.hasUnsavedModifications()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Fügt ein Paar der doppelten spitzen Klammern (&#x00ab;&#x00bb;) in den aktuell aktiven Editor ein.
     */
    public void insertBraces() {
        if (isTemplateSelected()) {
            final TemplatePanel templatePanel = getSelectedTemplatePanel();
            templatePanel.getEditorPane().performInsertBraces();
        } else {
            JOptionPane.showMessageDialog(this, "Es ist gerade gar kein Template geöffnet.");
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
     * Fügt einen Listener in die Liste der Listener ein, die über Änderungen an den im gerade aktiven Dokument
     * enthaltenen Fehlern informiert werden wollen.
     * 
     * @param listener
     *            Listener, der zu der Liste hinzugefügt werden soll.
     */
    public void addErrorListener(final ErrorListener listener) {
        errorListeners.add(listener);
    }

    /**
     * Entfernt einen Listener von der Liste der Listener, die über Änderungen an den im gerade aktiven Dokument
     * enthaltenen Fehlern informiert werden wollen.
     * 
     * @param listener
     *            Listener, der von der Liste entfernt werden soll.
     */
    public void removeErrorListener(final ErrorListener listener) {
        errorListeners.remove(listener);
    }
}