package com.jsrana.plugins.quicknotes;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import org.jetbrains.annotations.NotNull;

public final class QuickNotesProjectListener implements ProjectManagerListener {
    @Override
    public void projectClosed(@NotNull Project project) {
        QuickNotesManager manager = ApplicationManager.getApplication().getServiceIfCreated(QuickNotesManager.class);
        if (manager == null) {
            return;
        }
        manager.clearLocks(project.getUserData(QuickNotesManager.PANEL_KEY));
        manager.saveNotes();
    }
}
