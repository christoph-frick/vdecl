package vdecl

import com.vaadin.ui.Component
import com.vaadin.ui.declarative.Design

@org.springframework.stereotype.Component
class HTMLFileToComponentStrategy implements IFileToComponentStrategy {

    final String suffix = 'html'
    final String description = 'Vaadin declarative HTML5'

    @Override
    Component render(File f) {
        new FileInputStream(f).withCloseable {
            Design.read(it)
        }
    }

}
