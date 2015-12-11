package vdecl

import net.engio.mbassy.bus.MBassador
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    static void main(String[] args) throws Exception {
        new SpringApplication(Application).with{
            bannerMode = Banner.Mode.OFF
            run(args)
        }
    }

    @Bean
    MBassador<FileEvent> getMessageBus() {
        new MBassador<FileEvent>()
    }

}
