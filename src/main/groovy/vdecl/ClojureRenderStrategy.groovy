package vdecl

import clojure.java.api.Clojure
import com.vaadin.ui.Component
import com.vaadin.ui.declarative.Design
import groovy.util.logging.Slf4j
import org.apache.commons.io.input.ReaderInputStream

import java.util.regex.Pattern

@org.springframework.stereotype.Component
@Slf4j
class ClojureRenderStrategy extends ComponentRenderStrategy {

    final Pattern fileNamePattern = ~/.*\.clj$/
    final String description = 'Clojure'

    ClojureRenderStrategy() {
        def require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("clojure.data.xml"))
        require.invoke(Clojure.read("clojure.main"))
    }

    @Override
    Component renderComponent(File f) {
        def out = new StringWriter()
        Clojure.var("clojure.data.xml", "emit").invoke(
                Clojure.var("clojure.data.xml", "sexp-as-element").invoke(
                        Clojure.var('clojure.main', 'load-script').invoke(f.canonicalPath)
                ),
                out
        )
        out.close()
        log.trace out.toString()
        return Design.read(new ReaderInputStream(new StringReader(out.toString()), 'UTF-8'))
    }

}
