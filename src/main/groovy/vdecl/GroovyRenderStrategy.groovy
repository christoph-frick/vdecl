package vdecl
import com.vaadin.ui.Component

import java.util.regex.Pattern

@org.springframework.stereotype.Component
class GroovyRenderStrategy extends ComponentRenderStrategy {

    final Pattern fileNamePattern = ~/.*\.groovy$/
    final String description = 'Groovy script'

    @Override
    Component renderComponent(File f) {
        new GroovyShell().run(f, []) as Component
    }

}
