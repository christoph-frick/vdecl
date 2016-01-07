package vdecl

import com.vaadin.sass.internal.ScssStylesheet
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl
import com.vaadin.sass.internal.handler.SCSSErrorHandler
import org.springframework.stereotype.Component

import java.util.regex.Pattern

@Component
class ThemeRenderStrategy implements IRenderStrategy {

    final Pattern fileNamePattern = ~/(?<root>.*\/VAADIN\/themes\/)(?<theme>[^\/]+)\/.*\.scss/
    final String description = "SASS Theme"

    @Override
    void render(File f, IRenderTarget target) {
        def themeMatch = fileNamePattern.matcher(f.canonicalPath)
        if (!themeMatch.matches()) {
            return
        }
        def theme = themeMatch.group('theme')
        def stylesScss = "${themeMatch.group('root')}${theme}/styles.scss"
        def errorHandler = new SCSSErrorHandler()
        ScssStylesheet.get(stylesScss, null, new SCSSDocumentHandlerImpl(), errorHandler).compile()
        if (!errorHandler.errorsDetected) {
            target.UI?.setTheme(theme)
        } else {
            throw new Exception("Failed to compile theme ${stylesScss}; See log for actual errors")
        }
    }
}
