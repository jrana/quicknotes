package com.jsrana.plugins.quicknotes.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.ColorChooser;
import com.intellij.ui.JBColor;
import com.intellij.util.Consumer;
import com.jsrana.plugins.quicknotes.QuickNotes;
import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import com.jsrana.plugins.quicknotes.util.Utils;
import org.jdom.Element;

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

import static com.jsrana.plugins.quicknotes.QuickNotes.PROPERTY_FILELOCATION;

public class OptionsDialog
        extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JComboBox<String> comboBoxFont;
    private JComboBox<String> comboBoxFontSize;
    private JButton buttonComments;
    private JButton buttonLicense;
    private JLabel labelWebsite;
    private JLabel labelManual;
    private JRadioButton defaultBackgroundColorRadio;
    private JRadioButton myBackgroundColorRadio;
    private JButton chooseBackgroundColorButton;
    private JRadioButton defaultLineColorRadio;
    private JRadioButton myLineColorRadio;
    private JButton chooseLineColorButton;
    private JRadioButton defaultFontColorRadio;
    private JRadioButton myFontColorRadio;
    private JButton chooseFontColorButton;
    private JLabel aboutVersionLabel;
    private JRadioButton defaultLineNumberColorRadio;
    private JRadioButton myLineNumberColorRadio;
    private JButton chooseLineNumberColorButton;
    private JButton buttonIssue;
    private JCheckBox checkBoxShowBackgroundLines;
    private JCheckBox checkBoxShowLineNumbers;
    private JCheckBox checkBoxWordWrap;
    private JLabel logoLabel;
    private JLabel fileLocationLabel;
    private JButton buttonFileLocation;
    private JTabbedPane tabbedPane;
    private Element element;

    OptionsDialog(Element element) {
        super();
        this.element = element;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Quick Notes Options");
        logoLabel.setIcon(QuickNotesIcon.QUICKNOTES_48);

        buttonOK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonOK.setIcon(QuickNotesIcon.CLOSE);
        buttonOK.setBackground(JBColor.background());
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonComments.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonComments.setIcon(QuickNotesIcon.COMMENT);
        buttonComments.setBackground(JBColor.background());
        buttonComments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Utils.openURL("https://plugins.jetbrains.com/plugin/4456-quick-notes");
            }
        });

        buttonIssue.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonIssue.setIcon(QuickNotesIcon.ALERT);
        buttonIssue.setBackground(JBColor.background());
        buttonIssue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Utils.openURL("https://github.com/jrana/quicknotes/issues");
            }
        });

        buttonLicense.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonLicense.setIcon(QuickNotesIcon.LICENSE);
        buttonLicense.setBackground(JBColor.background());
        buttonLicense.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonLicense.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                LicenseDialog dialog = new LicenseDialog();
                dialog.setLocationRelativeTo(null);
                dialog.pack();
                dialog.setVisible(true);
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        final QuickNotesManager manager = QuickNotesManager.getInstance();
        String currentFontName = manager.getNotesFont().getFontName();
        String currentFontSize = String.valueOf(manager.getNotesFont().getSize());
        String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String aFontList : fontList) {
            comboBoxFont.addItem(aFontList);
        }
        comboBoxFont.setSelectedItem(currentFontName);

        String[] fontSizes = {"8", "10", "11", "12", "14", "16", "18", "20", "24", "28", "32", "36", "40", "48", "52", "56", "64", "72", "92"};
        for (String fontSize : fontSizes) {
            comboBoxFontSize.addItem(fontSize);
        }
        comboBoxFontSize.setSelectedItem(currentFontSize);

        comboBoxFont.setBackground(JBColor.background());
        comboBoxFont.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setNotesFont(new Font(String.valueOf(comboBoxFont.getSelectedItem()), Font.PLAIN, Integer.parseInt(String.valueOf(comboBoxFontSize.getSelectedItem()))));
            }
        });

        comboBoxFontSize.setBackground(JBColor.background());
        comboBoxFontSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setNotesFont(new Font(String.valueOf(comboBoxFont.getSelectedItem()), Font.PLAIN, Integer.parseInt(String.valueOf(comboBoxFontSize.getSelectedItem()))));
            }
        });

        if (manager.isWordWrap()) {
            checkBoxShowLineNumbers.setEnabled(false);
            checkBoxShowLineNumbers.setSelected(false);
        } else {
            checkBoxShowLineNumbers.setSelected(manager.isShowLineNumbers());
        }
        checkBoxShowLineNumbers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                if (!manager.isWordWrap()) {
                    manager.setShowLineNumbers(abstractButton.getModel().isSelected());
                }
            }
        });

        checkBoxWordWrap.setSelected(manager.isWordWrap());
        checkBoxWordWrap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                manager.setWordWrap(abstractButton.getModel().isSelected());
                if (abstractButton.getModel().isSelected()) {
                    manager.setShowLineNumbers(false);
                    checkBoxShowLineNumbers.setSelected(false);
                    checkBoxShowLineNumbers.setEnabled(false);
                } else {
                    checkBoxShowLineNumbers.setEnabled(true);
                }
            }
        });

        labelWebsite.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelWebsite.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Utils.openURL("https://github.com/jrana/quicknotes");
            }
        });

        labelManual.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelManual.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Utils.openURL("http://docs.google.com/fileview?id=0B6GyR43t58eXNzQ1ZmUyOTktZDc5NS00ZWRkLTlmMGMtOGQ0ZGIyZjdhM2E0&hl=en");
            }
        });

        chooseFontColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myFontColorRadio.setSelected(true);
                Color newColor = ColorChooser.chooseColor(
                        OptionsDialog.this,
                        "Choose Font Color",
                        manager.getFontColor());
                if (newColor != null) {
                    manager.setFontColor(newColor, false);
                }
            }
        });

        defaultFontColorRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setFontColor(QuickNotesPanel.EDITOR_COLOR_FONT, true);
            }
        });

        chooseBackgroundColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myBackgroundColorRadio.setSelected(true);
                Color newColor = ColorChooser.chooseColor(
                        OptionsDialog.this,
                        "Choose Background Color",
                        manager.getBackgroundColor());
                if (newColor != null) {
                    manager.setBackgroundColor(newColor, false);
                }
            }
        });

        defaultBackgroundColorRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setBackgroundColor(QuickNotesPanel.EDITOR_COLOR_BACKGROUND, true);
            }
        });

        if (manager.isBackgroundColor_default()) {
            defaultBackgroundColorRadio.setSelected(true);
        } else {
            myBackgroundColorRadio.setSelected(true);
        }

        checkBoxShowBackgroundLines.setSelected(manager.isShowBackgroundLines());
        checkBoxShowBackgroundLines.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                manager.setShowBackgroundLines(abstractButton.getModel().isSelected());
            }
        });

        if (manager.isBackgroundLineColor_default()) {
            defaultLineColorRadio.setSelected(true);
        } else {
            myLineColorRadio.setSelected(true);
        }

        chooseLineColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myLineColorRadio.setSelected(true);
                Color newColor = ColorChooser.chooseColor(
                        OptionsDialog.this,
                        "Choose Line Color",
                        manager.getBackgroundLineColor());
                if (newColor != null) {
                    manager.setBackgroundLineColor(newColor, false);
                }
            }
        });

        defaultLineColorRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setBackgroundLineColor(QuickNotesPanel.EDITOR_COLOR_LINE, true);
            }
        });

        aboutVersionLabel.setText("Quick Notes " + QuickNotesManager.VERSION);

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

        if (manager.isLineNumberColor_default()) {
            defaultLineNumberColorRadio.setSelected(true);
        } else {
            myLineNumberColorRadio.setSelected(true);
        }
        chooseLineNumberColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myLineNumberColorRadio.setSelected(true);
                Color newColor = ColorChooser.chooseColor(
                        OptionsDialog.this,
                        "Choose Line Number Color",
                        manager.getLineNumberColor());
                if (newColor != null) {
                    manager.setLineNumberColor(newColor, false);
                }
            }
        });
        defaultLineNumberColorRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setLineNumberColor(QuickNotesPanel.EDITOR_COLOR_LINENUMBER, true);
            }
        });

        // file location
        fileLocationLabel.setText(QuickNotesManager.getFolderPath());
        buttonFileLocation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor( false, true, false, false, false, false);
                fileChooserDescriptor.setTitle("Choose Plugin File Location");
                String fileLocationPath = QuickNotesManager.getFolderPath();

                VirtualFile virtualFile = FileChooser.chooseFile(fileChooserDescriptor, null, new VirtualFileWrapper( new File( fileLocationPath) ).getVirtualFile() );
                if ( virtualFile != null ) {
                    File newFolder = new File( virtualFile.getPath() );
                    if (!newFolder.getAbsolutePath().equals(fileLocationPath)) {
                        boolean persist = true;
                        if (!newFolder.exists()) {
                            if (!newFolder.mkdir()) {
                                JOptionPane.showMessageDialog(null, "Unable to make folder. Please try again", "Error", JOptionPane.ERROR_MESSAGE);
                                persist = false;
                            }
                        }
                        if (persist) {
                            moveQuickNotesFile(manager, newFolder);
                        }
                    }
                }
/*
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setCurrentDirectory(new File(fileLocationPath));
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File newFolder = fileChooser.getSelectedFile();
                    if (!newFolder.getAbsolutePath().equals(fileLocationPath)) {
                        boolean persist = true;
                        if (!newFolder.exists()) {
                            if (!newFolder.mkdir()) {
                                JOptionPane.showMessageDialog(null, "Unable to make folder. Please try again", "Error", JOptionPane.ERROR_MESSAGE);
                                persist = false;
                            }
                        }
                        if (persist) {
                            moveQuickNotesFile(manager, newFolder);
                        }
                    }
                }
*/
            }
        });
    }

    private void moveQuickNotesFile(QuickNotesManager mgr, File newFolder) {
        String folderPath = newFolder.getAbsolutePath();
        fileLocationLabel.setText(folderPath);
        PropertiesComponent.getInstance().setValue(PROPERTY_FILELOCATION, folderPath);
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
