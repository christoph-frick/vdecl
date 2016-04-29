import com.vaadin.data.util.HierarchicalContainer
import com.vaadin.event.dd.DragAndDropEvent
import com.vaadin.event.dd.DropHandler
import com.vaadin.event.dd.acceptcriteria.AcceptAll
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion
import com.vaadin.server.FontAwesome
import com.vaadin.shared.ui.dd.VerticalDropLocation
import com.vaadin.ui.Button
import com.vaadin.ui.Tree
import com.vaadin.ui.VerticalLayout

import static com.vaadin.ui.Tree.TreeDragMode.NODE

class Section {
    String headline
    List<Section> subSections
}

def tree = new Section(headline: "Root", subSections: [
        new Section(headline: "Child 1"),
        new Section(headline: "Child 2", subSections: [
                new Section(headline: "Grand Child 1"),
                new Section(headline: "Grand Child 2"),
        ]),
        new Section(headline: "Child 3", subSections: [
                new Section(headline: "Grand Child 3"),
        ]),
])

def addSection(Tree tree, Section s, Object parentItemId = null) {
    tree.with{
        addItem(s)
        if (parentItemId) {
            setParent(s, parentItemId)
        }
        setItemCaption(s, s.headline)
        expandItem(s)
        setChildrenAllowed(s, true)
    }
    s.subSections?.each{
        addSection(tree, it, s)
    }
}

Tree treeComponent

new VerticalLayout(
        new Button().with{
            caption = "Add section"
            icon = FontAwesome.PLUS
            addClickListener{
                def parentId = treeComponent.value ?: tree
                addSection(treeComponent, new Section(headline: new Date().toString()), parentId)
            }
            it
        },
        treeComponent = new Tree().with{
            def self = it
            setSelectable(true)
            addSection(it, tree)
            setDragMode(NODE)
            setDropHandler(new DropHandler() {
                @Override
                void drop(DragAndDropEvent event) {
                    def t = event.transferable
                    if (t.sourceComponent!=self) {
                        return
                    }
                    Tree.TreeTargetDetails ttd = event.targetDetails
                    def sourceItemId = t.getData('itemId')
                    def targetItemId = ttd.itemIdOver
                    def loc = ttd.dropLocation
                    HierarchicalContainer container = self.containerDataSource
                    switch (loc) {
                        case VerticalDropLocation.MIDDLE:
                            self.setParent(sourceItemId, targetItemId)
                            break
                        case VerticalDropLocation.TOP:
                            container.with{
                                def parentId = getParent(targetItemId)
                                if (parentId) {
                                    setParent(sourceItemId, parentId)
                                    moveAfterSibling(sourceItemId, targetItemId)
                                    moveAfterSibling(targetItemId, sourceItemId)
                                }
                            }
                            break
                        case VerticalDropLocation.BOTTOM:
                            container.with{
                                def parentId = getParent(targetItemId)
                                if (parentId) {
                                    setParent(sourceItemId, parentId)
                                    moveAfterSibling(sourceItemId, targetItemId)
                                }
                            }
                            break
                    }
                }

                @Override
                AcceptCriterion getAcceptCriterion() {
                    AcceptAll.get()
                }
            })
            it
        },
).with{
    spacing = true
    setMargin(true)
    it
}
