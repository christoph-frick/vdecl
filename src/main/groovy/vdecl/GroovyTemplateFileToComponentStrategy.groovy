package vdecl
import com.vaadin.ui.Component
import com.vaadin.ui.declarative.Design
import groovy.text.markup.MarkupTemplateEngine
import groovy.text.markup.TemplateConfiguration

@org.springframework.stereotype.Component
class GroovyTemplateFileToComponentStrategy implements IFileToComponentStrategy {

    private MarkupTemplateEngine mte

    GroovyTemplateFileToComponentStrategy() {
         mte = new MarkupTemplateEngine(new TemplateConfiguration())
    }

    @Override
    boolean canHandle(File f) {
        return f.name =~ /.*\.groovy/
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
