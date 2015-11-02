package vdecl.ui
import com.vaadin.data.util.BeanItemContainer
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
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
class HomeView extends CustomComponent implements View, InitializingBean, DisposableBean {

    @Autowired
    Config config

    @Autowired
    FileToComponentService fileToComponentService

    @Autowired
    MBassador<FileEvent> eventBus

    private final Table table
    private final Label headline
    private final Label legend

    private static final List<String> displayCols = ["displayName", "lastModified"]
    private static final String sortCol = "lastModified"

    HomeView() {
        setSizeFull()
        setCompositionRoot(
                new VerticalLayout(
                        headline = new Label().with{
                            addStyleName(ValoTheme.LABEL_H2)
                            it
                        },
                        new HorizontalLayout(
                                new Button("Watch for any change").with{
                                    addClickListener{watch()}
                                    it
                                },
                                new Label("- or pick one file to watch below -").with{
                                    addStyleName(ValoTheme.LABEL_SMALL)
                                    it
                                },
                        ).with{
                            setSpacing(true)
                            setComponentAlignment(getComponent(1), Alignment.MIDDLE_CENTER)
                            it
                        },
                        table = new Table().with{
                            pageLength = 0
                            sortEnabled = false
                            selectable = true
                            addValueChangeListener{watch(table.value as FileBean)}
                            setContainerDataSource(new BeanItemContainer<FileBean>(FileBean), displayCols)
                            setSizeFull()
                            it
                        },
                        legend = new Label().with{
                            setContentMode(ContentMode.HTML)
                            it
                        },
                ).with{
                    setSizeFull()
                    setSpacing(true)
                    setMargin(true)
                    setExpandRatio(table, 1.0f)
                    it
                }
        )
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
