package vdecl

import com.vaadin.ui.Component
import com.vaadin.ui.declarative.Design

import java.util.regex.Pattern

@org.springframework.stereotype.Component
class HTMLRenderStrategy extends ComponentRenderStrategy {

    final Pattern fileNamePattern = ~/.*\.html?$/
    final String description = 'Vaadin declarative HTML5'

    @Override
    Component renderComponent(File f) {
        new FileInputStream(f).withCloseable {
            Design.read(it)
        }
    }

}
