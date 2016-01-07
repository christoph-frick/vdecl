package vdecl.ui
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.sass.internal.ScssStylesheet
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl
import com.vaadin.sass.internal.handler.SCSSErrorHandler
import com.vaadin.spring.annotation.SpringView
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

    WatchView() {
        setSizeFull()
    }

    private String solo

    @Handler
    void handleFileEvent(FileEvent fe) {
        getUI()?.access{
            log.debug("$fe")
            if (fe.type==FileEvent.Type.DELETE) {
                Notification.show("Deleted $fe.file.name", Notification.Type.TRAY_NOTIFICATION)
            } else {
                if (!solo || config.relativeFileName(fe.file)==solo) {
                    update(fe.file)
                }
                updateTheme(fe.file)
            }
        }
    }

    void update(File f) {
        try {
            fileToComponentService.getStrategyForFile(f).ifPresent{
                setCompositionRoot(it.render(f))
                Notification.show("Updated from $f.name", Notification.Type.TRAY_NOTIFICATION)
            }
        }
        catch (Exception e) {
            Notification.show("Failed to update $f.name", e.message, Notification.Type.ERROR_MESSAGE)
            log.error e.message, e
        }
    }

    /* FIXME: make the file strategies just use regexps instead of the suffix and integrate this into the regular render cycle */
    void updateTheme(File f) {
        def themeMatch = f.canonicalPath =~ /(?<root>.*\/VAADIN\/themes\/)(?<theme>[^\/]+)\/.*\.scss/
        if (!themeMatch.matches()) {
            return
        }
        def root = themeMatch.group('root')
        def theme = themeMatch.group('theme')
        def styles = "${root}${theme}/styles.scss"
        def errorHandler = new SCSSErrorHandler()
        try {
            ScssStylesheet.get(styles, null, new SCSSDocumentHandlerImpl(), errorHandler).compile()
        }
        catch (Exception e) {
            Notification.show("Failed to update theme ${theme}", e.message, Notification.Type.ERROR_MESSAGE)
            log.error e.message, e
            return
        }
        if (!errorHandler.errorsDetected) {
            Notification.show("Updating theme ${theme}", Notification.Type.TRAY_NOTIFICATION)
            getUI().setTheme(theme)
        } else {
            Notification.show("Failed to update theme ${theme}", "Errors from compiler, see log", Notification.Type.ERROR_MESSAGE)
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
