package vdecl.ui
import com.vaadin.annotations.Push
import com.vaadin.annotations.Theme
import com.vaadin.annotations.Widgetset
import com.vaadin.navigator.Navigator
import com.vaadin.server.VaadinRequest
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.spring.navigator.SpringViewProvider
import com.vaadin.ui.CssLayout
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

@Push
@Slf4j
@SpringUI(path="")
@Theme(ValoTheme.THEME_NAME)
@Widgetset('vdecl.ui.widgetset')
class AppUI extends UI {

    @Autowired
    SpringViewProvider viewProvider

    private final ForceThemeExtension forceThemeExtension

    AppUI() {
        forceThemeExtension = new ForceThemeExtension().with{
            extend(this)
            it
        }
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        def root = new CssLayout().with{
            setSizeFull()
            it
        }
        setContent(root)
        new Navigator(this, root).with{
            addProvider(viewProvider)
        }
    }

    @Override
    void setTheme(String newTheme) {
        String oldTheme = theme
        super.setTheme(newTheme)
        if (oldTheme==newTheme) {
            forceThemeExtension.updateTheme(oldTheme, newTheme)
        }
    }

}
