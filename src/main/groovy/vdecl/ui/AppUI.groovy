package vdecl.ui
import com.vaadin.annotations.Push
import com.vaadin.annotations.Theme
import com.vaadin.server.VaadinRequest
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.Label
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme

@SpringUI(path="")
@Push
@Theme(ValoTheme.THEME_NAME)
class AppUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        def label = new Label("Hello World!")
        setContent(label)
        Thread.start{
            while(true) {
                Thread.sleep(1000)
                access{
                    label.value = new Date().toString()
                }
            }
        }
    }

}
