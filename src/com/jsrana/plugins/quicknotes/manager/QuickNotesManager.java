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
import com.jsrana.plugins.quicknotes.ui.QuickNotesPanel;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.jsrana.plugins.quicknotes.QuickNotes.PROPERTY_FILELOCATION;

/**
 * Quick Notes Panel
 *
 * @author Jitendra Rana
 */
public class QuickNotesManager {
    private HashMap<String, QuickNotesPanel> panelMap;
    private int index = 0;
    private static QuickNotesManager instance = new QuickNotesManager();
    public static final String VERSION = "v3.3";

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

    /**
     * Do not instantiate QuickNotesManager.
     */
    private QuickNotesManager() {
        panelMap = new HashMap<>();
    }

    /**
     * Getter for property 'instance'.
     *
     * @return Value for property 'instance'.
     */
    public static QuickNotesManager getInstance() {
        return instance;
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
    public static boolean saveSettings(Element element) {
        // Get an instane of XMLOutputter
        XMLOutputter outputter = new XMLOutputter();
        File settingsFile = getSettingsFile();
        if (settingsFile != null) {
            try {
                QuickNotesManager mgr = QuickNotesManager.getInstance();
                element.setAttribute("showlinenumbers", mgr.isShowLineNumbers() ? "Y" : "N");
                element.setAttribute("wordwrap", mgr.isWordWrap() ? "Y" : "N");

                Font font = mgr.getNotesFont();
                element.setAttribute("fontname", font.getFontName());
                element.setAttribute("fontsize", String.valueOf(font.getSize()));

                Color fontColor = mgr.getFontColor();
                element.setAttribute("fontColorDefault", mgr.fontColor_default ? "Y" : "N");
                element.setAttribute("fontColorRed", String.valueOf(fontColor.getRed()));
                element.setAttribute("fontColorGreen", String.valueOf(fontColor.getGreen()));
                element.setAttribute("fontColorBlue", String.valueOf(fontColor.getBlue()));

                Color bgColor = mgr.getBackgroundColor();
                element.setAttribute("bgColorDefault", mgr.isBackgroundColor_default() ? "Y" : "N");
                element.setAttribute("bgColorRed", String.valueOf(bgColor.getRed()));
                element.setAttribute("bgColorGreen", String.valueOf(bgColor.getGreen()));
                element.setAttribute("bgColorBlue", String.valueOf(bgColor.getBlue()));

                Color bgLineColor = mgr.getBackgroundLineColor();
                element.setAttribute("bgLineColorShow", mgr.isShowBackgroundLines() ? "Y" : "N");
                element.setAttribute("bgLineColorDefault", mgr.isBackgroundLineColor_default() ? "Y" : "N");
                element.setAttribute("bgLineColorRed", String.valueOf(bgLineColor.getRed()));
                element.setAttribute("bgLineColorGreen", String.valueOf(bgLineColor.getGreen()));
                element.setAttribute("bgLineColorBlue", String.valueOf(bgLineColor.getBlue()));

                Color lineNumberColor = mgr.getLineNumberColor();
                element.setAttribute("lineNumberColorDefault", mgr.isLineNumberColor_default() ? "Y" : "N");
                element.setAttribute("lineNumberColorRed", String.valueOf(lineNumberColor.getRed()));
                element.setAttribute("lineNumberColorGreen", String.valueOf(lineNumberColor.getGreen()));
                element.setAttribute("lineNumberColorBlue", String.valueOf(lineNumberColor.getBlue()));

                FileOutputStream fos = new FileOutputStream(settingsFile);
                outputter.setFormat(Format.getPrettyFormat()); // make it Pretty!!!
                outputter.output(element, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                return false;
            }
        }
        return true;
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
                qnp.getTextArea().repaint();
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
}
