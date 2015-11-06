package vdecl
import com.vaadin.ui.Component
import com.vaadin.ui.declarative.Design
import groovy.text.markup.MarkupTemplateEngine
import groovy.text.markup.TemplateConfiguration
import org.springframework.beans.factory.annotation.Autowired

@org.springframework.stereotype.Component
class GroovyTemplateFileToComponentStrategy implements IFileToComponentStrategy {

    final String suffix = 'gtpl'
    final String description = 'Groovy template'

    private MarkupTemplateEngine mte

    @Autowired
    GroovyTemplateFileToComponentStrategy(Config config) {
         mte = new MarkupTemplateEngine(Thread.currentThread().getContextClassLoader(), config.watchDir, new TemplateConfiguration())
    }

    @Override
    Component render(File f) {
        def tpl = mte.createTemplate(f.newReader())
        def bos = new ByteArrayOutputStream()
        def os = new OutputStreamWriter(bos)
        tpl.make().writeTo(os)
        Design.read(new ByteArrayInputStream(bos.toByteArray()))
    }

}
