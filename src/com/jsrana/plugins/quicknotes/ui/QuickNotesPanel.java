/**
 * Copyright 2009 Jitendra Rana, jsrana@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jsrana.plugins.quicknotes.ui;

import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import com.jsrana.plugins.quicknotes.util.Utils;
import org.jdom.Element;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private JToggleButton buttonList;
    private JButton buttonOptions;
    private JButton buttonRename;
    private JPanel topToolbarPanel;
    private JPanel bottomToolbarPanel;
    private JToolBar toolbar;
    private JToggleButton buttonSearch;
    private JPanel searchPanel;
    private JTextField searchField;
    private JButton buttonHideSearch;
    private JLabel quickNotesVersionLabel;
    public Element element;
    private int selectedIndex;
    private Element selectedNote;
    private QuickNotesManager quickNotesManager;

    public static final Color EDITOR_COLOR_FONT = new Color( 0, 0, 0 );
    public static final Color EDITOR_COLOR_BACKGROUND = new Color( 254, 252, 178 );
    public static final Color EDITOR_COLOR_LINE = new Color( 234, 233, 164 );
    public static final Color EDITOR_COLOR_LINENUMBER = new Color( 189, 183, 107 );
    private static final Insets EDITOR_INSET = new Insets( 0, 25, 0, 0 );
    private static final Insets EDITOR_INSET_LINENUMBER = new Insets( 0, 35, 0, 0 );
    private static final Insets EDITOR_INSET_LINENUMBER_1000 = new Insets( 0, 38, 0, 0 );

    public static SimpleDateFormat sdf = new SimpleDateFormat( "EEE, d MMM yyyy h:mm a" );
    public static SimpleDateFormat titleFormat = new SimpleDateFormat( "MMM d, yyyy h:mm a" );

    private Color backgroundColor = EDITOR_COLOR_BACKGROUND;
    private boolean showBackgroundLines = true;

    JEditorPane searchPane;

    /**
     * Constructor
     *
     * @param element
     */
    public QuickNotesPanel( final Element element ) {
        quickNotesManager = QuickNotesManager.getInstance();
        id = quickNotesManager.getNextPanelID();
        this.element = element;

        buttonAdd.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonAdd.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                addNewNote( "Enter your notes here..." );
            }
        } );

        buttonBack.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonBack.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                goBack();
            }
        } );

        buttonTrash.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonTrash.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                deleteNote();
            }
        } );

        buttonNext.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonNext.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                goNext();
            }
        } );

        buttonRename.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonRename.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                renameNote();
            }
        } );

        buttonSave.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonSave.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                saveNote();
            }
        } );

        buttonList.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonList.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                if ( buttonList.getIcon() == Utils.ICON_LIST16_SELECTED ) {
                    selectNote( getSelectedNoteIndex(), true );
                }
                else {
                    listAllNotes();
                }
            }
        } );

        buttonOptions.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        buttonOptions.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                OptionsDialog dialog = new OptionsDialog();
                dialog.setLocationRelativeTo( null );
                dialog.pack();
                dialog.setVisible( true );
            }
        } );

        pane.addKeyListener( new KeyAdapter() {
            @Override public void keyReleased( KeyEvent e ) {
                getSelectedNote().setText( pane.getText() );
                quickNotesManager.syncNoteText( id );
            }
        } );

        pane.addFocusListener( new FocusAdapter() {
            @Override public void focusGained( FocusEvent e ) {
                if ( pane.getText().equals( "Enter your notes here..." ) ) {
                    pane.select( 0, pane.getDocument().getLength() );
                }
                else {
                    pane.setCaretPosition( 0 );
                }
            }
        } );
        createPopupMenu();

        try {
            quickNotesManager.setShowLineNumbers( !"N".equals( element.getAttributeValue( "showlinenumbers" ) ) );
        }
        catch ( NumberFormatException e ) {
            quickNotesManager.setShowLineNumbers( true );
        }
        pane.setMargin( quickNotesManager.isShowLineNumbers() ? EDITOR_INSET_LINENUMBER : EDITOR_INSET );

        selectedIndex = 0;
        try {
            selectedIndex = Integer.parseInt( element.getAttributeValue( "selectednoteindex" ) );
        }
        catch ( NumberFormatException e ) {
            selectedIndex = 0;
        }

        quickNotesManager.setWordWrap( "Y".equals( element.getAttributeValue( "wordwrap" ) ) );
        quickNotesManager.addQuickNotesPanel( this );
        selectNote( selectedIndex, true );
        pane.setFont( quickNotesManager.getNotesFont() );
        pane.setLineWrap( quickNotesManager.isWordWrap() );
        pane.setWrapStyleWord( quickNotesManager.isWordWrap() );
        pane.setForeground( quickNotesManager.getFontColor() );

        topToolbarPanel.setBorder( BorderFactory.createLineBorder( Color.GRAY ) );
        bottomToolbarPanel.setBorder( BorderFactory.createLineBorder( Color.GRAY ) );
        setToolbarLocation( quickNotesManager.getToolbarLocation() );

        toolbar.setMargin( new Insets( 0, 0, 0, 0 ) );

        searchPanel.setVisible( false );
        buttonSearch.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                if ( buttonSearch.isSelected() ) {
                    showSearch();
                }
                else {
                    hideSearch();
                }
            }
        } );
        buttonHideSearch.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                hideSearch();
            }
        } );
        searchField.addKeyListener( new KeyAdapter() {
            public void keyReleased( KeyEvent e ) {
                handleSearch( e.getKeyCode() );
            }
        } );

        panel1.addAncestorListener( new AncestorListener() {
            public void ancestorAdded( AncestorEvent event ) {
            }

            public void ancestorRemoved( AncestorEvent event ) {
                QuickNotesManager.saveSettings( element );
            }

            public void ancestorMoved( AncestorEvent event ) {
            }
        } );

        quickNotesVersionLabel.setText( QuickNotesManager.VERSION );
    }

    /**
     * Sets the location of toolbar. Possible options are
     * QuickNotesManager.TOOLBARLOCATION_BOTTOM or QuickNotesManager.TOOLBARLOCATION_TOP
     *
     * @param location
     */
    public void setToolbarLocation( int location ) {
        bottomToolbarPanel.removeAll();
        topToolbarPanel.removeAll();
        topToolbarPanel.setVisible( false );
        bottomToolbarPanel.setVisible( false );

        if ( location == QuickNotesManager.TOOLBARLOCATION_BOTTOM ) {
            bottomToolbarPanel.setLayout( new BorderLayout() );
            bottomToolbarPanel.add( "Center", toolbar );
            bottomToolbarPanel.setVisible( true );
        }
        else {
            topToolbarPanel.setLayout( new BorderLayout() );
            topToolbarPanel.add( "Center", toolbar );
            topToolbarPanel.setVisible( true );
        }

        bottomToolbarPanel.repaint();
        topToolbarPanel.repaint();
    }

    /**
     * Setter for property 'notes'.
     *
     * @param notes Value to set for property 'notes'.
     */
    public void setNotes( String notes ) {
        getSelectedNote().setText( notes );
    }

    /**
     * Setter for property 'selectedNoteIndex'.
     *
     * @param index Value to set for property 'selectedNoteIndex'.
     */
    private void setSelectedNoteIndex( int index ) {
        selectedIndex = index;
        element.setAttribute( "selectednoteindex", String.valueOf( index ) );
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
    public Element getSelectedNote() {
        return selectedNote;
    }

    /**
     * @return
     */
    public boolean hasMoreNotes() {
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
        if ( getSelectedNoteIndex() > 0 ) {
            selectNote( getSelectedNoteIndex() - 1, true );
        }
    }

    /**
     * Selects the next Note
     */
    private void goNext() {
        if ( getSelectedNoteIndex() < element.getChildren().size() - 1 ) {
            selectNote( getSelectedNoteIndex() + 1, true );
        }
    }

    /**
     * Selects a Note
     *
     * @param index        - index of the note to select
     * @param requestFocus - whether to bring selected note in focus or not
     */
    public void selectNote( int index,
                            boolean requestFocus ) {
        // if pane has no parent then add the pane back to scroller and reset button statuses
        if ( pane.getParent() == null ) {
            buttonList.setEnabled( true );
            buttonRename.setVisible( true );
            buttonAdd.setEnabled( true );
            buttonSave.setEnabled( true );
            buttonList.setIcon( Utils.ICON_LIST16 );
            buttonOptions.setEnabled( true );
            logo.setIcon( Utils.ICON_NOTE );
            noteScroller.getViewport().add( pane );
        }

        if ( index >= 0 && index < element.getChildren().size() ) {
            setSelectedNoteIndex( index );
            selectedNote = ( Element ) element.getChildren().get( index );
            pane.setText( selectedNote.getText() );
            notestitle.setText( selectedNote.getAttributeValue( "title" ) );

            try {
                Date createdt = sdf.parse( selectedNote.getAttributeValue( "createdt" ) );
                Date today = new Date();
                long days = ( today.getTime() - createdt.getTime() ) / ( 60 * 60 * 24 * 1000 );
                addedon.setText( "(Added " + days + " days ago on " + selectedNote.getAttributeValue( "createdt" ) + ")" );
            }
            catch ( ParseException e ) {
                addedon.setText( "(Added on " + selectedNote.getAttributeValue( "createdt" ) + ")" );
            }
            indexLabel.setText( ( index + 1 ) + " / " + element.getChildren().size() );
            buttonBack.setEnabled( index > 0 );
            buttonNext.setEnabled( hasMoreNotes() );
            buttonTrash.setEnabled( element.getChildren().size() > 1 );
            if ( requestFocus ) {
                pane.requestFocus();
            }
        }

        buttonList.setSelected( false );
        pane.setFont( quickNotesManager.getNotesFont() );
        quickNotesManager.setNoteEditWarning();
    }

    /**
     * Deletes the current Note
     */
    private void deleteNote() {
        if ( element.getChildren().size() > 1 ) {
            if ( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog( panel1, "Are you sure you want to delete this Note?",
                                                                          "Confirm Note Delete", JOptionPane.YES_NO_OPTION ) ) {
                if ( getSelectedNoteIndex() >= 0 && getSelectedNoteIndex() < element.getChildren().size() ) {
                    Element note = getSelectedNote();
                    element.removeContent( note );
                    if ( getSelectedNoteIndex() > 0 ) {
                        setSelectedNoteIndex( getSelectedNoteIndex() - 1 );
                    }
                    selectNote( getSelectedNoteIndex(), true );
                    quickNotesManager.syncQuickNotePanels( id );
                }
            }
        }
    }

    /**
     * Saves all Notes to a File
     */
    private void saveNote() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog( panel1 );
        File file = fileChooser.getSelectedFile();
        if ( file != null ) {
            try {
                getSelectedNote().setText( pane.getText() );
                file.createNewFile();
                StringBuffer sb = new StringBuffer();
                FileWriter fileWriter = new FileWriter( file );
                java.util.List list = element.getChildren();
                for ( Object aList : list ) {
                    Element e = ( Element ) aList;
                    sb.append( e.getAttributeValue( "title" ) );
                    sb.append( " (Added on " ).append( e.getAttributeValue( "createdt" ) ).append( ")" );
                    sb.append( "\n-------------------------------------------------------------------\n" );
                    sb.append( e.getText() );
                    sb.append( "\n\n" );
                }
                fileWriter.write( sb.toString() );
                fileWriter.flush();
                fileWriter.close();
                JOptionPane.showMessageDialog( panel1, "Notes have been saved successfully to file\n\n" + file.getPath() + "\n ",
                                               "File saved", JOptionPane.INFORMATION_MESSAGE );
            }
            catch ( IOException e ) {
                JOptionPane.showMessageDialog( panel1, "Unable to create file. Please make sure you have write access to the folder.",
                                               "File creation failure", JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    /**
     * Renames the current Note
     */
    private void renameNote() {
        String notetitle = getSelectedNote().getAttributeValue( "title" );
        String title = JOptionPane.showInputDialog( panel1, "Please enter title for this Note", notetitle );
        if ( title != null && title.length() > 0 && !title.equals( notetitle ) ) {
            getSelectedNote().setAttribute( "title", title );
            notestitle.setText( title );
            quickNotesManager.syncQuickNotePanels( id );
        }
    }

    /**
     * Lists all notes
     */
    private void listAllNotes() {
        if ( searchPane == null ) {
            searchPane = new JEditorPane();
            searchPane.setEditable( false );
            searchPane.setContentType( "text/html" );
            searchPane.setMargin( new Insets( 0, 0, 0, 0 ) );
            searchPane.setBackground( new Color( 233, 248, 248 ) );
            searchPane.addHyperlinkListener( new HyperlinkListener() {
                public void hyperlinkUpdate( HyperlinkEvent e ) {
                    if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                        setSelectedNoteIndex( Integer.parseInt( e.getURL().getHost() ) );
                        hideSearch();
                    }
                }
            } );
        }
        if ( searchPane.getParent() == null ) {
            noteScroller.getViewport().add( searchPane );
        }

        boolean even = true;
        StringBuilder sb = new StringBuilder();
        sb.append( "<html><body style='margin:0'>" );
        java.util.List list = element.getChildren();
        for ( int i = 0; i < list.size(); i++ ) {
            Element e = ( Element ) list.get( i );
            String txt = e.getText();
            int end = 50;
            if ( txt.length() < end ) {
                end = txt.length();
            }
            txt = txt.substring( 0, end );
            txt = txt.replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" ).replaceAll( "\n", "<br>" );

            sb.append( "<div style='background:" ).append( even ? "#e9f8f8" : "#d1f0f0" ).append( ";padding:3px'>" );
            sb.append( "<div style='font:bold 10px sans-serif'>" );
            sb.append( ( i + 1 ) ).append( ".&nbsp;" );
            sb.append( "<a href='http://" ).append( i ).append( "'>" );
            sb.append( e.getAttributeValue( "title" ) ).append( "</a></div>" );
            sb.append( "<table><tr><td style='font:normal 9px verdana;color:gray;padding-left:10px'>" ).append( txt ).append( "</td></tr></table>" );
            sb.append( "</div>" );
            even = !even;
        }
        sb.append( "</body></html>" );
        searchPane.setText( sb.toString() );

        notestitle.setText( "List of all Notes" );
        addedon.setText( "(Click on a Note title to select it)" );
        buttonBack.setEnabled( false );
        buttonNext.setEnabled( false );
        buttonTrash.setEnabled( false );
        buttonAdd.setEnabled( false );
        buttonSave.setEnabled( false );
        buttonOptions.setEnabled( false );
        buttonList.setIcon( Utils.ICON_LIST16_SELECTED );
        buttonRename.setVisible( false );
        logo.setIcon( Utils.ICON_LIST );
    }

    /**
     * Shows the search bar to search for a text in Notes
     */
    private void showSearch() {
        searchPanel.setVisible( true );
        searchField.requestFocus();
        buttonBack.setEnabled( false );
        buttonNext.setEnabled( false );
        buttonTrash.setEnabled( false );
        buttonAdd.setEnabled( false );
        buttonSave.setEnabled( false );
        buttonOptions.setEnabled( false );
        buttonList.setEnabled( false );
        buttonRename.setVisible( false );
    }

    /**
     * Hides the search bar and resets the button status
     */
    private void hideSearch() {
        searchField.setText( "" );
        searchPanel.setVisible( false );
        buttonSearch.setSelected( false );
        buttonBack.setEnabled( true );
        buttonNext.setEnabled( true );
        buttonTrash.setEnabled( true );
        buttonAdd.setEnabled( true );
        buttonSave.setEnabled( true );
        buttonOptions.setEnabled( true );
        buttonList.setEnabled( true );
        buttonRename.setVisible( true );
        selectNote( selectedIndex, true );
    }

    /**
     * Handles searching
     *
     * @param keyCode
     */
    private void handleSearch( int keyCode ) {
        if ( keyCode == KeyEvent.VK_ESCAPE ) {
            // hide if user hits Escape
            hideSearch();
        }
        else {
            String str = searchField.getText().trim();
            if ( str.length() > 0 ) {
                if ( searchPane == null ) {
                    searchPane = new JEditorPane();
                    searchPane.setEditable( false );
                    searchPane.setContentType( "text/html" );
                    searchPane.setMargin( new Insets( 0, 0, 0, 0 ) );
                    searchPane.setBackground( new Color( 233, 248, 248 ) );
                    searchPane.addHyperlinkListener( new HyperlinkListener() {
                        public void hyperlinkUpdate( HyperlinkEvent e ) {
                            if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                                setSelectedNoteIndex( Integer.parseInt( e.getURL().getHost() ) );
                                hideSearch();
                            }
                        }
                    } );
                }
                if ( searchPane.getParent() == null ) {
                    noteScroller.getViewport().add( searchPane );
                }

                boolean even = true;
                int count = 0;
                StringBuilder sb = new StringBuilder();
                sb.append( "<html><body style='margin:0'>" );
                java.util.List list = element.getChildren();
                for ( int i = 0; i < list.size(); i++ ) {
                    Element e = ( Element ) list.get( i );
                    String txt = e.getText();
                    int index = txt.indexOf( str );
                    if ( index != -1 ) {
                        count++;
                        int start = index - 20;
                        if ( start < 0 ) {
                            start = 0;
                        }
                        int end = index + 20;
                        if ( end > txt.length() ) {
                            end = txt.length();
                        }
                        txt = txt.substring( start, end ).replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" );
                        txt = txt.replaceAll( str, "<b><font color=black>" + str + "</font></b>" ).replaceAll( "\n", "<br>" );

                        sb.append( "<div style='background:" ).append( even ? "#e9f8f8" : "#d1f0f0" ).append( ";padding:3px'>" );
                        sb.append( "<div style='font:bold 10px sans-serif'>" );
                        sb.append( "<a href='http://" ).append( i ).append( "'>" );
                        sb.append( e.getAttributeValue( "title" ) ).append( "</a></div>" );
                        sb.append( "<table><tr><td style='font:normal 9px verdana;color:gray;padding-left:10px'>" ).append( txt ).append( "</td></tr></table>" );
                        sb.append( "</div>" );
                        even = !even;
                    }
                }
                sb.append( "</body></html>" );
                searchPane.setText( count > 0 ? sb.toString() : "<div style='font:normal 10px sans-serif;padding:5px'>No matching notes found...</div>" );
            }
            else {
                searchPane.setText( "<div style='font:normal 10px sans-serif;padding:5px'>Please enter a text to search...</div>" );
            }
        }
    }

    /**
     * Adds a new Note Element to Root element
     *
     * @return Added Note Element
     */
    public void addNewNote( String notes ) {
        Element note = new Element( "note" );
        note.setAttribute( "title", titleFormat.format( new Date() ) );
        note.setAttribute( "createdt", sdf.format( new Date() ) );
        note.setText( notes );
        element.addContent( note );
        selectNote( element.getChildren().size() - 1, true );
        quickNotesManager.syncQuickNotePanels( id );
    }

    /**
     * Adds a new Note Element to Root element
     *
     * @return Added Note Element
     */
    public void appendToCurrentNote( String notes ) {
        selectedNote.setText( selectedNote.getText() + "\n" + notes );
        pane.setText( selectedNote.getText() );
        //quickNotesManager.syncQuickNotePanels( id );
    }

    /**
     * Creates Custom UI Component for Text Area
     */
    private void createUIComponents() {
        pane = new JTextArea() {
            {
                setOpaque( false );
            }

            public void paint( Graphics g ) {
                g.setColor( quickNotesManager.getBackgroundColor() );
                g.fillRect( 0, 0, getWidth(), getHeight() );
                Rectangle clip = g.getClipBounds();
                FontMetrics fm = g.getFontMetrics( getFont() );
                Insets insets = getInsets();
                int fontHeignt = fm.getHeight();
                int y = fm.getAscent() + insets.top;
                int startLineNumber = ( ( clip.y + insets.top ) / fontHeignt ) + 1;
                if ( y < clip.y ) {
                    y = startLineNumber * fontHeignt - ( fontHeignt - fm.getAscent() );
                }
                int yend = y + clip.height + fontHeignt;
                // making sure it does not go out of control
                if ( yend > 2048 ) {
                    yend = 2048;
                }
                while ( y < yend ) {
                    if ( quickNotesManager.isShowLineNumbers() && startLineNumber <= getLineCount() ) {
                        g.setColor( quickNotesManager.getBackgroundLineColor() );
                        g.drawString( startLineNumber++ + ".", 2, y );
                    }
                    if ( quickNotesManager.isShowBackgroundLines() ) {
                        g.setColor( quickNotesManager.getBackgroundLineColor() );
                        g.drawLine( 0, y + 2, getWidth(), y + 2 );
                    }
                    y += fontHeignt;
                }
                g.setColor( quickNotesManager.getBackgroundLineColor() );
                if ( quickNotesManager.isShowLineNumbers() ) {
                    if ( getLineCount() < 1000 ) {
                        g.drawLine( 30, 0, 30, getHeight() );
                        g.drawLine( 32, 0, 32, getHeight() );
                        pane.setMargin( EDITOR_INSET_LINENUMBER );
                    }
                    else {
                        g.drawLine( 34, 0, 34, getHeight() );
                        g.drawLine( 36, 0, 36, getHeight() );
                        pane.setMargin( EDITOR_INSET_LINENUMBER_1000 );
                    }
                }
                else {
                    g.drawLine( 20, 0, 20, getHeight() );
                    g.drawLine( 22, 0, 22, getHeight() );
                    pane.setMargin( EDITOR_INSET );
                }
                super.paint( g );
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        QuickNotesPanel that = ( QuickNotesPanel ) o;
        return id.equals( that.id );
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

        JMenuItem search = new JMenuItem( "Search", Utils.ICON_SEARCH );
        search.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showSearch();
            }
        } );

        JMenuItem cut = new JMenuItem( new DefaultEditorKit.CutAction() );
        cut.setText( "Cut" );
        cut.setIcon( Utils.ICON_CUT );
        cut.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                getSelectedNote().setText( pane.getText() );
                quickNotesManager.syncNoteText( id );
            }
        } );

        JMenuItem copy = new JMenuItem( new DefaultEditorKit.CopyAction() );
        copy.setText( "Copy" );
        copy.setIcon( Utils.ICON_COPY );

        JMenuItem paste = new JMenuItem( new DefaultEditorKit.PasteAction() );
        paste.setText( "Paste" );
        paste.setIcon( Utils.ICON_PASTE );
        paste.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                getSelectedNote().setText( pane.getText() );
                quickNotesManager.syncNoteText( id );
            }
        } );

        JMenuItem popupNext = new JMenuItem( "Next Note", Utils.ICON_FORWARD );
        popupNext.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                goNext();
            }
        } );

        JMenuItem popupBack = new JMenuItem( "Previous Note", Utils.ICON_BACK );
        popupBack.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                goBack();
            }
        } );

        JMenuItem popupList = new JMenuItem( "List All Notes", Utils.ICON_LIST16 );
        popupList.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                listAllNotes();
            }
        } );

        JMenuItem delete = new JMenuItem( "Delete Note", Utils.ICON_DELETE );
        delete.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                deleteNote();
            }
        } );

        popupMenu.add( search );
        popupMenu.addSeparator();
        popupMenu.add( cut );
        popupMenu.add( copy );
        popupMenu.add( paste );
        popupMenu.addSeparator();
        popupMenu.add( popupNext );
        popupMenu.add( popupBack );
        popupMenu.add( popupList );
        popupMenu.addSeparator();
        popupMenu.add( delete );
        pane.addMouseListener( new PopupListener( popupMenu ) );
    }

    /**
     * @param warning
     */
    public void setWarning( boolean warning ) {
        if ( warning ) {
            notestitle.setIcon( Utils.ICON_WARNING );
            notestitle.setToolTipText( "This Note is also being edited in another IDEA instance" );
        }
        else {
            notestitle.setIcon( null );
            notestitle.setToolTipText( null );
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
    public void setText( String text ) {
        pane.setText( text );
    }

    public void setNotesFont( Font notesFont ) {
        pane.setFont( notesFont );
    }

    public JTextArea getTextArea() {
        return pane;
    }

    public void setFontColor( Color fontColor ) {
        pane.setForeground( fontColor );
    }

    public void setBackgroundColor( Color backgroundColor ) {
        this.backgroundColor = backgroundColor;
        pane.setBackground( backgroundColor );
    }

    public void setShowBackgroundLines( boolean showBackgroundLines ) {
        this.showBackgroundLines = showBackgroundLines;
    }
}

/**
 *
 */
class PopupListener
        extends MouseAdapter {
    JPopupMenu popup;

    PopupListener( JPopupMenu popupMenu ) {
        popup = popupMenu;
    }

    public void mousePressed( MouseEvent e ) {
        maybeShowPopup( e );
    }

    public void mouseReleased( MouseEvent e ) {
        maybeShowPopup( e );
    }

    private void maybeShowPopup( MouseEvent e ) {
        if ( e.isPopupTrigger() ) {
            popup.show( e.getComponent(), e.getX(), e.getY() );
        }
    }
}