package musta.belmo.plugins.base64converter.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.project.Project;
import musta.belmo.plugins.base64converter.ast.Transformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAction extends AnAction {
    protected Project project;
    protected AnActionEvent event;

    @Override
    public void update(AnActionEvent e) {
        project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null
                && e.getData(CommonDataKeys.CARET) !=null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        if (event.getProject() == null) {
            return;
        }
        this.event = event;
        applyAction(event);
    }
    private void applyAction(@NotNull AnActionEvent event) {
        Transformer transformer = getTransformer();
        if(transformer != null && transformer.isApplied()) {
            try {
                UndoConfirmationPolicy requestConfirmation = UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION;
                CommandProcessor.getInstance().executeCommand(getEventProject(event),
                        () -> ApplicationManager.getApplication().runWriteAction(transformer::transformPsi),
                        transformer.getActionName(), null, requestConfirmation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
   @Nullable
   protected abstract Transformer getTransformer();
}
