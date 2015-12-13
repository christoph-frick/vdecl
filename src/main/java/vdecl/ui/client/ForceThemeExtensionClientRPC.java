package vdecl.ui.client;

import com.vaadin.shared.communication.ClientRpc;

public interface ForceThemeExtensionClientRPC extends ClientRpc {
    void updateTheme(String oldTheme, String newTheme);
}
