package vdecl
import com.vaadin.ui.Component

@org.springframework.stereotype.Component
class GroovyFileToComponentStrategy implements IFileToComponentStrategy {

    @Override
    String getSuffix() {
        "groovy"
    }

    @Override
    String getDescription() {
        "Groovy script"
    }

    @Override
    Component render(File f) {
        new GroovyShell().run(f, []) as Component
    }

}
