import com.vaadin.event.FieldEvents
import com.vaadin.server.FontAwesome
import com.vaadin.ui.*

import static com.vaadin.ui.AbstractTextField.TextChangeEventMode.*

new Panel().with{
	GridLayout grid
	def fonticon = { fi ->
		new Label().with{
			caption = "${fi.name()} (0x${Integer.toString(fi.codepoint, 16)})"
			icon = fi
			it
		}
	}
	def updateGrid = { filter ->
		grid.removeAllComponents()
		FontAwesome.values().findAll(filter).each { fi ->
			grid.addComponent(fonticon(fi))
		}
	}
	setContent(
			new VerticalLayout().with {
				addComponent(
						new TextField().with{
							setInputPrompt("Search...")
							setTextChangeEventMode(EAGER)
							addTextChangeListener({ FieldEvents.TextChangeEvent e ->
								if (e.text) {
									updateGrid{ it.name().toLowerCase().contains(e.text.toLowerCase()) }
								} else {
									updateGrid{true}
								}
							})
							it
						}
				)
				addComponent(
						grid = new GridLayout().with{
                            columns = 4
                            it
                        }
				)
				it
			}
	)
	updateGrid{true}
	setSizeFull()
	addStyleName("borderless")
	it
}
