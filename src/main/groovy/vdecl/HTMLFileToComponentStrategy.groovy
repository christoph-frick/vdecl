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
    Component render(File f) {
        Design.read(new FileInputStream(f))
    }

}
