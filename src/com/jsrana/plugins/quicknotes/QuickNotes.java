/*
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
package com.jsrana.plugins.quicknotes;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import com.jsrana.plugins.quicknotes.ui.QuickNotesIcon;
import com.jsrana.plugins.quicknotes.ui.QuickNotesPanel;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.InputSource;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Quick Notes is an IntelliJ IDEA Plugin that provides saving notes in IDEA itself
 *
 * @author Jitendra Rana
 */
public class QuickNotes
        implements ApplicationComponent {
    private String enotes = "";
    private Element notesElement;
    public static final Key KEY_PANELID = new Key("panelid");
    public static final String PROPERTY_FILELOCATION = "com.jsrana.plugins.quicknotes.filelocation";

    /**
     * Constructor
     */
    public QuickNotes() {
    }

    /**
     * Unique name of this component. If there is another component with the same name or
     * name is null internal assertion will occur.
     *
     * @return the name of this component
     */
    @NonNls
    @NotNull
    public String getComponentName() {
        return "Quick Notes by Jitendra Rana";
    }

    /**
     * Component should do initialization and communication with another components in this method.
     */
    public void initComponent() {
        notesElement = readSettings();
        ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerListener() {
            Key quicknoteskey = new Key("quicknotesid");

            public void projectOpened(final Project project) {
                final QuickNotesPanel quickNotesPanel = new QuickNotesPanel(notesElement);
                final ToolWindowManager twm = ToolWindowManager.getInstance(project);

                Runnable task1 = new Runnable() {
                    @Override
                    public void run() {
                        ToolWindow toolWindow = twm.registerToolWindow("Quick Notes", false, ToolWindowAnchor.RIGHT);
                        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                        Content content = contentFactory.createContent(quickNotesPanel.getRootComponent(), "", false);
                        toolWindow.getContentManager().addContent(content);
                        toolWindow.setIcon(QuickNotesIcon.QUICKNOTES);
                        project.putUserData(quicknoteskey, quickNotesPanel);
                    }
                };
                twm.invokeLater(task1);
            }

            public void projectClosed(final Project project) {
                // clear locks
                QuickNotesManager.getInstance().clearLocks((QuickNotesPanel) project.getUserData(quicknoteskey));

                // save data
                if (QuickNotesManager.saveSettings(notesElement)) {
                    enotes = "";
                } else {
                    enotes = new XMLOutputter().outputString(notesElement);
                }
            }
        });
    }

    /**
     * Component should dispose system resources or perform another cleanup in this method.
     */
    public void disposeComponent() {
    }

    /**
     * This method reads the settings from file ideaquicknotes.xml in user's home directory.
     */
    private Element readSettings() {
        Element element = null;
        File settingsFile = QuickNotesManager.getSettingsFile();
        SAXBuilder builder = new SAXBuilder();
        try {
            if (settingsFile != null) {
                // settingsfile is not null, which indicates that reading of setting file is successful and data can be
                // managed in the settings file
                if (settingsFile.length() == 0) {
                    if (enotes != null && enotes.trim().length() > 0) {
                        element = builder.build(new InputSource(new StringReader(enotes))).getRootElement();
                    }
                } else {
                    FileInputStream fis = new FileInputStream(settingsFile);
                    element = builder.build(fis).getRootElement();
                    fis.close();
                }
            } else if (enotes != null && enotes.length() > 0) {
                element = builder.build(new InputSource(new StringReader(enotes))).getRootElement();
            }
        } catch (Exception e) {
            // ignore
        }

        if (element == null) {
            element = new Element("notes");
            element.setAttribute("createdt", QuickNotesPanel.sdf.format(new Date()));
            element.setAttribute("selectednoteindex", "0");
            element.setAttribute("showlinenumbers", "Y");
            element.setAttribute("toolbarlocation", "0");
            element.setAttribute("fontname", "Arial");
            element.setAttribute("fontsize", "12");
            element.setAttribute("wordwrap", "N");
            element.setAttribute("fontColorDefault", "Y");
            element.setAttribute("fontColorRed", "0");
            element.setAttribute("fontColorGreen", "0");
            element.setAttribute("fontColorBlue", "0");

            Color bgColor = QuickNotesPanel.EDITOR_COLOR_BACKGROUND;
            element.setAttribute("bgColorDefault", "Y");
            element.setAttribute("bgColorRed", String.valueOf(bgColor.getRed()));
            element.setAttribute("bgColorGreen", String.valueOf(bgColor.getGreen()));
            element.setAttribute("bgColorBlue", String.valueOf(bgColor.getBlue()));

            Color bgLineColor = QuickNotesPanel.EDITOR_COLOR_LINE;
            element.setAttribute("bgLineColorShow", "Y");
            element.setAttribute("bgLineColorDefault", "Y");
            element.setAttribute("bgLineColorRed", String.valueOf(bgLineColor.getRed()));
            element.setAttribute("bgLineColorGreen", String.valueOf(bgLineColor.getGreen()));
            element.setAttribute("bgLineColorBlue", String.valueOf(bgLineColor.getBlue()));

            Color lineNumberColor = QuickNotesPanel.EDITOR_COLOR_LINENUMBER;
            element.setAttribute("lineNumberColorDefault", "Y");
            element.setAttribute("lineNumberColorRed", String.valueOf(lineNumberColor.getRed()));
            element.setAttribute("lineNumberColorGreen", String.valueOf(lineNumberColor.getGreen()));
            element.setAttribute("lineNumberColorBlue", String.valueOf(lineNumberColor.getBlue()));
        }

        QuickNotesManager mgr = QuickNotesManager.getInstance();
        mgr.setShowLineNumbers("Y".equals(element.getAttributeValue("showlinenumbers")));
        mgr.setWordWrap("Y".equals(element.getAttributeValue("wordwrap")));
        int fontsize = 12;
        try {
            fontsize = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("fontsize")));
        } catch (NumberFormatException e) {
            // ignore
        }
        mgr.setNotesFont(new Font(element.getAttributeValue("fontname"), Font.PLAIN, fontsize));

        // set font color
        mgr.setFontColor_default("Y".equals(element.getAttributeValue("fontColorDefault")));
        if (!mgr.isFontColor_default()) {
            int red;
            try {
                red = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("fontColorRed")));
            } catch (NumberFormatException e) {
                red = QuickNotesPanel.EDITOR_COLOR_FONT.getRed();
            }
            int green;
            try {
                green = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("fontColorGreen")));
            } catch (NumberFormatException e) {
                green = QuickNotesPanel.EDITOR_COLOR_FONT.getGreen();
            }
            int blue;
            try {
                blue = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("fontColorBlue")));
            } catch (NumberFormatException e) {
                blue = QuickNotesPanel.EDITOR_COLOR_FONT.getBlue();
            }
            mgr.setFontColor(new JBColor(new Color(red, green, blue), new Color(red, green, blue)), false);
        }

        // set background color
        mgr.setBackgroundColor_default("Y".equals(element.getAttributeValue("bgColorDefault")));
        if (!mgr.isBackgroundColor_default()) {
            int red;
            try {
                red = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("bgColorRed")));
            } catch (NumberFormatException e) {
                red = QuickNotesPanel.EDITOR_COLOR_BACKGROUND.getRed();
            }
            int green;
            try {
                green = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("bgColorGreen")));
            } catch (NumberFormatException e) {
                green = QuickNotesPanel.EDITOR_COLOR_BACKGROUND.getGreen();
            }
            int blue;
            try {
                blue = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("bgColorBlue")));
            } catch (NumberFormatException e) {
                blue = QuickNotesPanel.EDITOR_COLOR_BACKGROUND.getBlue();
            }
            mgr.setBackgroundColor(new JBColor(new Color(red, green, blue), new Color(red, green, blue)), false);
        }

        // set line properties
        mgr.setShowBackgroundLines("Y".equals(element.getAttributeValue("bgLineColorShow")));
        mgr.setBackgroundLineColor_default("Y".equals(element.getAttributeValue("bgLineColorDefault")));
        if (!mgr.isBackgroundLineColor_default()) {
            int red;
            try {
                red = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("bgLineColorRed")));
            } catch (NumberFormatException e) {
                red = QuickNotesPanel.EDITOR_COLOR_LINE.getRed();
            }
            int green;
            try {
                green = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("bgLineColorGreen")));
            } catch (NumberFormatException e) {
                green = QuickNotesPanel.EDITOR_COLOR_LINE.getGreen();
            }
            int blue;
            try {
                blue = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("bgLineColorBlue")));
            } catch (NumberFormatException e) {
                blue = QuickNotesPanel.EDITOR_COLOR_LINE.getBlue();
            }
            mgr.setBackgroundLineColor(new JBColor(new Color(red, green, blue), new Color(red, green, blue)), false);
        }

        // set line number color
        mgr.setLineNumberColor_default("Y".equals(element.getAttributeValue("lineNumberColorDefault")));
        if (!mgr.isLineNumberColor_default()) {
            int red;
            try {
                red = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("lineNumberColorRed")));
            } catch (NumberFormatException e) {
                red = QuickNotesPanel.EDITOR_COLOR_LINENUMBER.getRed();
            }
            int green;
            try {
                green = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("lineNumberColorGreen")));
            } catch (NumberFormatException e) {
                green = QuickNotesPanel.EDITOR_COLOR_LINENUMBER.getGreen();
            }
            int blue;
            try {
                blue = Integer.parseInt(Objects.requireNonNull(element.getAttributeValue("lineNumberColorBlue")));
            } catch (NumberFormatException e) {
                blue = QuickNotesPanel.EDITOR_COLOR_LINENUMBER.getBlue();
            }
            mgr.setLineNumberColor(new JBColor(new Color(red, green, blue), new Color(red, green, blue)), false);
        }

        List notes = element.getChildren();
        if (notes.size() == 0) {
            Element note = new Element("note");
            note.setAttribute("title", QuickNotesPanel.titleFormat.format(new Date()));
            note.setAttribute("createdt", QuickNotesPanel.sdf.format(new Date()));
            note.setText("Enter your notes here...");
            element.addContent(note);
        } else {
            for (Object note1 : notes) {
                Element note = (Element) note1;
                if (note.getAttributeValue("title") == null) {
                    note.setAttribute("title", "New Note");
                }
                if (note.getAttributeValue("createdt") == null) {
                    note.setAttribute("createdt", SimpleDateFormat.getInstance().format(new Date()));
                }
            }
        }

        return element;
    }
}
