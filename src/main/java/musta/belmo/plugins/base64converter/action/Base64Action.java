package musta.belmo.plugins.base64converter.action;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import musta.belmo.plugins.base64converter.ast.Base64Transformer;
import musta.belmo.plugins.base64converter.ast.Transformer;

public abstract class Base64Action extends AbstractAction {
    private final ActionType actionType;
    protected Base64Action(ActionType actionType) {
        this.actionType = actionType;
    }

    @Override
    protected Transformer getTransformer() {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        return new Base64Transformer(editor, actionType);
    }
}