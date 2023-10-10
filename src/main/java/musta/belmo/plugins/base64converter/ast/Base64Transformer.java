package musta.belmo.plugins.base64converter.ast;

import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.TextRange;
import musta.belmo.plugins.base64converter.action.ActionType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Transformer implements Transformer {

    private final Editor editor;
    private final ActionType actionType;
    private final Document document;

    public Base64Transformer(Editor editor, ActionType actionType) {
        this.editor = editor;
        this.actionType = actionType;
        document = editor.getDocument();
    }

    @Override
    public void transformPsi() {
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        int startFrom = selectionModel.getSelectionStart();

        if (selectedText == null) {
            CaretModel caretModel = editor.getCaretModel();
            int offset = caretModel.getOffset();
            startFrom = getStartOffset(offset);
            int endOffset = getEndOffset(offset);
            selectedText = document.getText(new TextRange(startFrom, endOffset));
        }
        String transformedText = transformText(selectedText);
        if (transformedText == null) {
            return;
        }
        document.replaceString(startFrom, startFrom + selectedText.length(), transformedText);
    }

    private int getStartOffset(int offset) {
        char current;
        int startOffset = offset;
        do {
            startOffset--;
            current = document.getText().charAt(startOffset);
        } while (isStringTokenDelimiter(current) && startOffset > 0);
        return startOffset + 1;
    }

    private int getEndOffset(int offset) {
        String text = document.getText();
        char current = text.charAt(offset);
        int startOffset = offset;

        while (isStringTokenDelimiter(current) && startOffset < editor.getDocument().getTextLength()) {
            startOffset++;
            current = text.charAt(startOffset);
        }
        return startOffset;
    }

    private boolean isStringTokenDelimiter(char current) {
        final String delimiters;
        if (actionType == ActionType.ENCODE) {
            delimiters = "'\" -/{}[]:;*=+)(@!$%&|.,~\n\r\t";
        } else {
            delimiters = "'\" -{}[]:;*+)(@!$%&|.,~\n\r\t";
        }
        return !delimiters.contains(String.valueOf(current));
    }

    private String transformText(String text) {
        if (text == null) {
            return null;
        }
        try {
            byte[] textToBytes = text.getBytes(StandardCharsets.UTF_8);
            final byte[] result;
            if (actionType == ActionType.ENCODE) {
                result = Base64.getEncoder().encode(textToBytes);
            } else {
                result = Base64.getDecoder().decode(textToBytes);
            }
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return text;
        }
    }

    @Override
    public String getActionName() {
        if (actionType == ActionType.ENCODE) {
            return "Encode base 64";
        }
        return "Decode base 64";
    }

    @Override
    public boolean isApplied() {
        return true;
    }
}
