package de.chrlembeck.codegen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.antlr.v4.runtime.Token;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.antlr.editor.TokenStyleRepository;
import de.chrlembeck.codegen.generator.Position;
import de.chrlembeck.codegen.gui.action.CloseApplicationAction;
import de.chrlembeck.codegen.gui.action.ConfigureClasspathAction;
import de.chrlembeck.codegen.gui.action.CopyAction;
import de.chrlembeck.codegen.gui.action.CutAction;
import de.chrlembeck.codegen.gui.action.FindAction;
import de.chrlembeck.codegen.gui.action.GenerateAction;
import de.chrlembeck.codegen.gui.action.InsertDoubleAngleQuotationMarksAction;
import de.chrlembeck.codegen.gui.action.LoadModelAction;
import de.chrlembeck.codegen.gui.action.LoadModelByReflectionAction;
import de.chrlembeck.codegen.gui.action.LoadTemplateAction;
import de.chrlembeck.codegen.gui.action.NewTemplateAction;
import de.chrlembeck.codegen.gui.action.PasteAction;
import de.chrlembeck.codegen.gui.action.RedoAction;
import de.chrlembeck.codegen.gui.action.SaveTemplateAction;
import de.chrlembeck.codegen.gui.action.SaveTemplateAsAction;
import de.chrlembeck.codegen.gui.action.SettingsAction;
import de.chrlembeck.codegen.gui.action.UndoAction;
import de.chrlembeck.codegen.gui.datamodel.ModelTreeNodeUtil;
import de.chrlembeck.util.swing.SwingUtil;

/**
 * Hauptklasse der Generator-GUI.
 * 
 * @author Christoph Lembeck
 */
@SuppressWarnings("PMD.GodClass")
public class CodeGenGui extends JFrame implements TabListener {

    private static final String CLIENT_PROPERTY_OUTPUT_PANEL = "CLIENT_PROPERTY_OUTPUT_PANEL";

    /**
     * Der Logger für diese Klasse.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeGenGui.class);

    /**
     * Version number of the current class.
     * 
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 1L;

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

    private JButton btUndo;

    private JButton btRedo;

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
     * Liste der Classpath-Elemente zum Laden des Modells.
     */
    private URL[] modelClasspath = new URL[0];

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

    private JMenuItem miUndo;

    private JMenuItem miRedo;

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
     * SplitPane für die Anzeige von EditorTabs und den geladenen Modellen.
     */
    private JSplitPane spEditorModel;

    /**
     * ScrollPane für die Anzeige der geladenen Modelle.
     */
    private JScrollPane spModel;

    /**
     * TreeTable für die Anzeige der geladenen Modelle.
     */
    private JXTreeTable ttModel;

    private BasicTabbedPane tpOutput;

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
    public static void setSystemLookAndFeel() {
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
     * Erstell eine neue grafische Oberfläche und öffnet diese.
     */
    public CodeGenGui() {
        super("CodeGenGui");
        initContent();
        initMenuBar();
        initCloseMechanism();
        pack();
        setSize(Math.max(1200, getWidth()), Math.max(800, getHeight()));
        SwingUtil.centerToScreen(this);
        setVisible(true);
        spMain.setDividerLocation(0.7d);
        spEditorModel.setDividerLocation(0.8d);
        setIconImages(Arrays.asList(((ImageIcon) IconFactory.GEAR_16.icon()).getImage(),
                ((ImageIcon) IconFactory.GEAR_24.icon()).getImage(),
                ((ImageIcon) IconFactory.GEAR_32.icon()).getImage(),
                ((ImageIcon) IconFactory.GEAR_48.icon()).getImage()));
    }

    /**
     * Initialisiert den Inhaltsbereich der Oberfläche, also alle Konpinenten, die in dem Fenster dargestellt werden.
     */
    private void initContent() {
        setLayout(new BorderLayout());
        final JToolBar toolBar = createToolBar();
        add(toolBar, BorderLayout.PAGE_START);

        // editor tabs
        editorTabs = new EditorTabs();
        editorTabs.addCaretPositionChangeListener(this::caretPositionChanged);
        editorTabs.addErrorListener(this::errorsChanged);
        editorTabs.addTabListener(this);
        editorTabs.addUndoableEditListener(this::undoableEditHappened);

        // Model Tree-Table
        ttModel = new JXTreeTable(createModelTreeModel());
        ttModel.setRootVisible(true);
        spModel = new JScrollPane(ttModel);

        // Ausgabebereich für die Fehlermeldungen
        errorTableModel = new DefaultTableModel();
        errorTableModel.setColumnCount(3);
        errorTableModel.setColumnIdentifiers(new String[] { "Zeile", "Spalte", "Problem" });
        tbErrors = new JTable(errorTableModel);
        final JScrollPane spErrors = new JScrollPane(tbErrors);

        tpOutput = new BasicTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        tpOutput.addTab("Errors", spErrors);

        // Panel für die Statuszeile
        final JPanel pnStatus = new JPanel();
        pnStatus.setLayout(new GridBagLayout());
        lbPosition = new JLabel(" ");
        lbPosition.setBorder(BorderFactory.createLoweredBevelBorder());
        pnStatus.add(lbPosition, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        // SplitPane für Editoren und Modelle
        spEditorModel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorTabs, spModel);

        // SplitPane für Editoren und Modelle oben und Ausgabebereich unten
        spMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spEditorModel, tpOutput);

        add(pnStatus, BorderLayout.SOUTH);
        add(spMain, BorderLayout.CENTER);
    }

    /**
     * Erstellt das Datenmodell für die TreeTable zur Darstellung der geladenen Modelle.
     * 
     * @return Datenmodell für die Darstellung der geladenen Modelle.
     */
    private DefaultTreeTableModel createModelTreeModel() {
        final DefaultTreeTableModel model = new DefaultTreeTableModel();
        model.setColumnIdentifiers(Arrays.asList("Name", "Typ", "Inhalt"));
        return model;
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
        final JButton btOpenTemplate = createToolBarButton(new LoadTemplateAction(this, true));
        btSaveTemplate = createToolBarButton(new SaveTemplateAction(this, true));
        btSaveTemplateAs = createToolBarButton(new SaveTemplateAsAction(this, true));
        btUndo = createToolBarButton(new UndoAction(this));
        btRedo = createToolBarButton(new RedoAction(this));
        final JButton btFind = createToolBarButton(new FindAction(this));
        final JButton btCut = createToolBarButton(new CutAction(this));
        final JButton btCopy = createToolBarButton(new CopyAction(this));
        final JButton btPaste = createToolBarButton(new PasteAction(this));

        btInsertBraces = createToolBarButton(new InsertDoubleAngleQuotationMarksAction(this));
        btExecuteTemplate = createToolBarButton(new GenerateAction(this));

        toolBar.add(btNewTemplate);
        toolBar.add(btOpenTemplate);
        toolBar.add(btSaveTemplate);
        toolBar.add(btSaveTemplateAs);
        toolBar.addSeparator();
        toolBar.add(btUndo);
        toolBar.add(btRedo);
        toolBar.addSeparator();
        toolBar.add(btCut);
        toolBar.add(btCopy);
        toolBar.add(btPaste);
        toolBar.addSeparator();
        toolBar.add(btFind);
        toolBar.add(btInsertBraces);
        toolBar.add(btExecuteTemplate);

        btSaveTemplate.setEnabled(false);
        btSaveTemplateAs.setEnabled(false);
        btInsertBraces.setEnabled(false);
        btUndo.setEnabled(false);
        btRedo.setEnabled(false);
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
        button.setFocusable(false);
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
        miUndo = new JMenuItem(new UndoAction(this));
        miRedo = new JMenuItem(new RedoAction(this));
        final JMenuItem miFind = new JMenuItem(new FindAction(this));
        final JMenuItem miCut = new JMenuItem(new CutAction(this));
        final JMenuItem miCopy = new JMenuItem(new CopyAction(this));
        final JMenuItem miPaste = new JMenuItem(new PasteAction(this));
        final JMenuItem miSettings = new JMenuItem(new SettingsAction(this));

        miLoadModel = new JMenuItem(new LoadModelAction(this, false));
        final JMenuItem miConfigureModelClasspath = new JMenuItem(new ConfigureClasspathAction(this));
        final JMenuItem miLoadModelReflection = new JMenuItem(new LoadModelByReflectionAction(this));

        final JMenu fileMenu = new JMenu("Datei");
        fileMenu.setMnemonic(KeyEvent.VK_D);
        fileMenu.add(miSettings);
        fileMenu.addSeparator();
        fileMenu.add(miCloseApplication);

        final JMenuItem editMenu = new JMenu("Bearbeiten");
        editMenu.setMnemonic(KeyEvent.VK_B);
        editMenu.add(miUndo);
        editMenu.add(miRedo);
        editMenu.add(new JSeparator());
        editMenu.add(miFind);
        editMenu.add(new JSeparator());
        editMenu.add(miCut);
        editMenu.add(miCopy);
        editMenu.add(miPaste);

        final JMenu templateMenu = new JMenu("Template");
        templateMenu.setMnemonic(KeyEvent.VK_T);
        templateMenu.add(miNewTemplate);
        templateMenu.add(miLoadTemplate);
        templateMenu.add(miSaveTemplate);
        templateMenu.add(miSaveTemplateAs);

        final JMenu modelMenu = new JMenu("Model");
        modelMenu.setMnemonic(KeyEvent.VK_M);
        modelMenu.add(miLoadModel);
        modelMenu.add(miLoadModelReflection);
        modelMenu.addSeparator();
        modelMenu.add(miConfigureModelClasspath);

        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(templateMenu);
        menuBar.add(modelMenu);
        setJMenuBar(menuBar);

        miSaveTemplate.setEnabled(false);
        miSaveTemplateAs.setEnabled(false);
        miUndo.setEnabled(false);
        miRedo.setEnabled(false);
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
    public static JFileChooser createFileChooser(final Path directory, final FileNameExtensionFilter filter,
            final int fileSelectionMode) {
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(fileSelectionMode);
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
    public void tabChanged(final Component oldComponent, final Component newComponent) {
        System.out.println("tabChanged");
        final boolean saveEnabled = newComponent instanceof TabComponent
                && ((TabComponent) newComponent).hasUnsavedModifications();
        btSaveTemplate.setEnabled(saveEnabled);
        miSaveTemplate.setEnabled(saveEnabled);
        btSaveTemplateAs.setEnabled(newComponent instanceof TabComponent);
        miSaveTemplateAs.setEnabled(newComponent instanceof TabComponent);
        btInsertBraces.setEnabled(newComponent instanceof TemplatePanel);

        resetErrorMessages();
        final TabComponent comp = getSelectedDocument();
        if (comp instanceof TemplatePanel) {
            final TemplatePanel templatePanel = (TemplatePanel) comp;
            final Map<Token, String> errors = templatePanel.getErrors();
            addErrorMessages(errors);
        }
        updateUndoRedoState();
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
    public TabComponent getSelectedDocument() {
        return editorTabs.getSelectedDocument();
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
     * Prüft, ob aktuell ein Dokument geöffnet ist.
     * 
     * @return {@code true} falls ein Tab ausgewählt ist, {@code false} falls kein Tab ausgewählt ist.
     */
    public boolean isDocumentSelected() {
        return editorTabs.isDocumentSelected();
    }

    /**
     * Speichert das in dem aktuell aktiven Editorfenster angezeigt Dokument auf die Festplatte.
     * 
     * @param path
     *            Pfad zu der Datei, in die das Dokument gespciehert werden soll.
     * @param charset
     *            Encoding zum Speichern der Datei.
     */
    public void saveDocument(final Path path, final Charset charset) {
        editorTabs.saveDocument(path, charset);
    }

    /**
     * Prüft, ob das gerade aktive Dokument noch neu ist oder bereits als Datei existiert.
     * 
     * @return {@code true} falls das Dokument neu ist und noch nie gepseichert wurde, {@code false} falls das Dokument
     *         aus einer Datei gelesen oder bereits einmal gespeichert wurde.
     */
    public boolean isSelectedDocumentNew() {
        return editorTabs.isSelectedDocumentNew();
    }

    /**
     * Gibt den Namen der Datei zurück, in oder aus dem das Dokument in dem gerade aktiven Editorfenster zuletzt
     * geschrieben oder gelesen wurde.
     * 
     * @return Name der Datei, aus der das aktive Dokument stammt.
     */
    public Path getSelectedDocumentPath() {
        return editorTabs.getSelectedDocumentPath();
    }

    /**
     * Gibt das Encoding der Datei zurück, mit dem das Dokument in dem gerade aktiven Editorfenster zuletzt in eine
     * Datei geschrieben oder gelesen wurde.
     * 
     * @return Encoding der Datei, aus der das aktive Dokument stammt.
     */
    public Charset getSelectedTemplateCharset() {
        return editorTabs.getSelectedDocumentCharset();
    }

    public TabComponent getSelectedTabComponent() {
        return editorTabs.getSelectedDocument();
    }

    /**
     * Ersetzt das für die Generierung zu verwendende Modell durch ein neues Modell.
     * 
     * @param name
     *            Name des neuen Modells
     * @param newModel
     *            Neues Modell für die Generierung.
     */
    public void setModel(final String name, final Object newModel) {
        this.model = newModel;
        final ModelTreeNodeUtil nodeUtil = new ModelTreeNodeUtil();
        final TreeTableNode rootNode = nodeUtil.createRootNode(name, newModel);
        final DefaultTreeTableModel model = getModelTreeTableModel();
        model.setRoot(rootNode);
    }

    /**
     * Gibt das aktuelle Datenmodell der TreeTable zurück, die zur Anzeige der geladenen Modelle verwendet wird.
     * 
     * @return Datenmodell der Modell-TreeTable.
     */
    private DefaultTreeTableModel getModelTreeTableModel() {
        return (DefaultTreeTableModel) ttModel.getTreeTableModel();
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

    public void showErrorMessage(final String message, final String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE, IconFactory.ERROR_32.icon());
    }

    public void removeAllOutputPanels() {
        for (int i = tpOutput.getTabCount() - 1; i >= 0; i--) {
            final Component comp = tpOutput.getComponentAt(i);
            if (comp instanceof JComponent) {
                final JComponent jComponent = (JComponent) comp;
                if (jComponent.getClientProperty(CLIENT_PROPERTY_OUTPUT_PANEL) == Boolean.TRUE) {
                    tpOutput.removeTabAt(i);
                }
            }
        }
    }

    public JTextComponent createOutputPanel(final String name) {
        final JTextArea taOutput = new JTextArea();
        final UnmodifiableTabComponent tabComponent = new UnmodifiableTabComponent(taOutput);
        tabComponent.putClientProperty(CLIENT_PROPERTY_OUTPUT_PANEL, Boolean.TRUE);

        tpOutput.addTabComponent(tabComponent, name, true);
        taOutput.addCaretListener(this::outputCaretChanged);

        return taOutput;
    }

    public void outputCaretChanged(final CaretEvent event) {
        final JTextComponent textComp = (JTextComponent) event.getSource();
        final Element rootElement = textComp.getDocument().getDefaultRootElement();
        final int line = rootElement.getElementIndex(event.getDot());
        final Element element = rootElement.getElement(line);
        final int column = event.getDot() - element.getStartOffset();
        lbPosition.setText(new Position(line + 1, column + 1).toShortString());
    }

    public void updateUndoRedoState() {
        final TabComponent selectedDocument = editorTabs.getSelectedDocument();
        if (selectedDocument instanceof TemplatePanel) {
            final TemplatePanel templatePanel = (TemplatePanel) selectedDocument;
            final TemplateEditorPane editorPane = templatePanel.getEditorPane();
            final boolean canUndo = editorPane.canUndo();
            miUndo.setEnabled(canUndo);
            btUndo.setEnabled(canUndo);
            final boolean canRedo = editorPane.canRedo();
            miRedo.setEnabled(canRedo);
            btRedo.setEnabled(canRedo);
        }
    }

    public void undoableEditHappened(final UndoableEditEvent editEvent) {
        updateUndoRedoState();
    }

    public TokenStyleRepository getTokenStyles() {
        return editorTabs.getTokenStyles();
    }

    /**
     * Gibt die Liste der Classpath-Elemente zum Laden des Modells zurück.
     * 
     * @return Liste der Classpath-Elemente zum Laden des Modells.
     */
    public URL[] getModelClasspath() {
        return modelClasspath;
    }

    public void setModelClasspath(final URL[] modelClasspath) {
        this.modelClasspath = modelClasspath;
    }

    public ClassLoader getModelClassLoader() {
        return new URLClassLoader(getModelClasspath());
    }

    public void updateTokenStyles(final TokenStyleRepository newTokenStyles) {
        editorTabs.updateTokenStyles(newTokenStyles);
    }
}