package vdecl.ui
import com.vaadin.annotations.DesignRoot
import com.vaadin.data.ValueProvider
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.*
import com.vaadin.ui.declarative.Design
import groovy.transform.Canonical
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import groovy.xml.MarkupBuilder
import net.engio.mbassy.bus.MBassador
import net.engio.mbassy.listener.Handler
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import vdecl.Config
import vdecl.FileEvent
import vdecl.RenderStrategyService

@Slf4j
@SpringView(name="")
@DesignRoot("home.html")
class HomeView extends VerticalLayout implements View, InitializingBean, DisposableBean {

    @Autowired
    Config config

    @Autowired
    RenderStrategyService fileToComponentService

    @Autowired
    MBassador<FileEvent> eventBus

    private final Set<FileBean> fileBeans = []
    private final Grid<FileBean> grid
    private final Label headline
    private final Label legend
    private final ComboBox theme
    private final Button watch

    private static final List<String> themes = ["valo"]

    HomeView() {
        Design.read(this)
        setSizeFull()
        grid.asSingleSelect().addValueChangeListener { watch(it.value) }
        grid.addColumn({FileBean it -> it.displayName} as ValueProvider).
                setCaption('File name').
                setExpandRatio(1).
                setId('displayName')
        grid.addColumn({FileBean it -> it.lastModified} as ValueProvider).
                setCaption('Last modified').
                setId('lastModified')
        grid.sort('lastModified', SortDirection.DESCENDING)
        watch.addClickListener{watch()}
        theme.with{
            setItems(themes)
            addValueChangeListener{
                if (theme.value) {
                    getUI()?.theme = theme.value
                    theme.value = null
                }
            }
        }
    }

    void watch(FileBean fb=null) {
       getUI()?.navigator?.navigateTo("watch${fb?"/"+fb.displayName:""}")
    }

    void update() {
        fileBeans.clear()
        config.watchDir.eachFileRecurse {
            if (it.isFile()) {
                update(it, false)
            }
        }
        grid.setItems(fileBeans)
    }

    void update(File f, Boolean immediate=true) {
        if (fileToComponentService.canHandle(f)) {
            fileBeans.add(new FileBean(config, f))
            if (immediate) {
                grid.setItems(fileBeans)
            }
        }
    }

    void remove(File f, Boolean immediate=true) {
        fileBeans.remove(new FileBean(config, f))
        if (immediate) {
            grid.setItems(fileBeans)
        }
    }

    @Override
    void enter(ViewChangeListener.ViewChangeEvent event) {
        update()
    }

    @Handler
    void handleFileEvent(FileEvent fe) {
        getUI()?.access{
            log.debug("$fe")
            if (fe.type==FileEvent.Type.DELETE) {
                remove(fe.file)
            } else {
                update(fe.file)
            }
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        eventBus.subscribe(this)
        headline.value = "Watching ${config.watchDir}"
        updateLegend()
    }

    @CompileDynamic
    private void updateLegend() {
        def sb = new StringWriter()
        new MarkupBuilder(sb).div{
            h3("Supported formats")
            dt{
                fileToComponentService.legend.each{ suffix, description ->
                    dt{ code(suffix) }
                    dd(description)
                }
            }
        }
        legend.value = sb.toString()
    }

    @Override
    void destroy() throws Exception {
        eventBus.unsubscribe(this)
    }

    @Canonical(includes = "file")
    public class FileBean {
        final File file
        final String displayName
        final Date lastModified
        FileBean(Config config, File file) {
            this.file = file
            displayName = config.relativeFileName(file)
            lastModified = new Date(file.lastModified())
        }
    }
}
