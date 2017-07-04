package vdecl.ui
import com.vaadin.event.Action
import com.vaadin.event.ShortcutAction
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.*
import com.vaadin.ui.declarative.Design
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Slf4j
import net.engio.mbassy.bus.MBassador
import net.engio.mbassy.listener.Handler
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import vdecl.Config
import vdecl.FileEvent
import vdecl.IRenderTarget
import vdecl.RenderStrategyService

import java.nio.file.Files

@Slf4j
@SpringView(name="watch")
class WatchView extends CustomComponent implements View, InitializingBean, DisposableBean, IRenderTarget, Action.Handler {

    @Autowired
    MBassador<FileEvent> eventBus

    @Autowired
    RenderStrategyService fileToComponentService

    @Autowired
    Config config

    final Action[] actions

    WatchView() {
        setSizeFull()
        actions = [
                new ShortcutAction("Save with F4",
                        ShortcutAction.KeyCode.F4
                )
        ] as Action[]
    }

    @Override
    Action[] getActions(Object target, Object sender) {
        return actions
    }

    @Override
    void handleAction(Action action, Object sender, Object target) {
        save()
    }

    private File solo

    @Handler
    void handleFileEvent(FileEvent fe) {
        getUI()?.access{
            log.debug("$fe")
            if (fe.type==FileEvent.Type.DELETE) {
                Notification.show("Deleted $fe.file.name", Notification.Type.TRAY_NOTIFICATION)
            } else {
                update(fe.file)
                if (solo) {
                    update(solo)
                }
            }
        }
    }

    void save() {
        try {
            def tempFile = Files.createTempFile("vdecl-export-", ".html")
            tempFile.withOutputStream { os ->
                Design.write(compositionRoot, os)
            }
            Notification.show(
                    "Current layout saved",
                    tempFile.toAbsolutePath().toString(),
                    Notification.Type.HUMANIZED_MESSAGE
            )
        }
        catch (Exception e) {
            Notification.show("Failed to save file", e.message, Notification.Type.ERROR_MESSAGE)
            log.error e.message, e
        }
    }

    void update(File f) {
        try {
            fileToComponentService.getStrategyForFile(f).ifPresent{
                it.render(f, this)
                Notification.show("Updated from $f.name", Notification.Type.TRAY_NOTIFICATION)
            }
        }
        catch (Exception e) {
            Notification.show("Failed to update $f.name", e.message, Notification.Type.ERROR_MESSAGE)
            log.error e.message, e
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
        getUI().addActionHandler(this)
        solo = event.parameters ? config.absoluteFile(event.parameters) : null
        if (solo) {
            update(solo)
        } else {
            setCompositionRoot(
                    new VerticalLayout(
                            new Label("Waiting for first file change in ${config.watchDir} ...").with{
                                addStyleName(ValoTheme.LABEL_LIGHT)
                                addStyleName(ValoTheme.LABEL_HUGE)
                                setSizeUndefined()
                                it
                            }
                    ).with{
                        setSizeFull()
                        setComponentAlignment(getComponent(0), Alignment.MIDDLE_CENTER)
                        it
                    }
            )
        }
    }

    @Override
    void render(Component c) {
        setCompositionRoot(c)
    }
}
