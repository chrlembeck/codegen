package de.chrlembeck.codegen.gui;

import java.awt.BorderLayout;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.antlr.v4.runtime.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.Position;
import de.chrlembeck.codegen.gui.action.CloseApplicationAction;
import de.chrlembeck.codegen.gui.action.GenerateAction;
import de.chrlembeck.codegen.gui.action.InsertDoubleAngleQuotationMarksAction;
import de.chrlembeck.codegen.gui.action.LoadModelAction;
import de.chrlembeck.codegen.gui.action.LoadTemplateAction;
import de.chrlembeck.codegen.gui.action.NewTemplateAction;
import de.chrlembeck.codegen.gui.action.SaveTemplateAction;
import de.chrlembeck.codegen.gui.action.SaveTemplateAsAction;
import de.chrlembeck.codegen.gui.action.SettingsAction;

/**
 * Hauptklasse der Generator-GUI.
 * 
 * @author Christoph Lembeck
 */
public class CodeGenGui extends JFrame implements TabListener {

    /**
     * Der Logger für diese Klasse.
     */
    private static Logger LOGGER = LoggerFactory.getLogger(CodeGenGui.class);

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Startet den Editor und Zeigt ihn auf dem Bildschirm an.
     * 
     * @param args
     *            Werden nicht verwendet.
     */
    public static void main(final String... args) {
        setSystemLookAndFeel();
        SwingUtilities.invokeLater(CodeGenGui::new);
    }

    /**
     * Setzt das Look And Feel des Betriebssystems.
     */
    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (final UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            LOGGER.error(e.getLocalizedMessage());
            System.exit(-1);
        }
    }

    /**
     * Panel, in dem die einzelnene Editor-Fenster geöffnet werden.
     */
    private EditorTabs editorTabs;

    /**
     * Splitpane für die Editor-Fenster (oden) und die Ausgabe (unten).
     */
    private JSplitPane spMain;

    /**
     * Label für die Anzeige der Cusros-Position.
     */
    private JLabel lbPosition;

    /**
     * Button in der Toolbar zum Anlegen eines neuen Templates.
     */
    private JButton btNewTemplate;

    /**
     * Button in der Toolbar zum Speichern des aktiven Fenstrinhalts.
     */
    private JButton btSaveTemplate;

    /**
     * Button in der Toolbar zum Speichern des aktiven Fensterinhalts unter einem neuen Namen.
     */
    private JButton btSaveTemplateAs;

    /**
     * Button in der Toolbar zum Einfügen eines Paares der doppeltn spitzen Klammern.
     */
    private JButton btInsertBraces;

    /**
     * Menüeintrag zum Speichern des aktiven Fensterinhaltes.
     */
    private JMenuItem miSaveTemplate;

    /**
     * Menüeintrag zum Speichern des aktiven Fensterinhaltes zunter einem neuen Namen.
     */
    private JMenuItem miSaveTemplateAs;

    /**
     * Referenz auf das zuletzt geladene Modell.
     */
    private Object model;

    /**
     * Menüeintarg zum Anlegen eines neuen Templates
     */
    private JMenuItem miNewTemplate;

    /**
     * Menüeintrag zum Laden eines Templates aus einer Datei.
     */
    private JMenuItem miLoadTemplate;

    /**
     * Menüeintrag zum Laden eines Modells.
     */
    private JMenuItem miLoadModel;

    /**
     * Menüeintrag zum Starten des Generators.
     */
    private JButton btExecuteTemplate;

    /**
     * Tabelle, in der die im aktiven Dokument enthaltenen Fehler angezeigt werden.
     */
    private JTable tbErrors;

    /**
     * Datenmodell für die Azeige der im Dokument enthaltenen Fehler.
     */
    private DefaultTableModel errorTableModel;

    /**
     * Erstell eine neue grafische Oberfläche und öffnet diese.
     */
    public CodeGenGui() {
        super("CodeGenGui");
        initContent();
        initMenuBar();
        initCloseMechanism();
        pack();
        setSize(Math.max(500, getWidth()), Math.max(350, getHeight()));
        centerOnScreen(this);
        setVisible(true);
        spMain.setDividerLocation(0.7d);
    }

    /**
     * Initialisiert den Inhaltsbereich der Oberfläche, also alle Konpinenten, die in dem Fenster dargestellt werden.
     */
    private void initContent() {
        setLayout(new BorderLayout());
        final JToolBar toolBar = createToolBar();
        add(toolBar, BorderLayout.PAGE_START);

        final JPanel pnStatus = new JPanel();
        pnStatus.setLayout(new GridBagLayout());
        lbPosition = new JLabel(" ");
        lbPosition.setBorder(BorderFactory.createLoweredBevelBorder());
        pnStatus.add(lbPosition, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        editorTabs = new EditorTabs();
        editorTabs.addCaretPositionChangeListener(this::caretPositionChanged);
        editorTabs.addErrorListener(this::errorsChanged);
        final JTabbedPane tpBottom = new JTabbedPane();
        spMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorTabs, tpBottom);
        errorTableModel = new DefaultTableModel();
        errorTableModel.setColumnCount(3);
        errorTableModel.setColumnIdentifiers(new String[] { "Zeile", "Spalte", "Problem" });
        tbErrors = new JTable(errorTableModel);
        final JScrollPane spErrors = new JScrollPane(tbErrors);
        tpBottom.addTab("Errors", spErrors);

        add(pnStatus, BorderLayout.SOUTH);
        add(spMain, BorderLayout.CENTER);
        editorTabs.addTabListener(this);
    }

    /**
     * Wird aufgerufen, wenn sich die Cursor-Position innerhalb des aktifen Editor-Fensters geändert hat. Aktualisiert
     * die Anzeige der Cursorposition unterhalb des Editors.
     * 
     * @param event
     *            Event, welches über die neue Position informiert.
     */
    public void caretPositionChanged(final CaretPositionChangeEvent event) {
        lbPosition.setText(event.getPosition() == null ? " " : event.getPosition().toShortString());
    }

    /**
     * Erstellt die Toolbar mit den darin enhaltenen Aktionschaltflächen.
     * 
     * @return Neu erstellte Toolbar.
     */
    private JToolBar createToolBar() {
        final JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setFloatable(false);
        btNewTemplate = createToolBarButton(new NewTemplateAction(this, true));
        btSaveTemplate = createToolBarButton(new SaveTemplateAction(this, true));
        btSaveTemplateAs = createToolBarButton(new SaveTemplateAsAction(this, true));

        btInsertBraces = createToolBarButton(new InsertDoubleAngleQuotationMarksAction(this));
        btExecuteTemplate = createToolBarButton(new GenerateAction(this));

        toolBar.add(btNewTemplate);
        toolBar.add(btSaveTemplate);
        toolBar.add(btSaveTemplateAs);
        toolBar.add(btInsertBraces);
        toolBar.add(btExecuteTemplate);

        btSaveTemplate.setEnabled(false);
        btSaveTemplateAs.setEnabled(false);
        btInsertBraces.setEnabled(false);
        return toolBar;
    }

    /**
     * Erstellt einen Button für die Toolbar anhand der übergebenen Action.
     * 
     * @param action
     *            Action, die der Button ausführen soll.
     * @return Button mit der Action als auszuführende Akton.
     */
    private JButton createToolBarButton(final Action action) {
        final JButton button = new JButton(action);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setText(null);
        return button;
    }

    /**
     * Initialisiert die Menüleiste.
     */
    private void initMenuBar() {
        final JMenuItem miCloseApplication = new JMenuItem(new CloseApplicationAction(this));
        miNewTemplate = new JMenuItem(new NewTemplateAction(this, false));
        miLoadTemplate = new JMenuItem(new LoadTemplateAction(this, false));
        miSaveTemplate = new JMenuItem(new SaveTemplateAction(this, false));
        miSaveTemplateAs = new JMenuItem(new SaveTemplateAsAction(this, false));
        miLoadModel = new JMenuItem(new LoadModelAction(this, false));
        final JMenuItem miSettings = new JMenuItem(new SettingsAction(this));

        final JMenu fileMenu = new JMenu("Datei");
        fileMenu.setMnemonic(KeyEvent.VK_D);
        fileMenu.add(miSettings);
        fileMenu.addSeparator();
        fileMenu.add(miCloseApplication);

        final JMenu templateMenu = new JMenu("Template");
        templateMenu.setMnemonic(KeyEvent.VK_T);
        templateMenu.add(miNewTemplate);
        templateMenu.add(miLoadTemplate);
        templateMenu.add(miSaveTemplate);
        templateMenu.add(miSaveTemplateAs);

        final JMenu modelMenu = new JMenu("Model");
        modelMenu.setMnemonic(KeyEvent.VK_M);
        modelMenu.add(miLoadModel);

        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(templateMenu);
        menuBar.add(modelMenu);
        setJMenuBar(menuBar);

        miSaveTemplate.setEnabled(false);
        miSaveTemplateAs.setEnabled(false);
    }

    /**
     * Initialisiert die Aktion, die durchgeführt werden soll, wenn der Benutzer die Anwendung beenden möchte.
     */
    private void initCloseMechanism() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new CloseApplicationAction(this));
    }

    /**
     * Prüft, ob es noch ein Editor-Fenster gibt, welches noch nicht gespeicherte Änderungen enthält.
     * 
     * @return {@code true}, falls noch ein Editor geöffnet ist, in dem die Änderungen noch nicht gespeichert wurden,
     *         {@code false} falls alle Änderungen gespeichert sind.
     */
    public boolean containsUnsavedModifications() {
        return editorTabs.containsUnsavedModifications();
    }

    /**
     * Positioniert das übergebene Anwendungsfenster auf der Mitte des Bildschirms.
     * 
     * @param window
     *            Fenster, welches ausgerichtet werden soll.
     */
    public static void centerOnScreen(final Window window) {
        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final DisplayMode displayMode = graphicsEnvironment.getDefaultScreenDevice().getDisplayMode();
        window.setLocation((displayMode.getWidth() - window.getWidth()) / 2,
                (displayMode.getHeight() - window.getHeight()) / 2);
    }

    /**
     * Öffnet einen neuen, leeren Template-Editor.
     */
    public void newTemplate() {
        editorTabs.newTemplate();
    }

    /**
     * Erstellt einen Dialog zum laden oder Speichern von Dateien.
     * 
     * @param directory
     *            Verzeichnis, welches initial in dem Dialog ausgewählt sein soll.
     * @param filter
     *            Filter für die anzuzeigenden Dateien.
     * @return Vorbereiteter Dialog zur Anzeige auf dem Bildschirm.
     */
    public static JFileChooser createFileChooser(final Path directory, final FileNameExtensionFilter filter) {
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(false);
        chooser.setCurrentDirectory(directory.toFile());
        return chooser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void firstTabOpened(final TabComponent component) {
        System.out.println("firstTabOpened");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void lastTabClosed(final TabComponent component) {
        System.out.println("lastTabClosed");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabChanged(final TabComponent oldComponent, final TabComponent newComponent) {
        System.out.println("tabChanged");
        btSaveTemplate.setEnabled(newComponent != null && newComponent.hasUnsavedModifications());
        miSaveTemplate.setEnabled(newComponent != null && newComponent.hasUnsavedModifications());
        btSaveTemplateAs.setEnabled(newComponent != null);
        miSaveTemplateAs.setEnabled(newComponent != null);
        btInsertBraces.setEnabled(newComponent instanceof TemplatePanel);

        resetErrorMessages();
        final TemplatePanel templatePanel = getSelectedTemplatePanel();
        if (templatePanel != null) {
            final Map<Token, String> errors = templatePanel.getErrors();
            addErrorMessages(errors);
        }
    }

    /**
     * Wird aufgerufen, wenn bei einer Validierung innerhalb des Editors neue Fehler innerhalb des Dokuments gefunden
     * wurden. Löscht die Tabelle der angezeigten Fehler und ersetzt den Inhalt durch die neuen Meldungen.
     * 
     * @param errors
     *            Liste der aktuell im Dokument erkannten Fehler. Die Liste enthält die Token, bei denen Probleme
     *            erkannt wurden zusammen mit einer entsprechenden Beschreibung des Problems.
     */
    public void errorsChanged(final Map<Token, String> errors) {
        resetErrorMessages();
        addErrorMessages(errors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabOpened(final TabComponent component) {
        System.out.println("tabOpened");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabClosed(final TabComponent component) {
        System.out.println("tabClosed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabSaved(final TabComponent component) {
        btSaveTemplate.setEnabled(false);
        miSaveTemplate.setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabContentHasUnsavedModifications(final TabComponent component) {
        btSaveTemplate.setEnabled(true);
        miSaveTemplate.setEnabled(true);
    }

    /**
     * Gibt das zuletzte geladene, also gerade aktiv Modell zurück.
     * 
     * @return Aktuelles Modell für die Generierung.
     */
    public Object getModel() {
        return model;
    }

    /**
     * Gibt das aktuell ausgewählte Editor-Fenster zurück.
     * 
     * @return Aktuell ausgewähltes Editor-Fenster.
     */
    public TemplatePanel getSelectedTemplatePanel() {
        return editorTabs.getSelectedTemplatePanel();
    }

    /**
     * Löscht die in der Tabelle der aktuellen Fehler enthaltenen Einträge.
     */
    public void resetErrorMessages() {
        errorTableModel.setRowCount(0);
    }

    /**
     * Fügt zu der Tabelle der zu dem aktuellen Dokument angezeigten Fehlermeldungen die übergebenen Meldungen hinzu.
     * 
     * @param errors
     *            Liste von Zuordnungen von Token zu den zu ihnen zu hinterlegenden Meldungen.
     */
    public void addErrorMessages(final Map<Token, String> errors) {
        for (final Map.Entry<Token, String> entry : errors.entrySet()) {
            final Token token = entry.getKey();
            final String message = entry.getValue();
            final Position pos = new Position(token);
            errorTableModel.addRow(new Object[] { pos.getLine(), pos.getColumn(), message });
        }
    }

    /**
     * Prüft, ob das gerade aktive Editorfenster eine Template-Datei enthält.
     * 
     * @return {@code true} falls das aktive Editorfenster eine Template-Datei enthält, {@code false} falls das
     *         Editorfenster eine andere Datei enthält.
     */
    public boolean isTemplateSelected() {
        return editorTabs.isTemplateSelected();
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
        editorTabs.saveTemplate(path, charset);
    }

    /**
     * Prüft, ob das gerade aktive Dokument noch neu ist oder bereits als Datei existiert.
     * 
     * @return {@code true} falls das Dokument neu ist und noch nie gepseichert wurde, {@code false} falls das Dokument
     *         aus einer Datei gelesen oder bereits einmal gespeichert wurde.
     */
    public boolean isSelectedTemplateNew() {
        return editorTabs.isSelectedTemplateNew();
    }

    /**
     * Gibt den Namen der Datei zurück, in oder aus dem das Dokument in dem gerade aktiven Editorfenster zuletzt
     * geschrieben oder gelesen wurde.
     * 
     * @return Name der Datei, aus der das aktive Dokument stammt.
     */
    public Path getSelectedTemplatePath() {
        return editorTabs.getSelectedTemplatePath();
    }

    /**
     * Gibt das Encoding der Datei zurück, mit dem das Dokument in dem gerade aktiven Editorfenster zuletzt in eine
     * Datei geschrieben oder gelesen wurde.
     * 
     * @return Encoding der Datei, aus der das aktive Dokument stammt.
     */
    public Charset getSelectedTemplateCharset() {
        return editorTabs.getSelectedTemplateCharset();
    }

    public void setModel(final Object newModel) {
        this.model = newModel;
    }

    /**
     * Lädt den Inhalt der übergebenen Datei in das gerade aktive Editorfenster.
     * 
     * @param path
     *            Pfad zu der zu ladenden Datei.
     * @param charset
     *            Encoding, mit dem die Datei gelesen werden soll.
     */
    public void loadTemplate(final Path path, final Charset charset) {
        editorTabs.loadTemplate(path, charset);
    }

    /**
     * Fügt ein Paar der doppelten spitzen Klammern (&#x00ab;&#x00bb;) in den aktuell aktiven Editor ein.
     */
    public void insertBraces() {
        editorTabs.insertBraces();
    }
}