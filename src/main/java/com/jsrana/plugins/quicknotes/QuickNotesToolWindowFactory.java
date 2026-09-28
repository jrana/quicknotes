package com.jsrana.plugins.quicknotes;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import com.jsrana.plugins.quicknotes.ui.QuickNotesPanel;
import org.jetbrains.annotations.NotNull;

public final class QuickNotesToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        QuickNotesManager manager = QuickNotesManager.getInstance();
        QuickNotesPanel panel = new QuickNotesPanel(manager.getNotesElement());
        Content content = ContentFactory.getInstance().createContent(panel.getRootComponent(), "", false);
        toolWindow.getContentManager().addContent(content);
        project.putUserData(QuickNotesManager.PANEL_KEY, panel);
    }
}
