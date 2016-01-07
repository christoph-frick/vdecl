package vdecl

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.regex.Pattern

@Service
@Slf4j
class RenderStrategyService {

    final Map<String,String> legend
    private final Map<Pattern,IRenderStrategy> patternToStrategy

    @Autowired
    RenderStrategyService(List<IRenderStrategy> fileToComponentStrategies) {
        legend = fileToComponentStrategies.collectEntries{[it.fileNamePattern,it.description]}
        patternToStrategy = fileToComponentStrategies.collectEntries{[it.fileNamePattern, it]}
        log.debug "Found strategies: ${patternToStrategy.collectEntries {[it.key, it.value.getClass().simpleName]}}"
    }

    Optional<IRenderStrategy> getStrategyForFile(File f) {
        Optional.ofNullable(
                patternToStrategy.find{
                    it.key.matcher(f.canonicalPath).matches()
                }?.value)
    }

    boolean canHandle(File f) {
        getStrategyForFile(f).present
    }
}
