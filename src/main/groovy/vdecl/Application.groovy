package vdecl
import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@CompileStatic
class Application {

    static void main(String[] args) throws Exception {
        def app = new SpringApplication(Application)
        app.showBanner = false
        app.run(args)
    }

}
