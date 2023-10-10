package musta.belmo.plugins.base64converter.ast;

public interface Transformer {
    void transformPsi();

    String getActionName();

    boolean isApplied();
}
