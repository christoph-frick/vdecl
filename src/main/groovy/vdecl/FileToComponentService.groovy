package vdecl

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.regex.Pattern

@Service
@Slf4j
class FileToComponentService {

    final Map<String,String> legend
    private final Map<String,IFileToComponentStrategy> suffixToStrategy
    private final Pattern suffixPattern

    @Autowired
    FileToComponentService(List<IFileToComponentStrategy> fileToComponentStrategies) {
        legend = fileToComponentStrategies.collectEntries{[it.suffix,it.description]}
        suffixToStrategy = fileToComponentStrategies.collectEntries{[it.suffix, it]}
        suffixPattern = ~/.*\.(${suffixToStrategy.keySet().collect{Pattern.quote(it)}.join("|")})/
        log.debug "Found strategies: ${suffixToStrategy}"
    }

    boolean canHandle(File f) {
        suffixPattern.matcher(f.name).matches()
    }

    Optional<IFileToComponentStrategy> getStrategyForFile(File f) {
        def m = suffixPattern.matcher(f.name)
        if (!m.matches()) {
            return Optional.empty()
        }
        def suffix = m.group(1)
        Optional.ofNullable(suffixToStrategy.get(suffix))
    }
}
