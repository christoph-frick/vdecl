import com.vaadin.data.HasValue
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.FontAwesome
import com.vaadin.ui.*

new Panel().with{
	GridLayout grid
	def fonticon = { fi ->
		new Label().with{
			caption = "${fi.getClass().simpleName}.${fi.name()} (0x${Integer.toString(fi.codepoint, 16)})"
			icon = fi
			it
		}
	}
	def updateGrid = { filter ->
		grid.removeAllComponents()
		(VaadinIcons.values() + FontAwesome.values()).findAll(filter).each { fi ->
			grid.addComponent(fonticon(fi))
		}
	}
	setContent(
			new VerticalLayout().with {
				addComponent(
						new TextField().with{
							setPlaceholder("Search...")
							addValueChangeListener({ HasValue.ValueChangeEvent<String> e ->
								if (e.value) {
									updateGrid{ it.name().toLowerCase().contains(e.value.toLowerCase()) }
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
