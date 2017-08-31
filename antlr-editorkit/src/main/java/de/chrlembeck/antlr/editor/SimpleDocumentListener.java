package de.chrlembeck.antlr.editor;

import java.util.function.Consumer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Hilfklasse zur Definition eines einfachen DocumentListeners, der bei den drei Operationen Einfügen, Ändern und
 * Löschen jeweils genau die gleiche Aktion durchführt. Die Durchzuführende Aktion kann dem Listener übergeben werden.
 * 
 * @author Christoph Lembeck
 */
public class SimpleDocumentListener implements DocumentListener {

    /**
     * Aktion, die durchgeführt werden soll, wenn eine Änderung an dem beobachteten Dokument vorgenommen wurde.
     */
    private Consumer<DocumentEvent> eventConsumer;

    /**
     * Erstellt einen neuen Listener mit der übergebenen Aktion als Reaktion auf jegliche Änderungen im beobachteten
     * Dokument.
     * 
     * @param eventConsumer
     *            Aktion, die durchgeführt werden soll, wenn eine Änderung an dem beobachteten Dokument vorgenommen
     *            wurde.
     */
    public SimpleDocumentListener(final Consumer<DocumentEvent> eventConsumer) {
        this.eventConsumer = eventConsumer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertUpdate(final DocumentEvent documentEvent) {
        eventConsumer.accept(documentEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUpdate(final DocumentEvent documentEvent) {
        eventConsumer.accept(documentEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changedUpdate(final DocumentEvent documentEvent) {
        eventConsumer.accept(documentEvent);
    }
}