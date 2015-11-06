package vdecl

import groovy.util.logging.Slf4j
import net.engio.mbassy.bus.MBassador
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class FileMonitor implements DisposableBean {

    private final Config config
    private final MBassador<FileEvent> eventBus

    private final FileAlterationMonitor fam

    @Autowired
    FileMonitor(Config config, MBassador<FileEvent> eventBus) {
        this.config = config
        this.eventBus = eventBus
        fam = new FileAlterationMonitor(config.interval)
        fam.addObserver(new FileAlterationObserver(config.watchDir).with{
            addListener(new FileAlterationListenerAdaptor() {
                @Override
                void onFileCreate(File file) {
                    notify(FileEvent.Type.CREATE, file)
                }

                @Override
                void onFileChange(File file) {
                    notify(FileEvent.Type.CHANGE, file)
                }

                @Override
                void onFileDelete(File file) {
                    notify(FileEvent.Type.DELETE, file)
                }
            })
            it
        })
        log.info "Starting file monitor"
        fam.start()
    }

    @Override
    void destroy() throws Exception {
        log.info "Stopping file monitor"
        fam?.stop()
    }

    void notify(FileEvent.Type type, File file) {
        eventBus.publishAsync(FileEvent.build(type, file))
    }

}
