package vdecl
import groovy.transform.CompileStatic
import net.engio.mbassy.bus.MBassador
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@CompileStatic
class Application {

    static void main(String[] args) throws Exception {
        def app = new SpringApplication(Application)
        app.showBanner = false
        app.run(args)
    }

    @Bean
    MBassador<FileEvent> getMessageBus() {
        new MBassador<FileEvent>()
    }

}
