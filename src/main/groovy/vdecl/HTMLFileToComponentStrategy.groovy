package vdecl

import com.vaadin.ui.Component
import com.vaadin.ui.declarative.Design

@org.springframework.stereotype.Component
class HTMLFileToComponentStrategy implements IFileToComponentStrategy {

    @Override
    String getSuffix() {
        "html"
    }

    @Override
    String getDescription() {
        "Vaadin declarative HTML5"
    }

    @Override
    Component render(File f) {
        Design.read(new FileInputStream(f))
    }

}
