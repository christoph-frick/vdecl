package vdecl
import com.vaadin.ui.Component

@org.springframework.stereotype.Component
class GroovyFileToComponentStrategy implements IFileToComponentStrategy {

    @Override
    boolean canHandle(File f) {
        return f.name =~ /.*\.groovy/
    }

    @Override
    Component render(File f) {
        new GroovyShell().run(f, []) as Component
    }

}
