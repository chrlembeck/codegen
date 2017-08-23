package de.chrlembeck.antlr.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.Utilities;

/**
 * ScrollPaneDecorator zur Verwendung als RowHeaderView in einer JScrollPane einer Text-Komponente.
 * 
 * @see JScrollPane#setRowHeaderView(java.awt.Component)
 */
public class LineNumberComponent extends JPanel {

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = -7472557743611616979L;

    /**
     * Abstand zwischen Zeilennummern und Rändern.
     */
    private final int BORDER_PADDING = 5;

    /**
     * Text-Komponente, die im Scrollpane dargestellt werden soll, und an der sich die Zeilennummerierung ausrichten
     * soll.
     */
    private JTextComponent component;

    /**
     * Farbe für die Zeilennummer der Zeile, in der sich der Cursor befindet.
     */
    private Color currentLineForeground;

    /**
     * Zuletzt benötigte Anzahl an Ziffern für die Berechnung der Breite der Komponente.
     */
    private int lastDigits;

    /**
     * Zuletzt zum Zeichnen verwendete Start-Y-Koordinate
     */
    private int lastYPos;

    /**
     * Zeile, in der sich der Cursor zuletzt befunden hat.
     */
    private int lastLineIndex;

    /**
     * Speichert Fontmetrics zu den im Textdokument verwendeten Schriftstilen ab.
     */
    private HashMap<String, FontMetrics> fontMetricsCache;

    /**
     * Erzeugt eine neue LineNumberComponent zur Verwenung mit der übergebenen Text-Komponente.
     * 
     * @param component
     *            JTextComponent, an der sich die Zeilennummerierung ausrichten soll.
     */
    public LineNumberComponent(final JTextComponent component) {
        this.component = component;
        setFont(component.getFont());
        setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
                BorderFactory.createEmptyBorder(0, BORDER_PADDING, 0, BORDER_PADDING)));
        setCurrentLineForeground(Color.RED);
        component.getDocument().addDocumentListener(new SimpleDocumentListener(this::documentChanged));
        component.addCaretListener(this::caretMoved);
        component.addPropertyChangeListener("font", this::fontChanged);
    }

    /**
     * Gibt die Farbe für die Zeilennummerierung der Zeile, in der sich der Cursor befindet, zurück.
     * 
     * @return Farbe für die Zeilennummerierung der Zeile mit dem Cursor.
     */
    public Color getCurrentLineForeground() {
        return currentLineForeground == null ? getForeground() : currentLineForeground;
    }

    /**
     * Setzt die Farbe für die Zeilennummerierung der Zeile, in der sich der Cursor befindet.
     * 
     * @param currentLineForeground
     *            Farbe für die Zeilennummerierung der Zeile mit dem Cursor.
     */
    public void setCurrentLineForeground(final Color currentLineForeground) {
        this.currentLineForeground = currentLineForeground;
    }

    /**
     * Prüft, ob die PreferredSize der Komponente aufgrund einer Änderung der darzustellenden Ziffern angepasst werden
     * muss und führt die Anpassung ggf. durch.
     */
    private void updatePreferredWidth() {
        final Element root = component.getDocument().getDefaultRootElement();
        final int lines = root.getElementCount();
        final int digits = Integer.toString(lines).length();

        if (lastDigits != digits) {
            lastDigits = digits;
            final FontMetrics fontMetrics = getFontMetrics(getFont());
            final int width = fontMetrics.charWidth('0') * digits;
            final Insets insets = getInsets();
            final int preferredWidth = insets.left + insets.right + width;

            final Dimension d = getPreferredSize();
            d.setSize(preferredWidth, Integer.MAX_VALUE - 1000000);
            setPreferredSize(d);
            setSize(d);
        }
    }

    /**
     * Zeichnet die Zeilennummern.
     * 
     * @param g
     *            Zum Zeichnen zu verwendendes Graphics-Objekt.
     */
    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        final FontMetrics fontMetrics = component.getFontMetrics(component.getFont());
        final Insets insets = getInsets();
        final int availableWidth = getSize().width - insets.left - insets.right;

        final Rectangle clip = g.getClipBounds();
        int rowStartOffset = component.viewToModel(new Point(0, clip.y));
        final int endOffset = component.viewToModel(new Point(0, clip.y + clip.height));

        while (rowStartOffset <= endOffset) {
            try {
                if (isCurrentLine(rowStartOffset)) {
                    g.setColor(getCurrentLineForeground());
                } else {
                    g.setColor(getForeground());
                }
                final String lineNumber = getTextLineNumber(rowStartOffset);
                final int stringWidth = fontMetrics.stringWidth(lineNumber);
                final int x = availableWidth - stringWidth + insets.left;
                final int y = getOffsetY(rowStartOffset, fontMetrics);
                g.drawString(lineNumber, x, y);

                rowStartOffset = Utilities.getRowEnd(component, rowStartOffset) + 1;
            } catch (final Exception e) {
                break;
            }
        }
    }

    /**
     * Prüft, ob sich der Cursor in der Zeile befindet, die an dem übergebenen index beginnt.
     * 
     * @param rowStartIndex
     *            Indexposition, an der zu prüfende Zeile im Dokument beginnt.
     * @return true, falls der Index die Zeile beschreibt, in der der Cursor ist, sonst false.
     */
    private boolean isCurrentLine(final int rowStartIndex) {
        final int caretPosition = component.getCaretPosition();
        final Element root = component.getDocument().getDefaultRootElement();
        return root.getElementIndex(rowStartIndex) == root.getElementIndex(caretPosition);
    }

    /**
     * Gibt den für die Zeile anzuzeigenden Text zurück. Bei Zeilen mit Zeilenumbruch wird ein Leerstring zurückgegeben.
     * 
     * @param rowStartOffset
     *            Indexposition des Zeichens zu dessen Zeile die Zeilennummer ausgegeben werden soll.
     * @return Formatierte Zeilennummer der Zeile oder ein Leerstring, wenn die Zeile einen Zeilenumbruch besitzt.
     */
    protected String getTextLineNumber(final int rowStartOffset) {
        final Element root = component.getDocument().getDefaultRootElement();
        final int index = root.getElementIndex(rowStartOffset);
        final Element line = root.getElement(index);

        if (line.getStartOffset() == rowStartOffset) {
            return String.valueOf(index + 1);
        } else {
            return "";
        }
    }

    /**
     * Berechnet die Vertikale Startposition für die Zeilennummerierung zu der gewünschten Zeile. Die Zeile wird dabei
     * durch ihren Start-Index im Dokument angegeben.
     * 
     * @param rowStartOffset
     *            Index des ersten Zeichens in der Zeile.
     * @param fontMetrics
     *            FontMetrics der Schriftart des TextDokumentes. In der Regel ist diese ausreichend. Bei Dokumenten mit
     *            verschiedenen Schriftarten muss in der Methode ggf. ein neues FontMetrics-Objekt ermittelt werden.
     * @return Y-Koordinate für das Zeichnen der Zeilennummerierung.
     * @throws BadLocationException
     *             Falls die angegebene Zeichenposition keine gültige Position in dem Dokument ist.
     */
    private int getOffsetY(final int rowStartOffset, final FontMetrics fontMetrics)
            throws BadLocationException {

        final Rectangle r = component.modelToView(rowStartOffset);
        final int lineHeight = fontMetrics.getHeight();
        final int y = r.y + r.height;
        int descent = 0;

        if (r.height == lineHeight) {
            descent = fontMetrics.getDescent();
        } else {
            if (fontMetricsCache == null) {
                fontMetricsCache = new HashMap<String, FontMetrics>();
            }

            final Element root = component.getDocument().getDefaultRootElement();
            final int index = root.getElementIndex(rowStartOffset);
            final Element line = root.getElement(index);

            for (int i = 0; i < line.getElementCount(); i++) {
                final Element child = line.getElement(i);
                final AttributeSet as = child.getAttributes();
                final String fontFamily = (String) as.getAttribute(StyleConstants.FontFamily);
                final Integer fontSize = (Integer) as.getAttribute(StyleConstants.FontSize);
                final String key = fontFamily + fontSize;

                FontMetrics fm = fontMetricsCache.get(key);

                if (fm == null) {
                    final Font font = new Font(fontFamily, Font.PLAIN, fontSize);
                    fm = component.getFontMetrics(font);
                    fontMetricsCache.put(key, fm);
                }

                descent = Math.max(descent, fm.getDescent());
            }
        }
        return y - descent;
    }

    /**
     * Wird aufgerufen, wenn sich die Position des Cursors im Dokument geändert hat. Ist er in eine neue Zeile
     * gesprungen, muss die hervorgehobene Nummerierung entsprechend angepasst werden.
     * 
     * @param caretEvent
     *            Event, dass über die Cursorbewegung informiert.
     */
    protected void caretMoved(final CaretEvent caretEvent) {
        final int caretPosition = component.getCaretPosition();
        final Element rootElement = component.getDocument().getDefaultRootElement();
        final int lineIndex = rootElement.getElementIndex(caretPosition);
        if (lastLineIndex != lineIndex) {
            repaint();
            lastLineIndex = lineIndex;
        }
    }

    /**
     * Wird aufgerufen, wenn es eine Änderung am Textdokument gegeben hat. Hier wird dann geprüft, ob die
     * Zeilennummerierung ggf. angepasst werden muss.
     * 
     * @param documentEvent
     *            Event, welches über die Änderung im Dokument informiert.
     */
    private void documentChanged(final DocumentEvent documentEvent) {
        SwingUtilities.invokeLater(this::updateLineNumbers);
    }

    /**
     * Stößt, falls erfordertlich, ein Neuzeichnen der Zeilennummern an.
     */
    protected void updateLineNumbers() {
        try {
            final int endPos = component.getDocument().getLength();
            final Rectangle rect = component.modelToView(endPos);

            if (rect != null && rect.y != lastYPos) {
                updatePreferredWidth();
                repaint();
                lastYPos = rect.y;
            }
        } catch (final BadLocationException ex) {
        }
    }

    /**
     * Wird aufgerufen, wenn sich die Schriftart des Textdokumentes geändert hat. Die Schriftart der Zeilennummerierung
     * wird dann angepasst.
     * 
     * @param evt
     *            Event, welches die Änderung der Schriftart angezeigt hat.
     */
    public void fontChanged(final PropertyChangeEvent evt) {
        if (evt.getNewValue() instanceof Font) {
            final Font newFont = (Font) evt.getNewValue();
            setFont(newFont);
            lastDigits = 0;
            updatePreferredWidth();
        }
    }
}