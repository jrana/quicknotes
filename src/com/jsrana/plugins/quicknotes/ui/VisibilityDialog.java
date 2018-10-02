package com.jsrana.plugins.quicknotes.ui;

import com.intellij.ui.components.JBLabel;
import org.jdom.Element;

import javax.swing.*;
import java.awt.event.*;

public class VisibilityDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonClose;
    private JButton buttonCancel;
    private JRadioButton radioGlobal;
    private JRadioButton radioProject;
    private JRadioButton radioFile;
    private JBLabel labelGlobal;
    private JBLabel labelProject;
    private JBLabel labelFile;
    private Element element;

    public VisibilityDialog(Element element) {
        this.element = element;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonClose);
        setTitle("Quick Notes :: Set Note Visibility Level");

        buttonClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        labelGlobal.setIcon(QuickNotesIcon.GLOBE);
        labelProject.setIcon(QuickNotesIcon.FOLDER);
        labelFile.setIcon(QuickNotesIcon.FILE);
        buttonClose.setIcon(QuickNotesIcon.CHECK);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        VisibilityDialog dialog = new VisibilityDialog(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
