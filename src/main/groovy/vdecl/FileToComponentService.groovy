package vdecl

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.regex.Pattern

@Service
@Slf4j
class FileToComponentService {

    private final List<IFileToComponentStrategy> fileToComponentStrategies
    private final Map<String,IFileToComponentStrategy> suffixToStrategy
    final Map<String,String> legend
    private final Pattern suffixPattern

    @Autowired
    FileToComponentService(List<IFileToComponentStrategy> fileToComponentStrategies) {
        this.fileToComponentStrategies = fileToComponentStrategies
        suffixToStrategy = fileToComponentStrategies.collectEntries{[it.suffix, it]}
        legend = fileToComponentStrategies.collectEntries{[it.suffix,it.description]}
        suffixPattern = ~/.*\.(${suffixToStrategy.keySet().collect{Pattern.quote(it)}.join("|")})/
        log.debug "Found strategies: ${suffixToStrategy}"
    }

    boolean canHandle(File f) {
        suffixPattern.matcher(f.name).matches()
    }

    IFileToComponentStrategy getStrategyForFile(File f) {
        def m = suffixPattern.matcher(f.name)
        if (!m.matches()) {
            return null
        }
        def suffix = m.group(1)
        return suffixToStrategy.get(suffix)
    }
}
