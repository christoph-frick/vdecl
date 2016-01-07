package vdecl
import com.vaadin.ui.Component
import com.vaadin.ui.declarative.Design
import groovy.text.markup.MarkupTemplateEngine
import groovy.text.markup.TemplateConfiguration
import org.springframework.beans.factory.annotation.Autowired

import java.util.regex.Pattern

@org.springframework.stereotype.Component
class GroovyTemplateRenderStrategy extends ComponentRenderStrategy {

    final Pattern fileNamePattern = ~/.*\.gtpl$/
    final String description = 'Groovy template'

    private MarkupTemplateEngine mte

    @Autowired
    GroovyTemplateRenderStrategy(Config config) {
        def templateConfig = new TemplateConfiguration().with{
            cacheTemplates = false
            it
        }
        mte = new MarkupTemplateEngine(Thread.currentThread().getContextClassLoader(), config.watchDir, templateConfig)
    }

    @Override
    Component renderComponent(File f) {
        f.newReader().withCloseable {
            def tpl = mte.createTemplate(it)
            new ByteArrayOutputStream().withCloseable { bos ->
                new OutputStreamWriter(bos).withCloseable {
                    tpl.make().writeTo(it)
                    new ByteArrayInputStream(bos.toByteArray()).withCloseable {
                        Design.read(it)
                    }
                }
            }
        }
    }

}
