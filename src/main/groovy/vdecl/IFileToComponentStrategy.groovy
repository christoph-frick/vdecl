package vdecl

import com.vaadin.ui.Component

interface IFileToComponentStrategy {
    boolean canHandle(File f)
    Component render(File f)
}