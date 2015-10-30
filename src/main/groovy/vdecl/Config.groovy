package vdecl

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Component
@ConfigurationProperties
@Slf4j
class Config implements InitializingBean {

    @NotNull
    String watch

    @NotNull
    @Min(1L)
    Long interval

    File getWatchDir() {
        (watch as File).canonicalFile
    }

    @Override
    void afterPropertiesSet() throws Exception {
        log.info "Watching $watchDir in $interval intervals"
        assert watchDir.isDirectory()
    }
}
