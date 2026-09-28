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
package com.jsrana.plugins.quicknotes.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jsrana.plugins.quicknotes.manager.QuickNotesManager;
import com.jsrana.plugins.quicknotes.ui.QuickNotesPanel;
import org.jetbrains.annotations.NotNull;

/**
 * @author Jitendra Rana
 */
public class AddToQuickNotes
        extends AnAction {
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (selectedText != null && !selectedText.trim().isEmpty()) {
            Project project = e.getData(CommonDataKeys.PROJECT);
            if (project == null) {
                return;
            }
            String panelid = project.getUserData(QuickNotesManager.KEY_PANELID);
            QuickNotesPanel quickNotesPanel = QuickNotesManager.getInstance().getQuickNotesPanel(panelid);
            if (quickNotesPanel != null) {
                FileDocumentManager manager = FileDocumentManager.getInstance();
                VirtualFile virtualFile = manager.getFile(editor.getDocument());
                if (virtualFile != null) {
                    quickNotesPanel.addNewNote("[File: " + virtualFile.getPath() + "]\n" + selectedText);
                }
            }
        }
    }
}
