package com.beanloaf.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.undo.UndoManager;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.beanloaf.common.TC;
import com.beanloaf.objects.ListTab;
import com.beanloaf.objects.Settings;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.shared.CheckForFolders;
import com.beanloaf.shared.TextAreaMouseListener;
import com.beanloaf.tMainEventHandlers.FileActionButtonPressed;
import com.beanloaf.tMainEventHandlers.KeyChange;
import com.beanloaf.tMainEventHandlers.ListItemPressed;
import com.beanloaf.tMainEventHandlers.ListTabPressed;

/**
 * 
 * @author beanloaf
 */
public class ThoughtsMain {

    public ThoughtObject selectedFile;

    public ArrayList<File> unsortedFiles = new ArrayList<>();
    public ArrayList<File> sortedFiles = new ArrayList<>();
    public ArrayList<ThoughtObject> unsortedThoughtList = new ArrayList<>();
    public ArrayList<ThoughtObject> sortedThoughtList = new ArrayList<>();

    public JFrame window;
    public JPanel container;

    /* Input Fields */
    public JLabel dateLabel;

    /* Right Panel Input Fields */
    public JTextArea titleLabel, tagLabel;
    public JTextArea textContentArea;
    public UndoManager undo = new UndoManager();
    public JButton sortButton, deleteButton, newFileButton;
    /* Right Panel Ghost Text */
    public JLabel emptyTitle, emptyTag, emptyBody;

    /* Left Panel */
    public JSplitPane splitPane;

    public JPanel leftPanel;
    public JTabbedPane leftTabs;
    public DefaultListModel<String> unsortedListModel = new DefaultListModel<>();
    public DefaultListModel<String> sortedListModel = new DefaultListModel<>();
    public ListTab unsortedListLabel, sortedListLabel;
    public ArrayList<String> tagList = new ArrayList<>();

    public boolean ready = false;

    /**
     * Number of tags on the displayed
     * 
     * Default to 2 for sorted/unsorted
     */
    public int numTags = 2;

    /**
     * Default size of each tag label
     */
    public Dimension tagDim = new Dimension(150, 25);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }

        new ThoughtsMain();

    }

    public ThoughtsMain() {
        createGUI();
        this.window.setLocationRelativeTo(null);
        this.window.setVisible(true);
        onStartUp();

    }

    private void onStartUp() {
        new CheckForFolders().createDataFolder();
        refreshThoughtList();

        if (this.unsortedThoughtList.size() > 0) {
            new ListItemPressed(this,
                    this.unsortedListLabel,
                    this.unsortedThoughtList).setContentFields(0);
        }

        this.ready = true;
    }

    private void createGUI() {

        this.window = new JFrame("Thoughts");
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setFocusable(true);

        // open the window in full screen by default
        // this.window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.window.setSize(1650, 1080);
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyBinds());

        this.container = new JPanel();
        this.container.setLayout(new GridBagLayout());
        this.container.setBackground(Color.darkGray);

        this.window.add(this.container);

        createTopPanel();
        createCenterPanel();
        createLeftPanel();
        createRightPanel();
        createBottomPanel();

    }

    private void createTopPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 0.1;
        c.weighty = 0.12;
        c.fill = GridBagConstraints.BOTH;

        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        topPanel.setBackground(Color.DARK_GRAY);

        JButton testButton = new JButton("test");
        topPanel.add(testButton);
        testButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });

        this.container.add(topPanel, c);
    }

    private void createBottomPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weightx = 0.1;
        c.weighty = 0.12;
        c.fill = GridBagConstraints.BOTH;

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        bottomPanel.setBackground(Color.DARK_GRAY);

        JButton openSettingsButton = new JButton("Settings");
        openSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Settings();

            }
        });
        bottomPanel.add(openSettingsButton);

        this.container.add(bottomPanel, c);
    }

    private void createCenterPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0.1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;

        UIManager.put("SplitPaneDivider.draggingColor", Color.gray);

        this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.splitPane.resetToPreferredSizes();
        this.splitPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        this.splitPane.setDividerSize(5);
        this.splitPane.setBackground(Color.darkGray);
        this.splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    public void setBorder(Border b) {
                    }

                    @Override
                    public void paint(Graphics g) {
                        g.setColor(Color.gray);
                        g.fillRect(0, 0, getSize().width, getSize().height);
                        super.paint(g);
                    }
                };
            }

        });

        this.container.add(splitPane, c);
    }

    private void createLeftPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;

        this.leftPanel = new JPanel();
        this.leftPanel.setBackground(Color.darkGray);
        this.leftPanel.setLayout(new GridBagLayout());
        this.leftPanel.setPreferredSize(new Dimension(450, 0));
        this.leftPanel.setMinimumSize(new Dimension(0, 0));
        this.splitPane.setLeftComponent(leftPanel);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(TC.h5);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setBackground(Color.LIGHT_GRAY);
        refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                refreshThoughtList();
            }
        });
        leftPanel.add(refreshButton);

        UIManager.put("TabbedPane.contentBorderInsets",
                new Insets(-2, -2, -2, -2));

        this.leftTabs = new JTabbedPane(JTabbedPane.LEFT);
        this.leftTabs.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        this.leftTabs.setFont(TC.h4);
        this.leftTabs.setBackground(Color.LIGHT_GRAY);
        this.leftTabs.addMouseListener(new ListTabPressed(this));
        this.leftTabs.setPreferredSize(new Dimension(200, 200));
        this.leftPanel.add(this.leftTabs, c);
        createUnsortedTab();
        createSortedTab();

    }

    private void createUnsortedTab() {
        GridBagConstraints cc = new GridBagConstraints();

        JPanel unsortedPanel = new JPanel();
        unsortedPanel.setBackground(Color.DARK_GRAY);
        unsortedPanel.setLayout(new GridBagLayout());

        this.leftTabs.add(createScrollView(unsortedPanel),
                "Unsorted");

        JLabel tabLabel = new JLabel("Unsorted", SwingConstants.CENTER);
        tabLabel.setFont(TC.h4);
        tabLabel.setPreferredSize(tagDim);
        leftTabs.setTabComponentAt(0, tabLabel);

        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1;
        cc.weighty = 0.1;
        cc.gridx = 0;

        JLabel unsortedLabel = new JLabel("Unsorted");
        unsortedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        unsortedLabel.setFont(TC.h3);
        unsortedLabel.setForeground(Color.white);
        cc.gridy = 0;
        cc.weighty = 0.1;
        cc.anchor = GridBagConstraints.NORTH;
        unsortedPanel.add(unsortedLabel, cc);

        JPanel listContainer = new JPanel();
        listContainer.setOpaque(false);
        listContainer.setLayout(new BorderLayout());
        cc.gridy = 1;
        cc.weighty = 1;
        unsortedPanel.add(listContainer, cc);

        unsortedListLabel = new ListTab(this, unsortedThoughtList, unsortedListModel);
        listContainer.add(unsortedListLabel, BorderLayout.CENTER);
    }

    private void createSortedTab() {
        GridBagConstraints cc = new GridBagConstraints();

        JPanel sortedPanel = new JPanel();
        sortedPanel.setBackground(Color.DARK_GRAY);
        sortedPanel.setLayout(new GridBagLayout());
        leftTabs.add(createScrollView(sortedPanel), "Sorted");

        JLabel tabLabel = new JLabel("Sorted", SwingConstants.CENTER);
        tabLabel.setFont(TC.h4);
        tabLabel.setPreferredSize(tagDim);
        leftTabs.setTabComponentAt(1, tabLabel);

        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1;
        cc.weighty = 0.1;
        cc.gridx = 0;

        JLabel sortedLabel = new JLabel("Sorted");
        sortedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sortedLabel.setFont(TC.h3);
        sortedLabel.setForeground(Color.white);
        cc.gridy = 0;
        cc.weighty = 0.1;
        cc.anchor = GridBagConstraints.NORTH;
        sortedPanel.add(sortedLabel, cc);

        JPanel listContainer = new JPanel();
        listContainer.setOpaque(false);
        listContainer.setLayout(new BorderLayout());
        cc.gridy = 1;
        cc.weighty = 1;
        sortedPanel.add(listContainer, cc);

        sortedListLabel = new ListTab(this, sortedThoughtList, sortedListModel);
        listContainer.add(sortedListLabel, BorderLayout.CENTER);

    }

    private void createTagTabs(DefaultListModel<String> model,
            ArrayList<ThoughtObject> arrayList, String tagName) {

        GridBagConstraints cc = new GridBagConstraints();

        JPanel tagPanel = new JPanel();
        tagPanel.setBackground(Color.DARK_GRAY);
        tagPanel.setLayout(new GridBagLayout());
        leftTabs.add(createScrollView(tagPanel), tagName);

        JLabel tabLabel = new JLabel(tagName, SwingConstants.CENTER);
        tabLabel.setFont(TC.h4);
        tabLabel.setPreferredSize(tagDim);
        leftTabs.setTabComponentAt(numTags, tabLabel);
        numTags++;

        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1;
        cc.weighty = 0.1;
        cc.gridx = 0;

        JLabel tagNameLabel = new JLabel(tagName);
        tagNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tagNameLabel.setFont(TC.h3);
        tagNameLabel.setForeground(Color.white);

        cc.gridy = 0;
        cc.weighty = 0.1;
        cc.anchor = GridBagConstraints.NORTH;
        tagPanel.add(tagNameLabel, cc);

        JPanel listContainer = new JPanel();
        listContainer.setOpaque(false);
        listContainer.setLayout(new BorderLayout());
        cc.gridy = 1;
        cc.weighty = 1;
        tagPanel.add(listContainer, cc);

        ListTab tagListLabel = new ListTab(this, arrayList, model);
        listContainer.add(tagListLabel, BorderLayout.CENTER);

    }

    private JScrollPane createScrollView(JPanel panel) {
        JScrollPane scroll = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);

        scroll.getVerticalScrollBar().setBackground(Color.lightGray);
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.gray;
            }
        });
        return scroll;
    }

    private void createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.DARK_GRAY);
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(750, 0));
        rightPanel.setMinimumSize(new Dimension(0, 0));
        splitPane.setRightComponent(rightPanel);

        /* Top */
        GridBagConstraints topc = new GridBagConstraints();
        JPanel topLabel = new JPanel();
        topLabel.setBackground(Color.DARK_GRAY);
        topLabel.setLayout(new GridBagLayout());
        topc.gridx = 0;
        topc.gridy = 0;
        topc.weightx = 0.1;
        topc.weighty = 0.1;
        topc.insets = new Insets(10, 10, 0, 0);
        topc.anchor = GridBagConstraints.WEST;
        rightPanel.add(topLabel, topc);

        GridBagConstraints cc = new GridBagConstraints();
        cc.anchor = GridBagConstraints.LINE_START;

        titleLabel = new JTextArea(TC.DEFAULT_TITLE);
        titleLabel.setColumns(6);
        titleLabel.setOpaque(false);
        titleLabel.setForeground(Color.white);
        titleLabel.setFont(TC.h1);
        titleLabel.getDocument().addDocumentListener(new KeyChange(this));
        titleLabel.getDocument().putProperty("labelType", titleLabel);
        titleLabel.setName("titleLabel");
        titleLabel.addMouseListener(new TextAreaMouseListener());
        titleLabel.setCaretColor(Color.white);
        titleLabel.getDocument().putProperty("filterNewlines", true);
        titleLabel.getDocument().addUndoableEditListener(undo);
        titleLabel.setLayout(new GridBagLayout());

        GridBagConstraints et = new GridBagConstraints();
        et.weightx = 0.1;
        et.weighty = 0.1;
        et.anchor = GridBagConstraints.NORTHWEST;
        emptyTitle = new JLabel("");
        emptyTitle.setForeground(Color.white);
        emptyTitle.setFont(TC.h1);
        emptyTitle.setOpaque(false);
        emptyTitle.setEnabled(false);
        titleLabel.add(emptyTitle, et);

        cc.gridx = 0;
        cc.gridy = 0;
        topLabel.add(titleLabel, cc);

        tagLabel = new JTextArea(TC.DEFAULT_TAG);
        tagLabel.setColumns(9);
        tagLabel.setForeground(new Color(220, 220, 220));
        tagLabel.setOpaque(false);
        tagLabel.setFont(TC.h3);
        tagLabel.getDocument().addDocumentListener(new KeyChange(this));
        tagLabel.getDocument().putProperty("labelType", tagLabel);
        tagLabel.setName("tagLabel");
        tagLabel.addMouseListener(new TextAreaMouseListener());
        tagLabel.setCaretColor(Color.white);
        tagLabel.getDocument().putProperty("filterNewlines", true);
        tagLabel.getDocument().addUndoableEditListener(undo);
        tagLabel.setLayout(new GridBagLayout());
        cc.gridx = 0;
        cc.gridy = 1;

        // Ghost text for the tag
        emptyTag = new JLabel("");
        emptyTag.setForeground(Color.white);
        emptyTag.setFont(TC.h3);
        emptyTag.setOpaque(false);
        emptyTag.setEnabled(false);
        tagLabel.add(emptyTag, et);
        topLabel.add(tagLabel, cc);

        dateLabel = new JLabel("Created on: " + TC.DEFAULT_DATE);
        dateLabel.setForeground(new Color(220, 220, 220));
        dateLabel.setFont(TC.h4);
        cc.gridx = 0;
        cc.gridy = 2;
        cc.insets = new Insets(10, 0, 0, 0);
        topLabel.add(dateLabel, cc);

        /* Bottom */
        GridBagConstraints botc = new GridBagConstraints();

        textContentArea = new JTextArea(TC.DEFAULT_BODY);
        textContentArea.setBackground(new Color(32, 32, 32));
        textContentArea.setForeground(Color.white);
        textContentArea.setFont(TC.p);
        textContentArea.setLineWrap(true);
        textContentArea.setWrapStyleWord(true);
        textContentArea.getDocument().addDocumentListener(new KeyChange(this));
        textContentArea.getDocument().putProperty("labelType", textContentArea);
        textContentArea.setName("textContentArea");
        textContentArea.setPreferredSize(new Dimension(0, 0));
        textContentArea.addMouseListener(new TextAreaMouseListener());
        textContentArea.getDocument().addUndoableEditListener(undo);
        textContentArea.setCaretColor(Color.white);
        textContentArea.setLayout(new GridBagLayout());
        botc.weightx = 0.1;
        botc.weighty = 0.9;
        botc.gridx = 0;
        botc.gridy = 1;
        botc.insets = new Insets(5, 5, 5, 5);
        botc.fill = GridBagConstraints.BOTH;

        // Ghost text for body
        emptyBody = new JLabel("");
        emptyBody.setForeground(Color.white);
        emptyBody.setFont(TC.p);
        emptyBody.setOpaque(false);
        emptyBody.setEnabled(false);
        textContentArea.add(emptyBody, et);
        rightPanel.add(textContentArea, botc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        botc.weightx = 0.3;
        botc.weighty = 0.05;
        botc.gridx = 0;
        botc.gridy = 2;
        rightPanel.add(buttonPanel, botc);

        GridBagConstraints bc = new GridBagConstraints();
        bc.weightx = 0.1;
        bc.weighty = 0.1;
        bc.fill = GridBagConstraints.BOTH;

        sortButton = new JButton("Sort/Unsort");
        sortButton.setName("sort");
        sortButton.addActionListener(new FileActionButtonPressed(this));
        buttonPanel.add(sortButton, bc);

        deleteButton = new JButton("Delete");
        deleteButton.setName("delete");
        deleteButton.addActionListener(new FileActionButtonPressed(this));
        buttonPanel.add(deleteButton, bc);

        newFileButton = new JButton("New File");
        newFileButton.setName("newFile");
        newFileButton.addActionListener(new FileActionButtonPressed(this));
        buttonPanel.add(newFileButton, bc);

    }

    private ThoughtObject readFileContents(File filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JSONObject json = (JSONObject) new JSONParser().parse(reader);
            reader.close();
            return new ThoughtObject(
                    json.get("title").toString().trim(),
                    json.get("date").toString().trim(),
                    json.get("tag").toString().trim(),
                    json.get("body").toString().trim(),
                    filePath);
        } catch (Exception e) {
            System.err.println(
                    String.format(
                            "Found invalid file '%s'.", filePath.toPath()));
        }
        return null;
    }

    public void refreshThoughtList() {
        File[] unsortedFileDirectory = TC.UNSORTED_DIRECTORY_PATH.listFiles();
        File[] sortedFileDirectory = TC.SORTED_DIRECTORY_PATH.listFiles();
        // Resets number of tags to 2
        this.numTags = 2;

        // Stores currently selected tab
        final int selectedTab = leftTabs.getSelectedIndex();

        leftTabs.removeAll();

        // Resets all models and lists
        unsortedThoughtList.clear();
        sortedThoughtList.clear();
        unsortedFiles.clear();
        sortedFiles.clear();
        unsortedListModel.clear();
        sortedListModel.clear();
        tagList.clear();

        /* UNSORTED FILES */
        unsortedFiles.addAll(Arrays.asList(unsortedFileDirectory));
        for (int i = 0; i < unsortedFiles.size(); i++) {
            ThoughtObject content = readFileContents(unsortedFiles.get(i));
            if (content != null) {
                unsortedThoughtList.add(content);
                unsortedListModel.addElement(content.getTitle());
            }
        }

        /* SORTED FILES */
        sortedFiles.addAll(Arrays.asList(sortedFileDirectory));
        for (int i = 0; i < sortedFiles.size(); i++) {
            ThoughtObject content = readFileContents(sortedFiles.get(i));
            if (content != null) {
                sortedThoughtList.add(content);
                sortedListModel.addElement(content.getTitle());
            }
        }
        createUnsortedTab();
        createSortedTab();
        setTagModel();

        try {
            leftTabs.setSelectedIndex(selectedTab);
        } catch (Exception e) {
            leftTabs.setSelectedIndex(selectedTab - 1);
        }
    }

    public void setTagModel() {
        ArrayList<String> tagNames = new ArrayList<>();
        for (int i = 0; i < sortedFiles.size(); i++) {
            ThoughtObject content = readFileContents(sortedFiles.get(i));

            if (content != null) {
                if (!tagNames.contains(content.getTag())) {
                    tagNames.add(content.getTag());
                }
            }

        }
        tagNames.sort(String::compareToIgnoreCase);
        for (int i = 0; i < tagNames.size(); i++) {
            DefaultListModel<String> tempTagModel = new DefaultListModel<>();
            ArrayList<ThoughtObject> thoughtObjectList = new ArrayList<>();
            for (int j = 0; j < sortedFiles.size(); j++) {
                ThoughtObject content = readFileContents(sortedFiles.get(j));
                if (content != null && content.getTag().equals(tagNames.get(i))) {
                    tempTagModel.addElement(content.getTitle());
                    thoughtObjectList.add(content);
                }
            }
            createTagTabs(tempTagModel, thoughtObjectList, tagNames.get(i));
        }
    }

    public void selectTitle() {
        this.titleLabel.requestFocusInWindow();
        this.titleLabel.selectAll();
    }

    public class KeyBinds implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            // 401 is key-down
            if (e.getID() != 401) {
                return false;
            }
            int key = e.getKeyCode();
            boolean c = e.isControlDown();

            switch (key) {
                case KeyEvent.VK_Z: // Undo
                    if (c) {
                        try {
                            undo.undo();
                        } catch (Exception err) {
                        }
                    }
                    break;

                case KeyEvent.VK_Y: // Redo
                    if (c) {
                        try {
                            undo.redo();
                        } catch (Exception err) {
                        }
                    }
                    break;

                case KeyEvent.VK_N: // New File
                    if (c) {
                        newFileButton.doClick();
                    }
                    break;

                case KeyEvent.VK_D: // Delete File
                    if (c) {
                        deleteButton.doClick();
                    }
                    break;

                case KeyEvent.VK_Q: // Sort File
                    if (c) {
                        sortButton.doClick();
                    }
                    break;
            }

            return false;
        }

    }

    public class CellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {

            JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            c.setBackground(Color.black);
            c.setForeground(Color.white);
            c.setHorizontalAlignment(JLabel.CENTER);
            c.setPreferredSize(new Dimension(25, 25));
            return c;
        }
    }

}