/**
 * Copyright 2009 Jitendra Rana, jsrana@gmail.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jsrana.plugins.quicknotes.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import com.jsrana.plugins.quicknotes.util.StringUtil;
import org.jdom.Element;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Quick Notes Panel
 *
 * @author Jitendra Rana
 */
public class QuickNotesPanel {
    private String id;
    private JPanel panel1;
    private JTextArea pane;
    private JScrollPane noteScroller;
    private JPanel searchPanel;
    private JTextField searchField;
    private JButton buttonHideSearch;
    private JLabel labelNoteTitle;
    private JLabel labelIndex;
    private JPanel actionPanel;
    public Element element;
    private int selectedIndex;
    private Element selectedNote;
    private QuickNotesManager quickNotesManager;
    private boolean listAllNodes = false;
    private int caretPosition = 0;

    public static final Color EDITOR_COLOR_FONT = Gray._0;
    public static final Color EDITOR_COLOR_BACKGROUND = new JBColor(new Color(254, 252, 219), new Color(254, 252, 219));
    public static final Color EDITOR_COLOR_LINE = new JBColor(new Color(234, 233, 164), new Color(234, 233, 164));
    public static final Color EDITOR_COLOR_LINENUMBER = new JBColor(new Color(189, 183, 107), new Color(189, 183, 107));
    private static final Insets EDITOR_INSET = JBUI.insetsLeft(25);
    private static final Insets EDITOR_INSET_LINENUMBER = JBUI.insetsLeft(35);
    private static final Insets EDITOR_INSET_LINENUMBER_1000 = JBUI.insetsLeft(38);

    public static SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy h:mm a");
    public static SimpleDateFormat titleFormat = new SimpleDateFormat("MMM d, yyyy h:mm a");

    private boolean showBackgroundLines = true;

    private JEditorPane searchPane;

    /**
     * Constructor
     *
     * @param element Element
     */
    public QuickNotesPanel(final Element element) {
        quickNotesManager = QuickNotesManager.getInstance();
        id = quickNotesManager.getNextPanelID();
        this.element = element;

        final ActionManager actionManager = ActionManager.getInstance();
        final DefaultActionGroup dag = new DefaultActionGroup();

        dag.addSeparator();
        dag.add(new AnAction("Previous Note", "Previous Note", QuickNotesIcon.ARROW_LEFT) {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                goBack();
            }
        });
        dag.add(new AnAction("Next Note", "Next Note", QuickNotesIcon.ARROW_RIGHT) {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                goNext();
            }
        });
        dag.addSeparator();
        dag.add(new AnAction("Add Note", "Add Note", QuickNotesIcon.ADD) {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                addNewNote("Enter your notes here...");
            }
        });
        dag.add(new AnAction("Delete Note", "Delete Note", QuickNotesIcon.TRASH) {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                deleteNote();
            }
        });
        dag.addSeparator();
        dag.add(new AnAction("Search", "Search", QuickNotesIcon.SEARCH) {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                if (searchPanel.isVisible()) {
                    hideSearch();
                } else {
                    showSearch();
                }
            }
        });
        dag.add(new AnAction("Show All Notes", "Show All Notes", QuickNotesIcon.LIST) {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                if (listAllNodes) {
                    listAllNodes = false;
                    selectNote(getSelectedNoteIndex(), true);
                } else {
                    listAllNotes();
                }
            }
        });
        dag.add(new AnAction("Settings", "Settings", QuickNotesIcon.SLIDERS) {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                OptionsDialog dialog = new OptionsDialog(element);
                dialog.setLocationRelativeTo(null);
                dialog.pack();
                dialog.setVisible(true);
            }
        });

        final ActionToolbar actionToolbar = actionManager.createActionToolbar("QuickNotes", dag, true);
        final JComponent actionToolbarComponent = actionToolbar.getComponent();
        actionToolbar.setReservePlaceAutoPopupIcon(false);
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        actionPanel.add(actionToolbarComponent);

        buttonHideSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonHideSearch.setIcon(QuickNotesIcon.CLOSE);
        buttonHideSearch.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                hideSearch();
            }
        });

        labelNoteTitle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelNoteTitle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                renameNote();
            }
        });

        pane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                getSelectedNote().setText(pane.getText());
                quickNotesManager.syncNoteText(id);
            }
        });

        pane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (pane.getText().equals("Enter your notes here...")) {
                    pane.select(0, pane.getDocument().getLength());
                } else {
                    pane.setCaretPosition(0);
                }
            }
        });
        createPopupMenu();

        try {
            quickNotesManager.setShowLineNumbers(!"N".equals(element.getAttributeValue("showlinenumbers")));
        } catch (NumberFormatException e) {
            quickNotesManager.setShowLineNumbers(true);
        }
        pane.setMargin(quickNotesManager.isShowLineNumbers() ? EDITOR_INSET_LINENUMBER : EDITOR_INSET);

        selectedIndex = 0;
        try {
            selectedIndex = Integer.parseInt(element.getAttributeValue("selectednoteindex"));
        } catch (NumberFormatException e) {
            selectedIndex = 0;
        }

        quickNotesManager.setWordWrap("Y".equals(element.getAttributeValue("wordwrap")));
        quickNotesManager.addQuickNotesPanel(this);
        selectNote(selectedIndex, true);
        pane.setFont(quickNotesManager.getNotesFont());
        pane.setLineWrap(quickNotesManager.isWordWrap());
        pane.setWrapStyleWord(quickNotesManager.isWordWrap());
        pane.setForeground(quickNotesManager.getFontColor());
        pane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if ( caretPosition >= selectedNote.getText().length() ) {
                    caretPosition = selectedNote.getText().length() - 1;
                }
                pane.setCaretPosition(caretPosition);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                caretPosition = pane.getCaretPosition();
            }
        });

        searchPanel.setVisible(false);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                handleSearch(e.getKeyCode());
            }
        });

        panel1.addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
            }

            public void ancestorRemoved(AncestorEvent event) {
                QuickNotesManager.saveSettings(element);
            }

            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    /**
     * Setter for property 'notes'.
     *
     * @param notes Value to set for property 'notes'.
     */
    public void setNotes(String notes) {
        getSelectedNote().setText(notes);
    }

    /**
     * Setter for property 'selectedNoteIndex'.
     *
     * @param index Value to set for property 'selectedNoteIndex'.
     */
    private void setSelectedNoteIndex(int index) {
        selectedIndex = index;
        element.setAttribute("selectednoteindex", String.valueOf(index));
    }

    /**
     * Getter for property 'selectedNoteIndex'.
     *
     * @return Value for property 'selectedNoteIndex'.
     */
    public int getSelectedNoteIndex() {
        return selectedIndex;
    }

    /**
     * Getter for property 'selectedNote'.
     *
     * @return Value for property 'selectedNote'.
     */
    private Element getSelectedNote() {
        return selectedNote;
    }

    /**
     * @return
     */
    private boolean hasMoreNotes() {
        return getSelectedNoteIndex() < element.getChildren().size() - 1;
    }

    /**
     * Getter for property 'rootComponent'.
     *
     * @return Value for property 'rootComponent'.
     */
    public JComponent getRootComponent() {
        return panel1;
    }

    /**
     * Selects the previous Note
     */
    private void goBack() {
        if (getSelectedNoteIndex() > 0) {
            selectNote(getSelectedNoteIndex() - 1, true);
        }
    }

    /**
     * Selects the next Note
     */
    private void goNext() {
        if (getSelectedNoteIndex() < element.getChildren().size() - 1) {
            selectNote(getSelectedNoteIndex() + 1, true);
        }
    }

    /**
     * Selects a Note
     *
     * @param index        - index of the note to select
     * @param requestFocus - whether to bring selected note in focus or not
     */
    public void selectNote(int index,
                           boolean requestFocus) {
        // if pane has no parent then add the pane back to scroller and reset button statuses
        if (pane.getParent() == null) {
            noteScroller.getViewport().add(pane);
        }

        if (index >= 0 && index < element.getChildren().size()) {
            setSelectedNoteIndex(index);
            selectedNote = element.getChildren().get(index);
            pane.setText(selectedNote.getText());
            labelNoteTitle.setText(selectedNote.getAttributeValue("title"));

            try {
                Date createdt = sdf.parse(selectedNote.getAttributeValue("createdt"));
                Date today = new Date();
                long days = (today.getTime() - createdt.getTime()) / (60 * 60 * 24 * 1000);
                //addedon.setText("(Added " + days + " days ago on " + selectedNote.getAttributeValue("createdt") + ")");
                labelNoteTitle.setToolTipText("(Added " + days + " days ago on " + selectedNote.getAttributeValue("createdt") + ")");
            } catch (ParseException e) {
                labelNoteTitle.setToolTipText("(Added on " + selectedNote.getAttributeValue("createdt") + ")");
                //addedon.setText("(Added on " + selectedNote.getAttributeValue("createdt") + ")");
            }
            labelIndex.setText( "[" + (index + 1) + " / " + element.getChildren().size() + "]" );
            if (requestFocus) {
                pane.requestFocus();
            }
        }

/*
        buttonList2.setSelected(false);
*/
        pane.setFont(quickNotesManager.getNotesFont());
        quickNotesManager.setNoteEditWarning();
    }

    /**
     * Deletes the current Note
     */
    private void deleteNote() {
        if (element.getChildren().size() > 1) {
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(panel1, "Are you sure you want to delete this Note?",
                    "Confirm Note Delete", JOptionPane.YES_NO_OPTION)) {
                if (getSelectedNoteIndex() >= 0 && getSelectedNoteIndex() < element.getChildren().size()) {
                    Element note = getSelectedNote();
                    element.removeContent(note);
                    if (getSelectedNoteIndex() > 0) {
                        setSelectedNoteIndex(getSelectedNoteIndex() - 1);
                    }
                    selectNote(getSelectedNoteIndex(), true);
                    quickNotesManager.syncQuickNotePanels(id);
                }
            }
        }
    }

    /**
     * Renames the current Note
     */
    private void renameNote() {
        String notetitle = getSelectedNote().getAttributeValue("title");
        String title = JOptionPane.showInputDialog(panel1, "Please enter title for this Note", notetitle);
        if (title != null && title.length() > 0 && !title.equals(notetitle)) {
            getSelectedNote().setAttribute("title", title);
            labelNoteTitle.setText(title);
            quickNotesManager.syncQuickNotePanels(id);
        }
    }

    /**
     * Lists all notes
     */
    private void listAllNotes() {
        listAllNodes = true;
        if (searchPane == null) {
            searchPane = new JEditorPane();
            searchPane.setEditable(false);
            searchPane.setContentType("text/html");
            searchPane.setMargin(JBUI.emptyInsets());
            searchPane.setBackground(JBColor.background());
            searchPane.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        setSelectedNoteIndex(Integer.parseInt(e.getURL().getHost()));
                        hideSearch();
                    }
                }
            });
        }
        if (searchPane.getParent() == null) {
            noteScroller.getViewport().add(searchPane);
        }

        int foregroundRed = JBColor.foreground().getRed();
        int foregroundGreen = JBColor.foreground().getGreen();
        int foregroundBlue = JBColor.foreground().getBlue();
        String foregroundColor = "rgb(" + foregroundRed + "," + foregroundGreen + "," + foregroundBlue + ")";
        boolean even = true;
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='margin:0'>");
        java.util.List list = element.getChildren();
        for (int i = 0; i < list.size(); i++) {
            Element e = (Element) list.get(i);
            String txt = e.getText();
            int end = 50;
            if (txt.length() < end) {
                end = txt.length();
            }
            txt = txt.substring(0, end);
            txt = txt.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>");

            sb.append("<div style='padding:3px;border-bottom:1px solid ").append(foregroundColor).append(";'>");
            sb.append("<div style='font:bold 10px sans-serif'>");
            sb.append((i + 1)).append(".&nbsp;");
            sb.append("<a href='http://").append(i).append("'>");
            sb.append(e.getAttributeValue("title")).append("</a></div>");
            sb.append("<table><tr><td style='font:normal 9px verdana;color:gray;padding-left:10px'>").append(txt).append("</td></tr></table>");
            sb.append("</div>");
            even = !even;
        }
        sb.append("</body></html>");
        searchPane.setText(sb.toString());

        labelNoteTitle.setText("All Notes");
/*
        buttonBack.setEnabled(false);
        buttonNext.setEnabled(false);
        buttonTrash.setEnabled(false);
        buttonAdd.setEnabled(false);
        buttonOptions.setEnabled(false);
*/
    }

    /**
     * Shows the search bar to search for a text in Notes
     */
    private void showSearch() {
        searchPanel.setVisible(true);
        searchField.requestFocus();
/*
        buttonBack.setEnabled(false);
        buttonNext.setEnabled(false);
        buttonTrash.setEnabled(false);
        buttonAdd.setEnabled(false);
        buttonOptions.setEnabled(false);
        buttonList2.setEnabled(false);
*/
    }

    /**
     * Hides the search bar and resets the button status
     */
    private void hideSearch() {
        searchField.setText("");
        searchPanel.setVisible(false);
/*
        buttonBack.setEnabled(true);
        buttonNext.setEnabled(true);
        buttonTrash.setEnabled(true);
        buttonAdd.setEnabled(true);
        buttonOptions.setEnabled(true);
        buttonList2.setEnabled(true);
*/
        selectNote(selectedIndex, true);
    }

    /**
     * Handles searching
     *
     * @param keyCode
     */
    private void handleSearch(int keyCode) {
        if (keyCode == KeyEvent.VK_ESCAPE) {
            // hide if user hits Escape
            hideSearch();
        } else {
            String str = searchField.getText().trim();
            if (str.length() > 0) {
                if (searchPane == null) {
                    searchPane = new JEditorPane();
                    searchPane.setEditable(false);
                    searchPane.setContentType("text/html");
                    searchPane.setMargin(JBUI.emptyInsets());
                    searchPane.setBackground(JBColor.background());
                    searchPane.addHyperlinkListener(new HyperlinkListener() {
                        public void hyperlinkUpdate(HyperlinkEvent e) {
                            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                setSelectedNoteIndex(Integer.parseInt(e.getURL().getHost()));
                                hideSearch();
                            }
                        }
                    });
                }
                if (searchPane.getParent() == null) {
                    noteScroller.getViewport().add(searchPane);
                }

                int foregroundRed = JBColor.foreground().getRed();
                int foregroundGreen = JBColor.foreground().getGreen();
                int foregroundBlue = JBColor.foreground().getBlue();
                String foregroundColor = "rgb(" + foregroundRed + "," + foregroundGreen + "," + foregroundBlue + ")";
                boolean even = true;
                int count = 0;
                StringBuilder sb = new StringBuilder();
                sb.append("<html><body style='margin:0;font-color:").append(foregroundColor).append("'>");
                java.util.List list = element.getChildren();
                for (int i = 0; i < list.size(); i++) {
                    boolean exists = false;
                    Element e = (Element) list.get(i);

                    // search in Note text
                    String txt = e.getText();
                    int txtIndex = StringUtil.indexOfIgnoreCase(txt, str);
                    if (txtIndex > -1) {
                        exists = true;
                        String searchText = txt.substring(txtIndex, txtIndex + str.length());
                        txt = StringUtil.replace(txt, "<", "&lt;");
                        txt = StringUtil.replace(txt, ">", "&gt;");
                        txt = StringUtil.replace(txt, "\n", "<br>");
                        txt = StringUtil.replace(txt, searchText, "<span style='background-color:yellow'>" + searchText + "</span>");
                    }

                    // search in Note title
                    String title = e.getAttributeValue("title");
                    if (title == null) {
                        title = "";
                    }
                    int titleIndex = StringUtil.indexOfIgnoreCase(title, str);
                    if (titleIndex > -1) {
                        exists = true;
                        String searchText = title.substring(titleIndex, titleIndex + str.length());
                        title = StringUtil.replace(title, searchText, "<span style='background-color:yellow'>" + searchText + "</span>");
                    }

                    if (exists) {
                        count++;
                        even = !even;
                        sb.append("<div style='padding:3px;border-bottom:1px solid ").append(foregroundColor).append(";'>");
                        sb.append("<div style='font:bold 10px sans-serif'>");
                        sb.append("<a href='http://").append(i).append("' style='text-decoration:none;font-color:").append(foregroundColor).append(";'>").append(title).append("</a></div>");
                        sb.append("<table><tr><td style='font:normal 9px verdana;color:gray;padding-left:10px'>").append(txt).append("</td></tr></table>");
                        sb.append("</div>");
                    }
                }
                sb.append("</body></html>");
                searchPane.setText(count > 0 ? sb.toString() : "<div style='font:normal 10px sans-serif;padding:5px'>No matching notes found...</div>");
            } else {
                searchPane.setText("<div style='font:normal 10px sans-serif;padding:5px'>Please enter a text to search...</div>");
            }
        }
    }

    /**
     * Adds a new Note Element to Root element
     */
    public void addNewNote(String notes) {
        Element note = new Element("note");
        note.setAttribute("title", titleFormat.format(new Date()));
        note.setAttribute("createdt", sdf.format(new Date()));
        note.setText(notes);
        element.addContent(note);
        selectNote(element.getChildren().size() - 1, true);
        quickNotesManager.syncQuickNotePanels(id);
    }

    /**
     * Adds a new Note Element to Root element
     */
    public void appendToCurrentNote(String notes) {
        selectedNote.setText(selectedNote.getText() + "\n" + notes);
        pane.setText(selectedNote.getText());
        //quickNotesManager.syncQuickNotePanels( id );
    }

    /**
     * Creates Custom UI Component for Text Area
     */
    private void createUIComponents() {
        pane = new JTextArea() {
            {
                setOpaque(false);
            }

            public void paint(Graphics g) {
                g.setColor(quickNotesManager.getBackgroundColor());
                g.fillRect(0, 0, getWidth(), getHeight());
                Rectangle clip = g.getClipBounds();
                FontMetrics fm = g.getFontMetrics(getFont());
                Insets insets = getInsets();
                int fontHeignt = fm.getHeight();
                int y = fm.getAscent() + insets.top;
                int startLineNumber = ((clip.y + insets.top) / fontHeignt) + 1;
                if (y < clip.y) {
                    y = startLineNumber * fontHeignt - (fontHeignt - fm.getAscent());
                }
                int yend = y + clip.height + fontHeignt;
                // making sure it does not go out of control
                if (yend > 2048) {
                    yend = 2048;
                }
                while (y < yend) {
                    if (quickNotesManager.isShowLineNumbers() && startLineNumber <= getLineCount()) {
                        g.setColor(quickNotesManager.getLineNumberColor());
                        g.drawString(startLineNumber++ + ".", 2, y);
                    }
                    if (quickNotesManager.isShowBackgroundLines()) {
                        g.setColor(quickNotesManager.getBackgroundLineColor());
                        g.drawLine(0, y + 2, getWidth(), y + 2);
                    }
                    y += fontHeignt;
                }
                g.setColor(quickNotesManager.getBackgroundLineColor());
                if (quickNotesManager.isShowLineNumbers()) {
                    if (getLineCount() < 1000) {
                        g.drawLine(30, 0, 30, getHeight());
                        g.drawLine(32, 0, 32, getHeight());
                        pane.setMargin(EDITOR_INSET_LINENUMBER);
                    } else {
                        g.drawLine(34, 0, 34, getHeight());
                        g.drawLine(36, 0, 36, getHeight());
                        pane.setMargin(EDITOR_INSET_LINENUMBER_1000);
                    }
                } else {
                    g.drawLine(20, 0, 20, getHeight());
                    g.drawLine(22, 0, 22, getHeight());
                    pane.setMargin(EDITOR_INSET);
                }
                super.paint(g);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QuickNotesPanel that = (QuickNotesPanel) o;
        return id.equals(that.id);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public String getId() {
        return id;
    }

    /**
     * Shows the popup menu in Text Area
     */
    public void createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem search = new JMenuItem("Search", QuickNotesIcon.SEARCH);
        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSearch();
            }
        });

        JMenuItem cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText("Cut");
        cut.setIcon(QuickNotesIcon.CUT);
        cut.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                getSelectedNote().setText(pane.getText());
                quickNotesManager.syncNoteText(id);
            }
        });

        JMenuItem copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        copy.setIcon(QuickNotesIcon.COPY);

        JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        paste.setIcon(QuickNotesIcon.PASTE);
        paste.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                getSelectedNote().setText(pane.getText());
                quickNotesManager.syncNoteText(id);
            }
        });

        JMenuItem popupNext = new JMenuItem("Next Note", QuickNotesIcon.ARROW_RIGHT);
        popupNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goNext();
            }
        });

        JMenuItem popupBack = new JMenuItem("Previous Note", QuickNotesIcon.ARROW_LEFT);
        popupBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });

        JMenuItem popupList = new JMenuItem("List All Notes", QuickNotesIcon.LIST);
        popupList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listAllNotes();
            }
        });

        JMenuItem delete = new JMenuItem("Delete Note", QuickNotesIcon.TRASH);
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteNote();
            }
        });

        popupMenu.add(search);
        popupMenu.addSeparator();
        popupMenu.add(cut);
        popupMenu.add(copy);
        popupMenu.add(paste);
        popupMenu.addSeparator();
        popupMenu.add(popupNext);
        popupMenu.add(popupBack);
        popupMenu.add(popupList);
        popupMenu.addSeparator();
        popupMenu.add(delete);
        pane.addMouseListener(new PopupListener(popupMenu));
    }

    /**
     * @param warning
     */
    public void setWarning(boolean warning) {
        if (warning) {
            labelNoteTitle.setIcon(QuickNotesIcon.ALERT);
            labelNoteTitle.setToolTipText("This Note is also being edited in another IDEA instance");
        } else {
            labelNoteTitle.setIcon(null);
            labelNoteTitle.setToolTipText(null);
        }
    }

    /**
     * @return
     */
    public String getText() {
        return pane.getText();
    }

    /**
     * @param text
     */
    public void setText(String text) {
        pane.setText(text);
    }

    public void setNotesFont(Font notesFont) {
        pane.setFont(notesFont);
    }

    public JTextArea getTextArea() {
        return pane;
    }

    public void setFontColor(Color fontColor) {
        pane.setForeground(fontColor);
    }

    public void setBackgroundColor(Color backgroundColor) {
        Color backgroundColor1 = backgroundColor;
        pane.setBackground(backgroundColor);
    }

    public void setShowBackgroundLines(boolean showBackgroundLines) {
        this.showBackgroundLines = showBackgroundLines;
    }

    private final class AddNoteAction extends AnAction {
        private AddNoteAction() {
            super("Quick Notes", "Quick Notes Description", QuickNotesIcon.ADD);
        }

        @Override
        public void actionPerformed(final AnActionEvent e) {
            addNewNote("Enter your notes here...");
        }
    }

}

/**
 *
 */
class PopupListener
        extends MouseAdapter {
    JPopupMenu popup;

    PopupListener(JPopupMenu popupMenu) {
        popup = popupMenu;
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}