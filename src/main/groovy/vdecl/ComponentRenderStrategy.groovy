package vdecl

import com.vaadin.ui.Component

abstract class ComponentRenderStrategy implements IRenderStrategy {

    @Override
    void render(File f, IRenderTarget target) {
        target.render(renderComponent(f))
    }

    abstract Component renderComponent(File f)

}
