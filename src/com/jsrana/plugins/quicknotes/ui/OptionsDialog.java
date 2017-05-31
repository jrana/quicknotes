package com.jsrana.plugins.quicknotes.ui;

import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import com.jsrana.plugins.quicknotes.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class OptionsDialog
        extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JComboBox comboBoxFont;
    private JComboBox comboBoxFontSize;
    private JComboBox comboBoxLocation;
    private JLabel showLineNumberLabel;
    private JButton urlButton;
    private JButton licenseButton;
    private JLabel labelWebsite;
    private JLabel labelManual;
    private JLabel wordWrapLabel;
    private JRadioButton defaultBackgroundColorRadio;
    private JRadioButton myBackgroundColorRadio;
    private JButton chooseBackgroundColorButton;
    private JLabel showLinesLabel;
    private JRadioButton defaultLineColorRadio;
    private JRadioButton myLineColorRadio;
    private JButton chooseLineColorButton;
    private JRadioButton defaultFontColorRadio;
    private JRadioButton myFontColorRadio;
    private JButton chooseFontColorButton;
    private JLabel aboutVersionLabel;
/*
    private JButton chooseFileLocationButton;
    private JLabel labelFileLocation;

*/
    protected String fontSizes[] = {"8", "10", "11", "12", "14", "16", "18", "20", "24"};
    private boolean showLineNumber;
    private boolean wordwrap;
    private boolean showBackgroundLines;

    public OptionsDialog() {
        super();
        setContentPane( contentPane );
        setModal( true );
        getRootPane().setDefaultButton( buttonOK );
        setTitle( "Quick Notes Options" );

        buttonOK.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onOK();
            }
        } );

        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                onCancel();
            }
        } );

        contentPane.registerKeyboardAction( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

        final QuickNotesManager manager = QuickNotesManager.getInstance();
        String currentFontName = manager.getNotesFont().getFontName();
        String currentFontSize = String.valueOf( manager.getNotesFont().getSize() );
        String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for ( String aFontList : fontList ) {
            comboBoxFont.addItem( aFontList );
        }
        comboBoxFont.setSelectedItem( currentFontName );

        for ( String fontSize : fontSizes ) {
            comboBoxFontSize.addItem( fontSize );
        }
        comboBoxFontSize.setSelectedItem( currentFontSize );

        comboBoxFont.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                manager.setNotesFont( new Font( String.valueOf( comboBoxFont.getSelectedItem() ), Font.PLAIN, Integer.parseInt( String.valueOf( comboBoxFontSize.getSelectedItem() ) ) ) );
            }
        } );
        comboBoxFontSize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                manager.setNotesFont( new Font( String.valueOf( comboBoxFont.getSelectedItem() ), Font.PLAIN, Integer.parseInt( String.valueOf( comboBoxFontSize.getSelectedItem() ) ) ) );
            }
        } );

        comboBoxLocation.addItem( "Top" );
        comboBoxLocation.addItem( "Bottom" );
        comboBoxLocation.setSelectedItem( manager.getToolbarLocation() == QuickNotesManager.TOOLBARLOCATION_BOTTOM ? "Bottom" : "Top" );
        comboBoxLocation.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                manager.setToolBarLocation( "Top".equals( comboBoxLocation.getSelectedItem() ) ? QuickNotesManager.TOOLBARLOCATION_TOP : QuickNotesManager.TOOLBARLOCATION_BOTTOM );
            }
        } );

        showLineNumber = manager.isShowLineNumbers();
        showLineNumberLabel.setIcon( showLineNumber ? Utils.ICON_ON : Utils.ICON_OFF );
        showLineNumberLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        showLineNumberLabel.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if ( !wordwrap ) {
                    showLineNumber = !showLineNumber;
                    manager.setShowLineNumbers( showLineNumber );
                    showLineNumberLabel.setIcon( showLineNumber ? Utils.ICON_ON : Utils.ICON_OFF );
                }
            }
        } );

        wordwrap = manager.isWordWrap();
        wordWrapLabel.setIcon( wordwrap ? Utils.ICON_ON : Utils.ICON_OFF );
        wordWrapLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        wordWrapLabel.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                wordwrap = !wordwrap;
                manager.setWordWrap( wordwrap );
                wordWrapLabel.setIcon( wordwrap ? Utils.ICON_ON : Utils.ICON_OFF );
                if ( wordwrap ) {
                    showLineNumber = false;
                    manager.setShowLineNumbers( false );
                    showLineNumberLabel.setIcon( Utils.ICON_OFF );
                    showLineNumberLabel.setEnabled( false );
                }
                else {
                    showLineNumberLabel.setEnabled( true );
                }
            }
        } );

        urlButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Utils.openURL( "http://plugins.intellij.net/plugin/?id=4456" );
            }
        } );

        licenseButton.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        licenseButton.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                LicenseDialog dialog = new LicenseDialog();
                dialog.setLocationRelativeTo( null );
                dialog.pack();
                dialog.setVisible( true );
            }
        } );

        labelWebsite.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        labelWebsite.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                Utils.openURL( "https://github.com/jrana/quicknotes" );
            }
        } );

        labelManual.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        labelManual.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                Utils.openURL( "http://docs.google.com/fileview?id=0B6GyR43t58eXNzQ1ZmUyOTktZDc5NS00ZWRkLTlmMGMtOGQ0ZGIyZjdhM2E0&hl=en" );
            }
        } );

        chooseFontColorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                myFontColorRadio.setSelected( true );
                Color newColor = JColorChooser.showDialog(
                        OptionsDialog.this,
                        "Choose Font Color",
                        manager.getFontColor() );
                if ( newColor != null ) {
                    manager.setFontColor( newColor, false );
                }
            }
        } );

        defaultFontColorRadio.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                manager.setFontColor( QuickNotesPanel.EDITOR_COLOR_FONT, true );
            }
        } );

        chooseBackgroundColorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                myBackgroundColorRadio.setSelected( true );
                Color newColor = JColorChooser.showDialog(
                        OptionsDialog.this,
                        "Choose Background Color",
                        manager.getBackgroundColor() );
                if ( newColor != null ) {
                    manager.setBackgroundColor( newColor, false );
                }
            }
        } );

        defaultBackgroundColorRadio.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                manager.setBackgroundColor( QuickNotesPanel.EDITOR_COLOR_BACKGROUND, true );
            }
        } );

        if ( manager.isBackgroundColor_default() ) {
            defaultBackgroundColorRadio.setSelected( true );
        }
        else {
            myBackgroundColorRadio.setSelected( true );
        }

        showLinesLabel.setIcon( manager.isShowBackgroundLines() ? Utils.ICON_ON : Utils.ICON_OFF );
        showLinesLabel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        showLinesLabel.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                showBackgroundLines = !showBackgroundLines;
                manager.setShowBackgroundLines( showBackgroundLines );
                showLinesLabel.setIcon( showBackgroundLines ? Utils.ICON_ON : Utils.ICON_OFF );
            }
        } );

        if ( manager.isBackgroundLineColor_default() ) {
            defaultLineColorRadio.setSelected( true );
        }
        else {
            myLineColorRadio.setSelected( true );
        }

        chooseLineColorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                myLineColorRadio.setSelected( true );
                Color newColor = JColorChooser.showDialog(
                        OptionsDialog.this,
                        "Choose Line Color",
                        manager.getBackgroundLineColor() );
                if ( newColor != null ) {
                    manager.setBackgroundLineColor( newColor, false );
                }
            }
        } );

        defaultLineColorRadio.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                manager.setBackgroundLineColor( QuickNotesPanel.EDITOR_COLOR_LINE, true );
            }
        } );

        aboutVersionLabel.setText( "Quick Notes " + QuickNotesManager.VERSION );

/*
        labelFileLocation.setText( QuickNotesManager.getInstance().getFileLocation_default() );
        chooseFileLocationButton.addActionListener( new ActionListener() {
            @Override public void actionPerformed( ActionEvent e ) {
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
                fileChooser.setCurrentDirectory( new File( QuickNotesManager.getInstance().getFileLocation_default() ) );
                int returnVal = fileChooser.showOpenDialog( null );
                if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                    File file = fileChooser.getSelectedFile();
                    QuickNotesManager.getInstance().setFileLocation_default( file.getAbsolutePath() );
                }
            }
        } );
*/
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
