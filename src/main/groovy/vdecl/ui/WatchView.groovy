package vdecl.ui
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.spring.annotation.SpringView
import com.vaadin.spring.navigator.SpringViewProvider
import com.vaadin.ui.CustomComponent
import com.vaadin.ui.Label
import com.vaadin.ui.Notification
import groovy.util.logging.Slf4j
import net.engio.mbassy.bus.MBassador
import net.engio.mbassy.listener.Handler
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import vdecl.Config
import vdecl.FileEvent
import vdecl.FileToComponentService

@Slf4j
@SpringView(name="watch")
class WatchView extends CustomComponent implements View, InitializingBean, DisposableBean {

    @Autowired
    MBassador<FileEvent> eventBus

    @Autowired
    FileToComponentService fileToComponentService

    @Autowired
    Config config

    @Autowired
    SpringViewProvider viewProvider

    WatchView() {
        setSizeFull()
    }

    private String solo

    @Handler
    void handleFileEvent(FileEvent fe) {
        getUI()?.access{
            log.debug("$fe")
            if (fe.type==FileEvent.Type.DELETE) {
                if (solo) {
                    getUI()?.navigator?.navigateTo("")
                }
                Notification.show("Deleted $fe.file.name", Notification.Type.TRAY_NOTIFICATION)
            } else {
                if (!solo || config.relativeFileName(fe.file)==solo) {
                    update(fe.file)
                }
            }
        }
    }

    void update(File f) {
        try {
            fileToComponentService.getStrategyForFile(f)?.with{
                setCompositionRoot(it.render(f))
                Notification.show("Updated from $f.name", Notification.Type.TRAY_NOTIFICATION)
            }
        }
        catch (Throwable throwable) {
            Notification.show("Failed to update $f.name", throwable.message, Notification.Type.ERROR_MESSAGE)
            log.error throwable.message, throwable
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        eventBus.subscribe(this)
    }

    @Override
    void destroy() throws Exception {
        eventBus.unsubscribe(this)
    }

    @Override
    void enter(ViewChangeListener.ViewChangeEvent event) {
        solo = event.parameters ?: null
        if (solo) {
            update(config.absoluteFile(solo))
        } else {
            setCompositionRoot(new Label("Waiting for file change..."))
        }
    }
}
