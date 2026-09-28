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
package com.jsrana.plugins.quicknotes.manager;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.Key;
import com.intellij.ui.JBColor;
import com.jsrana.plugins.quicknotes.ui.QuickNotesPanel;
import org.jdom.Element;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Quick Notes Panel
 *
 * @author Jitendra Rana
 */
@Service(Service.Level.APP)
public final class QuickNotesManager {
    public static final Key<String> KEY_PANELID = Key.create("panelid");
    public static final Key<QuickNotesPanel> PANEL_KEY = Key.create("quicknotesid");
    public static final String PROPERTY_FILELOCATION = "com.jsrana.plugins.quicknotes.filelocation";

    private final HashMap<String, QuickNotesPanel> panelMap;
    private int index = 0;
    private Element notesElement;
    private String enotes = "";
    public static final String VERSION = "v4.0";

    private boolean showLineNumbers = true;
    private boolean wordWrap = false;
    private Font notesFont = new Font("Arial", Font.PLAIN, 12);
    private Color fontColor = QuickNotesPanel.EDITOR_COLOR_FONT;
    private boolean fontColor_default = true;

    private Color backgroundColor = QuickNotesPanel.EDITOR_COLOR_BACKGROUND;
    private boolean backgroundColor_default = true;

    private Color lineNumberColor = QuickNotesPanel.EDITOR_COLOR_LINE;
    private boolean lineNumberColor_default = true;

    private boolean showBackgroundLines = true;
    private Color backgroundLineColor = QuickNotesPanel.EDITOR_COLOR_LINE;
    private boolean backgroundLineColor_default = true;

    public QuickNotesManager() {
        panelMap = new HashMap<>();
        notesElement = readSettings();
    }

    public static QuickNotesManager getInstance() {
        return ApplicationManager.getApplication().getService(QuickNotesManager.class);
    }

    public Element getNotesElement() {
        return notesElement;
    }

    /**
     * @param panel
     */
    public void addQuickNotesPanel(QuickNotesPanel panel) {
        panelMap.put(panel.getId(), panel);
    }

    /**
     *
     */
    public void setNoteEditWarning() {
        HashMap<String, ArrayList<QuickNotesPanel>> map = new HashMap<String, ArrayList<QuickNotesPanel>>();
        for (Object o : panelMap.keySet()) {
            QuickNotesPanel panel = panelMap.get(o);
            String index = String.valueOf(panel.getSelectedNoteIndex());
            if (!map.containsKey(index)) {
                map.put(index, new ArrayList<QuickNotesPanel>());
            }
            (map.get(index)).add(panel);
        }

        for (String key : map.keySet()) {
            List<QuickNotesPanel> list = map.get(key);
            if (list.size() > 1) {
                for (QuickNotesPanel aList : list) {
                    (aList).setWarning(true);
                }
            } else if (list.size() == 1) {
                (list.get(0)).setWarning(false);
            }
        }
        map.clear();
    }

    /**
     * @param panelid
     */
    public void syncQuickNotePanels(String panelid) {
        if (panelid != null) {
            for (String id : panelMap.keySet()) {
                if (id != null && !panelid.equals(id)) {
                    QuickNotesPanel qnp = panelMap.get(id);
                    int index = qnp.getSelectedNoteIndex();
                    if (index == qnp.element.getChildren().size()) {
                        index--;
                    }
                    qnp.selectNote(index, false);
                }
            }
        }
    }

    /**
     * Getter for property 'nextPanelID'.
     *
     * @return Value for property 'nextPanelID'.
     */
    public String getNextPanelID() {
        return "panel_" + index++;
    }

    /**
     * @param panel
     */
    public void clearLocks(QuickNotesPanel panel) {
        if (panel == null) {
            return;
        }
        panelMap.remove(panel.getId());
        setNoteEditWarning();
    }

    /**
     * @return
     */
    public boolean isWordWrap() {
        return wordWrap;
    }

    /**
     * @param wordWrap
     */
    public void setWordWrap(boolean wordWrap) {
        this.wordWrap = wordWrap;
        for (String id : panelMap.keySet()) {
            if (id != null) {
                QuickNotesPanel qnp = panelMap.get(id);
                qnp.getTextArea().setLineWrap(wordWrap);
                qnp.getTextArea().setWrapStyleWord(wordWrap);
            }
        }
    }

    public static String getFolderPath() {
        return PropertiesComponent.getInstance().getValue(PROPERTY_FILELOCATION, System.getProperty("user.home") + System.getProperty("file.separator") + ".ideaquicknotes");
    }

    /**
     * Returns the settings file. Creates the setting folder and file if not found.
     *
     * @return File
     */
    public static File getSettingsFile() {
        File settingsFile = null;
        File fileLocationFolder = new File(getFolderPath());
        try {
            if (!fileLocationFolder.exists()) {
                if (fileLocationFolder.mkdir()) {
                    settingsFile = new File(fileLocationFolder, "ideaquicknotes.xml");
                    if (!settingsFile.exists()) {
                        settingsFile.createNewFile();
                    }
                }
            } else {
                settingsFile = new File(fileLocationFolder, "ideaquicknotes.xml");
                if (!settingsFile.exists()) {
                    settingsFile.createNewFile();
                }
            }
        } catch (IOException e) {
            settingsFile = null;
        }
        return settingsFile;
    }

    /**
     * Save the settings to ideaquicknotes.xml
     */
    public boolean saveSettings(Element element) {
        File settingsFile = getSettingsFile();
        if (settingsFile == null) {
            return false;
        }
        try {
            element.setAttribute("showlinenumbers", isShowLineNumbers() ? "Y" : "N");
            element.setAttribute("wordwrap", isWordWrap() ? "Y" : "N");

            Font font = getNotesFont();
            element.setAttribute("fontname", font.getFontName());
            element.setAttribute("fontsize", String.valueOf(font.getSize()));

            Color fontColor = getFontColor();
            element.setAttribute("fontColorDefault", fontColor_default ? "Y" : "N");
            element.setAttribute("fontColorRed", String.valueOf(fontColor.getRed()));
            element.setAttribute("fontColorGreen", String.valueOf(fontColor.getGreen()));
            element.setAttribute("fontColorBlue", String.valueOf(fontColor.getBlue()));

            Color bgColor = getBackgroundColor();
            element.setAttribute("bgColorDefault", isBackgroundColor_default() ? "Y" : "N");
            element.setAttribute("bgColorRed", String.valueOf(bgColor.getRed()));
            element.setAttribute("bgColorGreen", String.valueOf(bgColor.getGreen()));
            element.setAttribute("bgColorBlue", String.valueOf(bgColor.getBlue()));

            Color bgLineColor = getBackgroundLineColor();
            element.setAttribute("bgLineColorShow", isShowBackgroundLines() ? "Y" : "N");
            element.setAttribute("bgLineColorDefault", isBackgroundLineColor_default() ? "Y" : "N");
            element.setAttribute("bgLineColorRed", String.valueOf(bgLineColor.getRed()));
            element.setAttribute("bgLineColorGreen", String.valueOf(bgLineColor.getGreen()));
            element.setAttribute("bgLineColorBlue", String.valueOf(bgLineColor.getBlue()));

            Color lineNumberColor = getLineNumberColor();
            element.setAttribute("lineNumberColorDefault", isLineNumberColor_default() ? "Y" : "N");
            element.setAttribute("lineNumberColorRed", String.valueOf(lineNumberColor.getRed()));
            element.setAttribute("lineNumberColorGreen", String.valueOf(lineNumberColor.getGreen()));
            element.setAttribute("lineNumberColorBlue", String.valueOf(lineNumberColor.getBlue()));

            JDOMUtil.write(element, settingsFile.toPath(), System.lineSeparator());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void saveNotes() {
        if (notesElement == null) {
            return;
        }
        if (saveSettings(notesElement)) {
            enotes = "";
        } else {
            enotes = JDOMUtil.writeElement(notesElement);
        }
    }

    /**
     * @param panelid
     */
    public void syncNoteText(String panelid) {
        QuickNotesPanel panel = panelMap.get(panelid);
        for (Object o : panelMap.keySet()) {
            String id = (String) o;
            if (id != null && !panelid.equals(id)) {
                QuickNotesPanel qnp = panelMap.get(id);
                if (qnp.getSelectedNoteIndex() == panel.getSelectedNoteIndex()) {
                    qnp.setText(panel.getText());
                }
            }
        }
    }

    public boolean isShowLineNumbers() {
        return showLineNumbers;
    }

    public void setShowLineNumbers(boolean showLineNumbers) {
        this.showLineNumbers = showLineNumbers;
        for (String id : panelMap.keySet()) {
            if (id != null) {
                QuickNotesPanel qnp = panelMap.get(id);
                qnp.getTextArea().repaint();
            }
        }
    }

    public Font getNotesFont() {
        return notesFont;
    }

    public void setNotesFont(Font notesFont) {
        this.notesFont = notesFont;
        for (String id : panelMap.keySet()) {
            if (id != null) {
                QuickNotesPanel qnp = panelMap.get(id);
                qnp.setNotesFont(notesFont);
            }
        }
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color newColor,
                             boolean defaultColor) {
        fontColor_default = false;
        fontColor = newColor;
        for (String id : panelMap.keySet()) {
            if (id != null) {
                QuickNotesPanel qnp = panelMap.get(id);
                qnp.setFontColor(fontColor);
            }
        }
    }

    public void setFontColor_default(boolean fontColor_default) {
        this.fontColor_default = fontColor_default;
    }

    public boolean isFontColor_default() {
        return fontColor_default;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color newColor,
                                   boolean defaultColor) {
        backgroundColor_default = defaultColor;
        backgroundColor = newColor;
        for (String id : panelMap.keySet()) {
            if (id != null) {
                QuickNotesPanel qnp = panelMap.get(id);
                qnp.setBackgroundColor(backgroundColor);
            }
        }
    }

    public void setBackgroundColor_default(boolean backgroundColor_default) {
        this.backgroundColor_default = backgroundColor_default;
    }

    public boolean isBackgroundColor_default() {
        return backgroundColor_default;
    }

    public void setShowBackgroundLines(boolean showBackgroundLines) {
        this.showBackgroundLines = showBackgroundLines;
        for (String id : panelMap.keySet()) {
            if (id != null) {
                QuickNotesPanel qnp = panelMap.get(id);
                qnp.setShowBackgroundLines(showBackgroundLines);
            }
        }
    }

    public boolean isShowBackgroundLines() {
        return showBackgroundLines;
    }

    public Color getBackgroundLineColor() {
        return backgroundLineColor;
    }

    public void setBackgroundLineColor(Color backgroundLineColor,
                                       boolean defaultColor) {
        backgroundLineColor_default = defaultColor;
        this.backgroundLineColor = backgroundLineColor;
    }

    public Color getLineNumberColor() {
        return lineNumberColor;
    }

    public void setLineNumberColor(Color lineNumberColor,
                                   boolean defaultColor) {
        lineNumberColor_default = defaultColor;
        this.lineNumberColor = lineNumberColor;
    }

    public boolean isBackgroundLineColor_default() {
        return backgroundLineColor_default;
    }

    public void setBackgroundLineColor_default(boolean backgroundLineColor_default) {
        this.backgroundLineColor_default = backgroundLineColor_default;
    }

    public boolean isLineNumberColor_default() {
        return lineNumberColor_default;
    }

    public void setLineNumberColor_default(boolean lineNumberColor_default) {
        this.lineNumberColor_default = lineNumberColor_default;
    }

    public QuickNotesPanel getQuickNotesPanel(String panelid) {
        return panelMap.get(panelid);
    }

    private Element readSettings() {
        Element element = null;
        File settingsFile = getSettingsFile();
        try {
            if (settingsFile != null && settingsFile.length() > 0) {
                element = JDOMUtil.load(settingsFile);
            } else if (enotes != null && !enotes.isBlank()) {
                element = JDOMUtil.load(new StringReader(enotes));
            }
        } catch (Exception ignored) {
            // keep default notes when the settings file cannot be read
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

        setShowLineNumbers("Y".equals(element.getAttributeValue("showlinenumbers")));
        setWordWrap("Y".equals(element.getAttributeValue("wordwrap")));
        int fontsize = 12;
        String fontsizeValue = element.getAttributeValue("fontsize");
        if (fontsizeValue != null) {
            try {
                fontsize = Integer.parseInt(fontsizeValue);
            } catch (NumberFormatException ignored) {
                // keep the default size
            }
        }
        setNotesFont(new Font(element.getAttributeValue("fontname"), Font.PLAIN, fontsize));

        setFontColor_default("Y".equals(element.getAttributeValue("fontColorDefault")));
        if (!isFontColor_default()) {
            setFontColor(new JBColor(readColor(element, "fontColor", QuickNotesPanel.EDITOR_COLOR_FONT),
                    readColor(element, "fontColor", QuickNotesPanel.EDITOR_COLOR_FONT)), false);
        }

        setBackgroundColor_default("Y".equals(element.getAttributeValue("bgColorDefault")));
        if (!isBackgroundColor_default()) {
            setBackgroundColor(new JBColor(readColor(element, "bgColor", QuickNotesPanel.EDITOR_COLOR_BACKGROUND),
                    readColor(element, "bgColor", QuickNotesPanel.EDITOR_COLOR_BACKGROUND)), false);
        }

        setShowBackgroundLines("Y".equals(element.getAttributeValue("bgLineColorShow")));
        setBackgroundLineColor_default("Y".equals(element.getAttributeValue("bgLineColorDefault")));
        if (!isBackgroundLineColor_default()) {
            setBackgroundLineColor(new JBColor(readColor(element, "bgLineColor", QuickNotesPanel.EDITOR_COLOR_LINE),
                    readColor(element, "bgLineColor", QuickNotesPanel.EDITOR_COLOR_LINE)), false);
        }

        setLineNumberColor_default("Y".equals(element.getAttributeValue("lineNumberColorDefault")));
        if (!isLineNumberColor_default()) {
            setLineNumberColor(new JBColor(readColor(element, "lineNumberColor", QuickNotesPanel.EDITOR_COLOR_LINENUMBER),
                    readColor(element, "lineNumberColor", QuickNotesPanel.EDITOR_COLOR_LINENUMBER)), false);
        }

        List<Element> notes = element.getChildren();
        if (notes.isEmpty()) {
            Element note = new Element("note");
            note.setAttribute("title", QuickNotesPanel.titleFormat.format(new Date()));
            note.setAttribute("createdt", QuickNotesPanel.sdf.format(new Date()));
            note.setText("Enter your notes here...");
            element.addContent(note);
        } else {
            for (Element note : notes) {
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

    private static Color readColor(Element element, String prefix, Color fallback) {
        return new Color(
                colorComponent(element, prefix + "Red", fallback.getRed()),
                colorComponent(element, prefix + "Green", fallback.getGreen()),
                colorComponent(element, prefix + "Blue", fallback.getBlue()));
    }

    private static int colorComponent(Element element, String attribute, int fallback) {
        String value = element.getAttributeValue(attribute);
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
