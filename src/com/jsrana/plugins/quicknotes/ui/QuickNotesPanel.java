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

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import com.jsrana.plugins.quicknotes.util.Utils;
import com.twelvemonkeys.lang.StringUtil;
import org.jdom.Element;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private JLabel notestitle;
    private JLabel indexLabel;
    private JLabel addedon;
    private JLabel logo;
    private JScrollPane noteScroller;
    private JButton buttonAdd;
    private JButton buttonBack;
    private JButton buttonNext;
    private JButton buttonTrash;
    private JButton buttonSave;
    private JButton buttonOptions;
    private JButton buttonRename;
    private JPanel topToolbarPanel;
    private JPanel bottomToolbarPanel;
    private JToolBar toolbar;
    private JPanel searchPanel;
    private JTextField searchField;
    private JButton buttonHideSearch;
    private JLabel quickNotesVersionLabel;
    private JButton buttonSearch;
    private JButton buttonList2;
    private JButton buttonAllNotesClose;
    public Element element;
    private int selectedIndex;
    private Element selectedNote;
    private QuickNotesManager quickNotesManager;
    private boolean listAllNodes = false;

    public static final Color EDITOR_COLOR_FONT = Gray._0;
    public static final Color EDITOR_COLOR_BACKGROUND = new JBColor(new Color(254, 252, 178), new Color(254, 252, 178));
    public static final Color EDITOR_COLOR_LINE = new JBColor(new Color(234, 233, 164), new Color(234, 233, 164));
    public static final Color EDITOR_COLOR_LINENUMBER = new JBColor(new Color(189, 183, 107), new Color(189, 183, 107));
    private static final Insets EDITOR_INSET = JBUI.insetsLeft(25);
    private static final Insets EDITOR_INSET_LINENUMBER = JBUI.insetsLeft(35);
    private static final Insets EDITOR_INSET_LINENUMBER_1000 = JBUI.insetsLeft(38);

    public static SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy h:mm a");
    public static SimpleDateFormat titleFormat = new SimpleDateFormat("MMM d, yyyy h:mm a");

    private boolean showBackgroundLines = true;

    JEditorPane searchPane;

    /**
     * Constructor
     *
     * @param element
     */
    public QuickNotesPanel(final Element element) {
        quickNotesManager = QuickNotesManager.getInstance();
        id = quickNotesManager.getNextPanelID();
        this.element = element;

        // set toolbar background color to theme background color
        toolbar.setBackground(JBColor.background());
        toolbar.setMargin(JBUI.emptyInsets());

        // set button properties
        buttonAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonAdd.setIcon(IconLoader.getIcon("/resources/flat/add.png"));
        buttonAdd.setBackground(JBColor.background());
        buttonAdd.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                addNewNote("Enter your notes here...");
            }
        });

        buttonBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonBack.setIcon(IconLoader.getIcon("/resources/flat/arrowleft.png"));
        buttonBack.setBackground(JBColor.background());
        buttonBack.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });

        buttonNext.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonNext.setIcon(IconLoader.getIcon("/resources/flat/arrowright.png"));
        buttonNext.setBackground(JBColor.background());
        buttonNext.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                goNext();
            }
        });

        buttonSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonSearch.setIcon(IconLoader.getIcon("/resources/flat/search.png"));
        buttonSearch.setBackground(JBColor.background());
        buttonSearch.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (searchPanel.isVisible()) {
                    hideSearch();
                } else {
                    showSearch();
                }
            }
        });

        buttonTrash.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonTrash.setIcon(IconLoader.getIcon("/resources/flat/trash.png"));
        buttonTrash.setBackground(JBColor.background());
        buttonTrash.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                deleteNote();
            }
        });

        buttonList2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonList2.setIcon(IconLoader.getIcon("/resources/flat/list.png"));
        buttonList2.setBackground(JBColor.background());
        buttonList2.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (listAllNodes) {
                    listAllNodes = false;
                    selectNote(getSelectedNoteIndex(), true);
                } else {
                    listAllNotes();
                }
            }
        });

        buttonSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonSave.setIcon(IconLoader.getIcon("/resources/flat/save.png"));
        buttonSave.setBackground(JBColor.background());
        buttonSave.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                saveNote();
            }
        });

        buttonOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonOptions.setIcon(IconLoader.getIcon("/resources/flat/sliders.png"));
        buttonOptions.setBackground(JBColor.background());
        buttonOptions.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                OptionsDialog dialog = new OptionsDialog();
                dialog.setLocationRelativeTo(null);
                dialog.pack();
                dialog.setVisible(true);
            }
        });

        buttonRename.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonRename.setIcon(IconLoader.getIcon("/resources/flat/edit.png"));
        buttonRename.setBackground(JBColor.background());
        buttonRename.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                renameNote();
            }
        });

        buttonHideSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonHideSearch.setIcon(IconLoader.getIcon("/resources/flat/close.png"));
        buttonHideSearch.setBackground(JBColor.background());
        buttonHideSearch.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                hideSearch();
            }
        });

        buttonAllNotesClose.setVisible(false);
        buttonAllNotesClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonAllNotesClose.setIcon(IconLoader.getIcon("/resources/flat/close.png"));
        buttonAllNotesClose.setBackground(JBColor.background());
        buttonAllNotesClose.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                listAllNodes = false;
                selectNote(getSelectedNoteIndex(), true);
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

        topToolbarPanel.setBorder(BorderFactory.createLineBorder(JBColor.GRAY));
        bottomToolbarPanel.setBorder(BorderFactory.createLineBorder(JBColor.GRAY));
        setToolbarLocation(quickNotesManager.getToolbarLocation());

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

        quickNotesVersionLabel.setText(QuickNotesManager.VERSION);
    }

    /**
     * Sets the location of toolbar. Possible options are
     * QuickNotesManager.TOOLBARLOCATION_BOTTOM or QuickNotesManager.TOOLBARLOCATION_TOP
     *
     * @param location
     */
    public void setToolbarLocation(int location) {
        bottomToolbarPanel.removeAll();
        topToolbarPanel.removeAll();
        topToolbarPanel.setVisible(false);
        bottomToolbarPanel.setVisible(false);

        if (location == QuickNotesManager.TOOLBARLOCATION_BOTTOM) {
            bottomToolbarPanel.setLayout(new BorderLayout());
            bottomToolbarPanel.add("Center", toolbar);
            bottomToolbarPanel.setVisible(true);
        } else {
            topToolbarPanel.setLayout(new BorderLayout());
            topToolbarPanel.add("Center", toolbar);
            topToolbarPanel.setVisible(true);
        }

        bottomToolbarPanel.repaint();
        topToolbarPanel.repaint();
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
        buttonRename.setVisible(true);
        buttonAllNotesClose.setVisible(false);

        // if pane has no parent then add the pane back to scroller and reset button statuses
        if (pane.getParent() == null) {
            buttonList2.setEnabled(true);
            buttonRename.setVisible(true);
            buttonAdd.setEnabled(true);
            buttonSave.setEnabled(true);
            buttonOptions.setEnabled(true);
            logo.setIcon(Utils.ICON_NOTE);
            noteScroller.getViewport().add(pane);
        }

        if (index >= 0 && index < element.getChildren().size()) {
            setSelectedNoteIndex(index);
            selectedNote = (Element) element.getChildren().get(index);
            pane.setText(selectedNote.getText());
            notestitle.setText(selectedNote.getAttributeValue("title"));

            try {
                Date createdt = sdf.parse(selectedNote.getAttributeValue("createdt"));
                Date today = new Date();
                long days = (today.getTime() - createdt.getTime()) / (60 * 60 * 24 * 1000);
                addedon.setText("(Added " + days + " days ago on " + selectedNote.getAttributeValue("createdt") + ")");
            } catch (ParseException e) {
                addedon.setText("(Added on " + selectedNote.getAttributeValue("createdt") + ")");
            }
            indexLabel.setText((index + 1) + " / " + element.getChildren().size());
            buttonBack.setEnabled(index > 0);
            buttonNext.setEnabled(hasMoreNotes());
            buttonTrash.setEnabled(element.getChildren().size() > 1);
            if (requestFocus) {
                pane.requestFocus();
            }
        }

        buttonList2.setSelected(false);
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
     * Saves all Notes to a File
     */
    private void saveNote() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog(panel1);
        File file = fileChooser.getSelectedFile();
        if (file != null) {
            try {
                getSelectedNote().setText(pane.getText());
                file.createNewFile();
                StringBuffer sb = new StringBuffer();
                FileWriter fileWriter = new FileWriter(file);
                java.util.List list = element.getChildren();
                for (Object aList : list) {
                    Element e = (Element) aList;
                    sb.append(e.getAttributeValue("title"));
                    sb.append(" (Added on ").append(e.getAttributeValue("createdt")).append(")");
                    sb.append("\n-------------------------------------------------------------------\n");
                    sb.append(e.getText());
                    sb.append("\n\n");
                }
                fileWriter.write(sb.toString());
                fileWriter.flush();
                fileWriter.close();
                JOptionPane.showMessageDialog(panel1, "Notes have been saved successfully to file\n\n" + file.getPath() + "\n ",
                        "File saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(panel1, "Unable to create file. Please make sure you have write access to the folder.",
                        "File creation failure", JOptionPane.ERROR_MESSAGE);
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
            notestitle.setText(title);
            quickNotesManager.syncQuickNotePanels(id);
        }
    }

    /**
     * Lists all notes
     */
    private void listAllNotes() {
        buttonRename.setVisible(false);
        buttonAllNotesClose.setVisible(true);
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

        notestitle.setText("All Notes");
        addedon.setText("(Click on a Note title to view)");
        buttonBack.setEnabled(false);
        buttonNext.setEnabled(false);
        buttonTrash.setEnabled(false);
        buttonAdd.setEnabled(false);
        buttonSave.setEnabled(false);
        buttonOptions.setEnabled(false);
        logo.setIcon(Utils.ICON_LIST);
    }

    /**
     * Shows the search bar to search for a text in Notes
     */
    private void showSearch() {
        searchPanel.setVisible(true);
        searchField.requestFocus();
        buttonBack.setEnabled(false);
        buttonNext.setEnabled(false);
        buttonTrash.setEnabled(false);
        buttonAdd.setEnabled(false);
        buttonSave.setEnabled(false);
        buttonOptions.setEnabled(false);
        buttonList2.setEnabled(false);
        buttonRename.setVisible(false);
    }

    /**
     * Hides the search bar and resets the button status
     */
    private void hideSearch() {
        searchField.setText("");
        searchPanel.setVisible(false);
        buttonBack.setEnabled(true);
        buttonNext.setEnabled(true);
        buttonTrash.setEnabled(true);
        buttonAdd.setEnabled(true);
        buttonSave.setEnabled(true);
        buttonOptions.setEnabled(true);
        buttonList2.setEnabled(true);
        buttonRename.setVisible(true);
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
     *
     * @return Added Note Element
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
     *
     * @return Added Note Element
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

        JMenuItem search = new JMenuItem("Search", Utils.ICON_SEARCH);
        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSearch();
            }
        });

        JMenuItem cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText("Cut");
        cut.setIcon(Utils.ICON_CUT);
        cut.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                getSelectedNote().setText(pane.getText());
                quickNotesManager.syncNoteText(id);
            }
        });

        JMenuItem copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        copy.setIcon(Utils.ICON_COPY);

        JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        paste.setIcon(Utils.ICON_PASTE);
        paste.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                getSelectedNote().setText(pane.getText());
                quickNotesManager.syncNoteText(id);
            }
        });

        JMenuItem popupNext = new JMenuItem("Next Note", Utils.ICON_FORWARD);
        popupNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goNext();
            }
        });

        JMenuItem popupBack = new JMenuItem("Previous Note", Utils.ICON_BACK);
        popupBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });

        JMenuItem popupList = new JMenuItem("List All Notes", Utils.ICON_LIST16);
        popupList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listAllNotes();
            }
        });

        JMenuItem delete = new JMenuItem("Delete Note", Utils.ICON_DELETE);
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
            notestitle.setIcon(Utils.ICON_WARNING);
            notestitle.setToolTipText("This Note is also being edited in another IDEA instance");
        } else {
            notestitle.setIcon(null);
            notestitle.setToolTipText(null);
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