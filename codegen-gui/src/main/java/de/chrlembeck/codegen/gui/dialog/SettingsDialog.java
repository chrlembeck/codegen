package de.chrlembeck.codegen.gui.dialog;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import de.chrlembeck.antlr.editor.AntlrEditorKit;
import de.chrlembeck.antlr.editor.TokenStyle;
import de.chrlembeck.antlr.editor.TokenStyleRepository;
import de.chrlembeck.codegen.grammar.CodeGenParser.TemplateFileContext;
import de.chrlembeck.codegen.gui.CodeGenGui;
import de.chrlembeck.codegen.gui.EditorTabs;
import de.chrlembeck.codegen.gui.IconFactory;
import de.chrlembeck.codegen.gui.TemplateEditorPane;
import de.chrlembeck.codegen.gui.UserSettings;
import de.chrlembeck.util.objects.ToStringWrapper;
import de.chrlembeck.util.swing.action.DefaultAction;
import de.chrlembeck.util.swing.components.ColorChooserDialog;
import de.chrlembeck.util.swing.icon.RectangleIcon;

public class SettingsDialog extends AbstractDialog {

    private static final String EXAMPLE_TEMPLATE = "«COMMENT»Ein Kommentar«ENDCOMMENT»\n" +
            "«TEMPLATE root FOR java.lang.String»\n" +
            "  «OUTPUT \"file.txt\"»\n" +
            "    PI = «(float)3.1415»\n" +
            "  «ENDOUTPUT»\n" +
            "  «`invald_character`»\n" +
            "«ENDTEMPLATE»";

    private static final long serialVersionUID = 686994149235060167L;

    private JButton btKeywordColor;

    private JButton btPrimitiveTypeColor;

    private JButton btStringLiteralColor;

    private JButton btErrorColor;

    private JButton btCommentColor;

    private JButton btDefaultColor;

    private JButton btLiteralColor;

    private Map<StyleKeys, TokenStyle> styles;

    private TemplateEditorPane editorPane;

    private UserSettings settings;

    private CodeGenGui codeGenGui;

    private JComboBox<ToStringWrapper<Integer>> cbKeywordStyle;

    private JComboBox<ToStringWrapper<Integer>> cbPrimitiveTypeStyle;

    private JComboBox<ToStringWrapper<Integer>> cbStringLiteralStyle;

    private JComboBox<ToStringWrapper<Integer>> cbErrorStyle;

    private JComboBox<ToStringWrapper<Integer>> cbCommentStyle;

    private JComboBox<ToStringWrapper<Integer>> cbDefaultStyle;

    private JComboBox<ToStringWrapper<Integer>> cbLiteralStyle;

    public SettingsDialog(final CodeGenGui codeGenGui) {
        super(codeGenGui, "Einstellungen");
        this.codeGenGui = codeGenGui;
        addButton(new DefaultAction("OK", "OK", "Übernimmt die Einstellungen und schließt den Dialog", KeyEvent.VK_O,
                null, null, IconFactory.OK_22.icon(), null, this::okButtonPressed));
        addCancelButton();
    }

    @Override
    protected JComponent createMainPanel() {
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(true);
        tabbedPane.addTab("Editorfarben", createEditorColorsTab());
        tabbedPane.setBackgroundAt(0, Color.RED);
        return tabbedPane;
    }

    private enum StyleKeys {
        DEFAULT, KEYWORD, PRIMITIVE_TYPE, STRING_LITERAL, LITERAL, ERROR, COMMENT;
    }

    private JPanel createEditorColorsTab() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final JPanel pnColors = new JPanel();
        final JPanel pnExample = new JPanel();
        pnColors.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Farben"));
        pnExample.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Beispiel"));
        panel.add(pnColors, new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        panel.add(pnExample, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        final JLabel lbDefault = new JLabel("Standard");
        final JLabel lbKeyword = new JLabel("Schlüsselwörter");
        final JLabel lbPrimitiveType = new JLabel("Primitive Datentypen");
        final JLabel lbStringLiteral = new JLabel("Zeichenketten");
        final JLabel lbLiteral = new JLabel("Literale");
        final JLabel lbError = new JLabel("Syntaxfehler");
        final JLabel lbComment = new JLabel("Kommentare");

        settings = new UserSettings();
        final TokenStyle defaultStyle = settings.getDefaultTokenStyle();
        final TokenStyle keywordStyle = settings.getKeywordTokenStyle();
        final TokenStyle primitiveTypeStyle = settings.getPrimitiveTypeTokenStyle();
        final TokenStyle stringLiteralStyle = settings.getStringLiteralTokenStyle();
        final TokenStyle literalStyle = settings.getLiteralTokenStyle();
        final TokenStyle errorStyle = settings.getErrorTokenStyle();
        final TokenStyle commentStyle = settings.getCommentTokenStyle();
        styles = new TreeMap<>();
        styles.put(StyleKeys.DEFAULT, defaultStyle);
        styles.put(StyleKeys.KEYWORD, keywordStyle);
        styles.put(StyleKeys.PRIMITIVE_TYPE, primitiveTypeStyle);
        styles.put(StyleKeys.STRING_LITERAL, stringLiteralStyle);
        styles.put(StyleKeys.LITERAL, literalStyle);
        styles.put(StyleKeys.ERROR, errorStyle);
        styles.put(StyleKeys.COMMENT, commentStyle);

        cbDefaultStyle = createTextStyleCombobox(defaultStyle.getFontStyle(), StyleKeys.DEFAULT);
        cbKeywordStyle = createTextStyleCombobox(keywordStyle.getFontStyle(), StyleKeys.KEYWORD);
        cbPrimitiveTypeStyle = createTextStyleCombobox(primitiveTypeStyle.getFontStyle(), StyleKeys.PRIMITIVE_TYPE);
        cbStringLiteralStyle = createTextStyleCombobox(stringLiteralStyle.getFontStyle(), StyleKeys.STRING_LITERAL);
        cbLiteralStyle = createTextStyleCombobox(literalStyle.getFontStyle(), StyleKeys.LITERAL);
        cbErrorStyle = createTextStyleCombobox(errorStyle.getFontStyle(), StyleKeys.ERROR);
        cbCommentStyle = createTextStyleCombobox(commentStyle.getFontStyle(), StyleKeys.COMMENT);
        final int height = cbKeywordStyle.getPreferredSize().height;
        btDefaultColor = createColorButton(defaultStyle.getColor(), height, StyleKeys.DEFAULT);
        btKeywordColor = createColorButton(keywordStyle.getColor(), height, StyleKeys.KEYWORD);
        btPrimitiveTypeColor = createColorButton(primitiveTypeStyle.getColor(), height, StyleKeys.PRIMITIVE_TYPE);
        btStringLiteralColor = createColorButton(stringLiteralStyle.getColor(), height, StyleKeys.STRING_LITERAL);
        btLiteralColor = createColorButton(literalStyle.getColor(), height, StyleKeys.LITERAL);
        btErrorColor = createColorButton(errorStyle.getColor(), height, StyleKeys.ERROR);
        btCommentColor = createColorButton(commentStyle.getColor(), height, StyleKeys.COMMENT);
        final JButton btResetFonts = new JButton("Zurücksetzen");
        btResetFonts.addActionListener(this::resetFonts);

        pnColors.setLayout(new GridBagLayout());
        pnColors.add(lbDefault, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btDefaultColor, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbDefaultStyle, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pnColors.add(lbKeyword, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btKeywordColor, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbKeywordStyle, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pnColors.add(lbPrimitiveType, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btPrimitiveTypeColor, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbPrimitiveTypeStyle, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pnColors.add(lbStringLiteral, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btStringLiteralColor, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbStringLiteralStyle, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pnColors.add(lbLiteral, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btLiteralColor, new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbLiteralStyle, new GridBagConstraints(2, 4, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pnColors.add(lbError, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btErrorColor, new GridBagConstraints(1, 5, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbErrorStyle, new GridBagConstraints(2, 5, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pnColors.add(lbComment, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(btCommentColor, new GridBagConstraints(1, 6, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnColors.add(cbCommentStyle, new GridBagConstraints(2, 6, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pnColors.add(btResetFonts, new GridBagConstraints(0, 7, GridBagConstraints.REMAINDER, 1, 0, 1,
                GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(10, 2, 2, 2), 0, 0));

        pnExample.setLayout(new BorderLayout());
        final TokenStyleRepository tokenStyles = createTokenStyle(styles);

        editorPane = new TemplateEditorPane(null, null, tokenStyles);
        editorPane.setEditable(false);
        editorPane.setText(EXAMPLE_TEMPLATE);
        final JScrollPane spExample = new JScrollPane(editorPane);
        pnExample.add(spExample, BorderLayout.CENTER);

        return panel;
    }

    private JComboBox<ToStringWrapper<Integer>> createTextStyleCombobox(final int fontStyle, final StyleKeys styleKey) {
        final JComboBox<ToStringWrapper<Integer>> comboBox = new JComboBox<>();
        comboBox.setEditable(false);
        final Function<Integer, String> toStringFunction = new TextStyleRepresenter();
        for (int i = 0; i <= 3; i++) {
            comboBox.addItem(new ToStringWrapper<Integer>(i, toStringFunction));
        }
        comboBox.setSelectedIndex(fontStyle);
        comboBox.putClientProperty("codeGenColorKey", styleKey);
        comboBox.addActionListener(this::fontStyleChanged);
        return comboBox;
    }

    private JButton createColorButton(final Color color, final int height, final StyleKeys styleKey) {
        final JButton button = new JButton(
                createColorIcon(color));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, height));
        button.putClientProperty("codeGenColorKey", styleKey);
        button.setBorderPainted(false);
        button.addActionListener(this::colorButtonPressed);
        return button;
    }

    void fontStyleChanged(final ActionEvent event) {
        @SuppressWarnings("unchecked")
        final JComboBox<ToStringWrapper<Integer>> comboBox = (JComboBox<ToStringWrapper<Integer>>) event.getSource();
        final StyleKeys styleKey = (StyleKeys) comboBox.getClientProperty("codeGenColorKey");
        @SuppressWarnings("unchecked")
        final ToStringWrapper<Integer> selection = (ToStringWrapper<Integer>) comboBox.getSelectedItem();
        final TokenStyle oldStyle = styles.get(styleKey);
        styles.put(styleKey, new TokenStyle(oldStyle.getColor(), selection.getObject().intValue()));
        updateExampleStyle(createTokenStyle(styles));
    }

    private RectangleIcon createColorIcon(final Color color) {
        return new RectangleIcon(20, 10, 20, 10, new BasicStroke(1), Color.DARK_GRAY, color, Color.WHITE, 1);
    }

    void colorButtonPressed(final ActionEvent event) {
        final JButton button = (JButton) event.getSource();
        final StyleKeys styleKey = (StyleKeys) button.getClientProperty("codeGenColorKey");
        final Color color = styles.get(styleKey).getColor();
        final Color newColor = ColorChooserDialog.openColorChooser(button, "Farbe wählen", color);
        if (newColor != null) {
            button.setIcon(createColorIcon(newColor));
            final TokenStyle oldStyle = styles.get(styleKey);
            styles.put(styleKey, new TokenStyle(newColor, oldStyle.getFontStyle()));
            updateExampleStyle(createTokenStyle(styles));
        }
    }

    private void updateExampleStyle(final TokenStyleRepository newTokenStyles) {
        @SuppressWarnings("unchecked")
        final AntlrEditorKit<TemplateFileContext> editorKit = (AntlrEditorKit<TemplateFileContext>) editorPane
                .getEditorKit();
        editorKit.updateStyleRepository(newTokenStyles);
        editorPane.invalidate();
        editorPane.repaint();
    }

    void okButtonPressed(final ActionEvent event) {
        settings.setDefaultTokenStyle(styles.get(StyleKeys.DEFAULT));
        settings.setKeywordTokenStyle(styles.get(StyleKeys.KEYWORD));
        settings.setStringLiteralTokenStyle(styles.get(StyleKeys.STRING_LITERAL));
        settings.setLiteralTokenStyle(styles.get(StyleKeys.LITERAL));
        settings.setCommentTokenStyle(styles.get(StyleKeys.COMMENT));
        settings.setErrorTokenStyle(styles.get(StyleKeys.ERROR));
        settings.setPrimitiveTypeTokenStyle(styles.get(StyleKeys.PRIMITIVE_TYPE));
        codeGenGui.updateTokenStyles(createTokenStyle(styles));

        setResult(RESULT_OK);
        dispose();
    }

    static class TextStyleRepresenter implements Function<Integer, String> {

        @Override
        public String apply(final Integer style) {
            switch (style.intValue()) {
                case Font.PLAIN:
                    return "Normal";
                case Font.BOLD:
                    return "Fett";
                case Font.ITALIC:
                    return "Kursiv";
                case Font.BOLD | Font.ITALIC:
                    return "Fett und Kursiv";
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    void resetFonts(final ActionEvent event) {
        styles.put(StyleKeys.DEFAULT,
                new TokenStyle(UserSettings.DEFAULT_DEFAULT_COLOR, UserSettings.DEFAULT_DEFAULT_FONT_STYLE));
        styles.put(StyleKeys.COMMENT,
                new TokenStyle(UserSettings.DEFAULT_COMMENT_COLOR, UserSettings.DEFAULT_COMMENT_FONT_STYLE));
        styles.put(StyleKeys.KEYWORD,
                new TokenStyle(UserSettings.DEFAULT_KEYWORD_COLOR, UserSettings.DEFAULT_KEYWORD_FONT_STYLE));
        styles.put(StyleKeys.STRING_LITERAL, new TokenStyle(UserSettings.DEFAULT_STRING_LITERAL_COLOR,
                UserSettings.DEFAULT_STRING_LITERAL_FONT_STYLE));
        styles.put(StyleKeys.LITERAL, new TokenStyle(UserSettings.DEFAULT_LITERAL_COLOR,
                UserSettings.DEFAULT_LITERAL_FONT_STYLE));
        styles.put(StyleKeys.ERROR,
                new TokenStyle(UserSettings.DEFAULT_ERROR_COLOR, UserSettings.DEFAULT_ERROR_FONT_STYLE));
        styles.put(StyleKeys.PRIMITIVE_TYPE, new TokenStyle(UserSettings.DEFAULT_PRIMITIVE_TYPE_COLOR,
                UserSettings.DEFAULT_PRIMITIVE_TYPE_FONT_STYLE));
        btDefaultColor.setIcon(createColorIcon(UserSettings.DEFAULT_DEFAULT_COLOR));
        btCommentColor.setIcon(createColorIcon(UserSettings.DEFAULT_COMMENT_COLOR));
        btKeywordColor.setIcon(createColorIcon(UserSettings.DEFAULT_KEYWORD_COLOR));
        btStringLiteralColor.setIcon(createColorIcon(UserSettings.DEFAULT_STRING_LITERAL_COLOR));
        btLiteralColor.setIcon(createColorIcon(UserSettings.DEFAULT_LITERAL_COLOR));
        btErrorColor.setIcon(createColorIcon(UserSettings.DEFAULT_ERROR_COLOR));
        btPrimitiveTypeColor.setIcon(createColorIcon(UserSettings.DEFAULT_PRIMITIVE_TYPE_COLOR));
        cbDefaultStyle.setSelectedIndex(UserSettings.DEFAULT_DEFAULT_FONT_STYLE);
        cbCommentStyle.setSelectedIndex(UserSettings.DEFAULT_COMMENT_FONT_STYLE);
        cbErrorStyle.setSelectedIndex(UserSettings.DEFAULT_ERROR_FONT_STYLE);
        cbKeywordStyle.setSelectedIndex(UserSettings.DEFAULT_KEYWORD_FONT_STYLE);
        cbStringLiteralStyle.setSelectedIndex(UserSettings.DEFAULT_STRING_LITERAL_FONT_STYLE);
        cbLiteralStyle.setSelectedIndex(UserSettings.DEFAULT_LITERAL_FONT_STYLE);
        cbPrimitiveTypeStyle.setSelectedIndex(UserSettings.DEFAULT_PRIMITIVE_TYPE_FONT_STYLE);
        updateExampleStyle(createTokenStyle(styles));
    }

    private TokenStyleRepository createTokenStyle(final Map<StyleKeys, TokenStyle> styles) {
        return EditorTabs.createTokenStyles(styles.get(StyleKeys.DEFAULT), styles.get(StyleKeys.KEYWORD),
                styles.get(StyleKeys.PRIMITIVE_TYPE), styles.get(StyleKeys.STRING_LITERAL),
                styles.get(StyleKeys.ERROR), styles.get(StyleKeys.COMMENT), styles.get(StyleKeys.LITERAL));
    }
}