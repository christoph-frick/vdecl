package vdecl

import com.vaadin.ui.Component
import com.vaadin.ui.declarative.Design

import java.util.regex.Pattern

@org.springframework.stereotype.Component
class HTMLFileToComponentStrategy implements IFileToComponentStrategy {

    final Pattern fileNamePattern = ~/.*\.html$/
    final String description = 'Vaadin declarative HTML5'

    @Override
    Component render(File f) {
        new FileInputStream(f).withCloseable {
            Design.read(it)
        }
    }

}
