package vdecl

import com.vaadin.ui.Component

interface IFileToComponentStrategy {
    String getSuffix()
    String getDescription()
    Component render(File f)
}