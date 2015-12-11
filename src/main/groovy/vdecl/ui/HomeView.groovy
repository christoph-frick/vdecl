package vdecl.ui
import com.vaadin.annotations.DesignRoot
import com.vaadin.data.util.BeanItemContainer
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
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
import vdecl.FileToComponentService

@Slf4j
@SpringView(name="")
@DesignRoot("home.html")
class HomeView extends VerticalLayout implements View, InitializingBean, DisposableBean {

    @Autowired
    Config config

    @Autowired
    FileToComponentService fileToComponentService

    @Autowired
    MBassador<FileEvent> eventBus

    private final Table table
    private final Label headline
    private final Label legend
    private final ComboBox theme
    private final Button watch

    private static final List<String> displayCols = ["displayName", "lastModified"]
    private static final String sortCol = "lastModified"

    private static final List<String> themes = ["valo", "reindeer", "chameleon", "runo", "liferay"]

    HomeView() {
        Design.read(this)
        setSizeFull()
        table.with{
            addValueChangeListener{watch(table.value as FileBean)}
            setContainerDataSource(new BeanItemContainer<FileBean>(FileBean), displayCols)
        }
        watch.addClickListener{watch()}

        theme.with{
            themes.each{ themeName ->
                addItem(themeName)
            }
            value = 'valo'
            immediate = true
            nullSelectionAllowed = false
            addValueChangeListener{ getUI()?.theme = theme.value as String }
        }
    }

    void watch(FileBean fb=null) {
       getUI()?.navigator?.navigateTo("watch${fb?"/"+fb.displayName:""}")
    }

    void update() {
        table.containerDataSource.removeAllItems()
        config.watchDir.eachFileRecurse {
            if (it.isFile()) {
                update(it)
            }
        }
    }

    void update(File f) {
        if (fileToComponentService.canHandle(f)) {
            (table.containerDataSource as BeanItemContainer<FileBean>).with{
                addItem(new FileBean(config, f))
                sort([sortCol].toArray(), false)
            }
        }
    }

    void remove(File f) {
        table.containerDataSource.removeItem(new FileBean(config, f))
    }

    @Override
    void enter(ViewChangeListener.ViewChangeEvent event) {
        update()
        theme?.value = getUI()?.theme
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
            h3("Supported formats (suffix)")
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
