package vdecl

import com.vaadin.ui.Component
import com.vaadin.ui.UI

interface IRenderTarget {

    UI getUI()
    void render(Component c)

}
