package vdecl.ui;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.*;
import vdecl.ui.client.ForceThemeExtensionClientRPC;

public class ForceThemeExtension extends AbstractExtension {

    public void extend(UI ui) {
        super.extend(ui);
    }

    public void updateTheme(String oldTheme, String newTheme) {
        getRpcProxy(ForceThemeExtensionClientRPC.class).updateTheme(oldTheme, newTheme);
    }

}
