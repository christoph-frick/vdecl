package vdecl
import com.vaadin.ui.Component

@org.springframework.stereotype.Component
class GroovyFileToComponentStrategy implements IFileToComponentStrategy {

    final String suffix = 'groovy'
    final String description = 'Groovy script'

    @Override
    Component render(File f) {
        new GroovyShell().run(f, []) as Component
    }

}
