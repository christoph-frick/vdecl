package vdecl.ui.client;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.shared.ui.Connect;
import vdecl.ui.ForceThemeExtension;

@Connect(ForceThemeExtension.class)
public class ForceThemeExtensionConnection extends AbstractExtensionConnector {

    @Override
    protected void extend(ServerConnector target) {
        registerRpc(ForceThemeExtensionClientRPC.class, new ForceThemeExtensionClientRPC() {
            @Override
            public void updateTheme(String oldTheme, String newTheme) {
                if (getConnection()!=null && getConnection().getUIConnector()!=null) {
                    UIConnector uic = getConnection().getUIConnector();
                    onThemeChange(uic, oldTheme, newTheme);
                }
            }

            public native void onThemeChange(UIConnector ui, String oldTheme, String newTheme) /*-{
                var oldThemeUri = ui.@com.vaadin.client.ui.ui.UIConnector::getThemeUrl(*)(oldTheme);
                var newThemeUri = ui.@com.vaadin.client.ui.ui.UIConnector::getThemeUrl(*)(newTheme);
                ui.@com.vaadin.client.ui.ui.UIConnector::replaceTheme(*)(oldTheme, null, oldThemeUri, null);
                ui.@com.vaadin.client.ui.ui.UIConnector::replaceTheme(*)(oldTheme, newTheme, oldThemeUri, newThemeUri);
            }-*/;
        });
    }
}
