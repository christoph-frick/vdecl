package vdecl

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.regex.Pattern

@Service
@Slf4j
class FileToComponentService {

    final Map<String,String> legend
    private final Map<Pattern,IFileToComponentStrategy> patternToStrategy

    @Autowired
    FileToComponentService(List<IFileToComponentStrategy> fileToComponentStrategies) {
        legend = fileToComponentStrategies.collectEntries{[it.fileNamePattern,it.description]}
        patternToStrategy = fileToComponentStrategies.collectEntries{[it.fileNamePattern, it]}
        log.debug "Found strategies: ${patternToStrategy.collectEntries {[it.key, it.value.getClass().simpleName]}}"
    }

    Optional<IFileToComponentStrategy> getStrategyForFile(File f) {
        Optional.ofNullable(patternToStrategy.find{ it.key.matcher(f.name).matches() }?.value)
    }

    boolean canHandle(File f) {
        getStrategyForFile(f).present
    }
}
