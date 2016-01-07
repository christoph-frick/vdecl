package vdecl

import com.vaadin.ui.Component

import java.util.regex.Pattern

interface IFileToComponentStrategy {
    Pattern getFileNamePattern()
    String getDescription()
    Component render(File f)
}