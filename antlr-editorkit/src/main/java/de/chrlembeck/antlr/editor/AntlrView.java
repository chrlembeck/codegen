package de.chrlembeck.antlr.editor;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;

import org.antlr.v4.runtime.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View zum Zeichnen der einzelnen ANTLR-Token in der EditorPane. Wird vom AntlrEditorKit verwendet und dort erstellt.
 * 
 * @see AntlrEditorKit#create(Element)
 * 
 * @author Christoph Lembeck
 */
public class AntlrView extends PlainView {

    /**
     * Der Logger für die Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AntlrView.class);

    /**
     * Referenz auf das Repository welches die Farben und Schriftschnitte für die einzelnen Token-Typen beinhaltet.
     */
    private TokenStyleRepository styleRepository;

    /**
     * Erstellt eine neue View zu dem übergebenen Element für die Verwendung im AntlrEditorKit.
     * 
     * @param element
     *            Durch die View zu zeichnendes Element.
     * @param styleRepository
     *            Referenz auf das Repository welches die Farben und Schriftschnitte für die einzelnen Token-Typen
     *            beinhaltet.
     */
    AntlrView(final Element element, final TokenStyleRepository styleRepository) {
        super(element);
        this.styleRepository = styleRepository;
    }

    /**
     * Zeichnet die Token aus dem ausgewählten Textbereich mit den jeweiligen im TokenStyleRepository festgelegten
     * Farben und Schriftschnitten.
     * 
     * @param grfx
     *            Zum Zeichnen zu verwendendes Graphics-Object.
     * @param xPos
     *            Horizontale Startposition zum Zeichnen des Textes.
     * @param yPos
     *            Vertikale Startposition zum Zeichnen des Textes.
     * @param requestedPaintStartIdx
     *            Index des ersten zu Zeichnenden Buchstabens (inclusive) beginnend bei 0.
     * @param requestedPaintEndIdx
     *            Index des letzten zu zeichnenden Buchstanens (exclusive) beginnend bei 0.
     * @return X-Koordinate für das nächtste zu zeichnende Zeichen.
     */
    @Override
    protected final float drawUnselectedText(final Graphics2D grfx, float xPos, final float yPos,
            final int requestedPaintStartIdx,
            final int requestedPaintEndIdx) {
        final Graphics2D graphics = (Graphics2D) grfx.create();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        final Segment segment = getLineBuffer();
        final AntlrDocument<?> document = (AntlrDocument<?>) getDocument();
        final List<Token> tokens = document.getTokenSubList(requestedPaintStartIdx, requestedPaintEndIdx);
        try {
            for (final Token tokenToPaint : tokens) {
                final int textBegin = Math.max(tokenToPaint.getStartIndex(), requestedPaintStartIdx);
                final int textLength = Math.min(tokenToPaint.getStopIndex(), requestedPaintEndIdx - 1)
                        - textBegin + 1;
                document.getText(textBegin, textLength, segment);
                xPos = paintTokenSegment(graphics, document, tokenToPaint, segment, xPos, yPos, this);
            }
        } catch (final BadLocationException ex) {
            LOGGER.error("Requested: " + ex.offsetRequested(), ex);
        }
        return xPos;
    }

    /**
     * Zeichenmethode für selektierten Text. Verhält sich genau wie
     * {@link #drawUnselectedText(Graphics, int, int, int, int)}, da das Zeichnen der Selektion durch Highlighter
     * erledigt wird.
     * 
     * @param gfraphics
     *            Zum Zeichnen zu verwendendes Graphics-Object.
     * @param xPos
     *            Horizontale Startposition zum Zeichnen des Textes.
     * @param yPos
     *            Vertikale Startposition zum Zeichnen des Textes.
     * @param requestedPaintStartIdx
     *            Index des ersten zu zeichnenden Buchstabens (inclusive) beginnend bei 0.
     * @param requestedPaintEndIdx
     *            Index des letzten zu zeichnenden Buchstabens (exclusive) beginnend bei 0.
     * @return X-Koordinate für das nächste zu zeichnende Zeichen.
     */
    @Override
    protected int drawSelectedText(final Graphics gfraphics, final int xPos, final int yPos,
            final int requestedPaintStartIdx,
            final int requestedPaintEndIdx)
            throws BadLocationException {
        return drawUnselectedText(gfraphics, xPos, yPos, requestedPaintStartIdx, requestedPaintEndIdx);
    }

    /**
     * Führt das eigentliche Zeichnen des (Teil-)Tokens auf dem EditorPane durch.
     * 
     * @param graphics
     *            Graphics-Objekt, das zum Zeichnen auf dem EditorPane verwendet werden soll.
     * @param document
     *            Referenz auf das Document, welches im EditorPane dargestellt wird.
     * @param token
     *            Token, welches komplett oder in Teilen gezeichnet werden soll.
     * @param segment
     *            Teil des Tokens, der gezeichnet werden soll.
     * @param xPos
     *            X-Koordinate für den Beginn des Zeichnens.
     * @param yPos
     *            Y-Koordinate für den Beginn des Zeichnens.
     * @param tabExpander
     *            Für die Ausrichtung von Tab-Stops zu verwendender TabExpander.
     * @return X-Koordinate für das nächtste zu zeichnende Zeichen.
     */
    protected float paintTokenSegment(final Graphics2D graphics, final AntlrDocument<?> document, final Token token,
            final Segment segment, final float xPos, final float yPos,
            final TabExpander tabExpander) {
        final int startOffset = token.getStartIndex();
        final TokenStyle style = styleRepository.getStyle(token);
        graphics.setFont(graphics.getFont().deriveFont(style.getFontStyle()));
        final FontMetrics fontMetrics = graphics.getFontMetrics();
        final int ascent = fontMetrics.getAscent();
        final int lineHeight = ascent + fontMetrics.getDescent();
        final float textWidht = Utilities.getTabbedTextWidth(segment, fontMetrics, 0f, tabExpander, startOffset);
        final Rectangle2D border = new Rectangle2D.Float(xPos - 1, yPos - ascent + 1, textWidht + 2, lineHeight);
        if (document.hasError(token)) {
            graphics.setColor(Color.RED);
            graphics.draw(border);
        }
        graphics.setColor(style.getColor());
        return Utilities.drawTabbedText(segment, xPos, yPos, graphics, tabExpander, startOffset);
    }
}