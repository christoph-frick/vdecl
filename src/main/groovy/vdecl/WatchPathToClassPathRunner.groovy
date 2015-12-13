package vdecl

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Slf4j
@Component
class WatchPathToClassPathRunner implements CommandLineRunner {

    final private Config config

    @Autowired
    WatchPathToClassPathRunner(Config config) {
        this.config = config
    }

    @Override
    @CompileDynamic
    void run(String... args) throws Exception {
        log.debug "Manipulating class loader to include the watched dir"
        (Thread.currentThread().contextClassLoader as URLClassLoader).addURL(config.watchDir.toURI().toURL())
    }
}
