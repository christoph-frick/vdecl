package vdecl
import com.vaadin.ui.Component

import java.util.regex.Pattern

@org.springframework.stereotype.Component
class GroovyFileToComponentStrategy implements IFileToComponentStrategy {

    final Pattern fileNamePattern = ~/.*\.groovy$/
    final String description = 'Groovy script'

    @Override
    Component render(File f) {
        new GroovyShell().run(f, []) as Component
    }

}
