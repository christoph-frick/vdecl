package vdecl

import java.util.regex.Pattern

interface IRenderStrategy {
    Pattern getFileNamePattern()
    String getDescription()
    void render(File f, IRenderTarget target)
}