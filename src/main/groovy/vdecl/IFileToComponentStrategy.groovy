package vdecl

import com.vaadin.ui.Component

interface IFileToComponentStrategy {
    String getSuffix()
    Component render(File f)
}