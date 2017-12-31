package com.jsrana.plugins.quicknotes.ui;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;
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

public class OptionsDialog
        extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JComboBox<String> comboBoxFont;
    private JComboBox<String> comboBoxFontSize;
    private JComboBox<String> comboBoxLocation;
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

    OptionsDialog() {
        super();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Quick Notes Options");

        buttonOK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonOK.setIcon(IconLoader.getIcon("/resources/flat/close.png"));
        buttonOK.setBackground(JBColor.background());
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonComments.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonComments.setIcon(IconLoader.getIcon("/resources/flat/comment.png"));
        buttonComments.setBackground(JBColor.background());
        buttonComments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Utils.openURL("http://plugins.intellij.net/plugin/?id=4456");
            }
        });

        buttonIssue.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonIssue.setIcon(IconLoader.getIcon("/resources/flat/alert.png"));
        buttonIssue.setBackground(JBColor.background());
        buttonIssue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Utils.openURL("https://github.com/jrana/quicknotes/issues");
            }
        });

        buttonLicense.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonLicense.setIcon(IconLoader.getIcon("/resources/flat/license.png"));
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

        String[] fontSizes = {"8", "10", "11", "12", "14", "16", "18", "20", "24"};
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

        comboBoxLocation.setBackground(JBColor.background());
        comboBoxLocation.addItem("Top");
        comboBoxLocation.addItem("Bottom");
        comboBoxLocation.setSelectedItem(manager.getToolbarLocation() == QuickNotesManager.TOOLBARLOCATION_BOTTOM ? "Bottom" : "Top");
        comboBoxLocation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.setToolBarLocation("Top".equals(comboBoxLocation.getSelectedItem()) ? QuickNotesManager.TOOLBARLOCATION_TOP : QuickNotesManager.TOOLBARLOCATION_BOTTOM);
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
                Color newColor = JColorChooser.showDialog(
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
                Color newColor = JColorChooser.showDialog(
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
                Color newColor = JColorChooser.showDialog(
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
                Color newColor = JColorChooser.showDialog(
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
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
