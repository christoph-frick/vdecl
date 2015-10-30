package vdecl.ui
import com.vaadin.annotations.Push
import com.vaadin.annotations.Theme
import com.vaadin.server.Page
import com.vaadin.server.VaadinRequest
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.Label
import com.vaadin.ui.Notification
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Slf4j
import net.engio.mbassy.bus.MBassador
import net.engio.mbassy.listener.Handler
import org.springframework.beans.factory.annotation.Autowired
import vdecl.Config
import vdecl.FileEvent
import vdecl.IFileToComponentStrategy

@Push
@Slf4j
@SpringUI(path="")
@Theme(ValoTheme.THEME_NAME)
class AppUI extends UI {

    @Autowired
    MBassador<FileEvent> eventBus

    @Autowired
    List<IFileToComponentStrategy> fileToComponentStrategies

    @Autowired
    Config config

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        log.debug "Subscribe to event bus"
        eventBus.subscribe(this)
        // TODO: generate a directory listing; provide a view "watch" and pass the file as param
        setContent(new Label("Waiting for file change in ${config.watchDir} every ${config.interval}ms ..."))
    }

    @Handler
    void handleFileEvent(FileEvent e) {
        if (e.type==FileEvent.Type.CHANGE) {
            access{
                try {
                    fileToComponentStrategies.find{ it.canHandle(e.file) }.with{
                        setContent(it.render(e.file))
                        new Notification("Updated from $e.file.name", Notification.Type.TRAY_NOTIFICATION).with{
                            setDelayMsec(500)
                            show(Page.current)
                        }
                    }
                }
                catch (Throwable throwable) {
                    Notification.show("Failed to update $e.file.name", throwable.message, Notification.Type.ERROR_MESSAGE)
                    log.error throwable.message, throwable
                }
            }
        }
    }

    @Override
    void detach() {
        log.debug "Unsubscribe from event bus"
        eventBus?.unsubscribe(this)
        super.detach()
    }
}
