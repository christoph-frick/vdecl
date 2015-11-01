import com.vaadin.ui.*
import com.vaadin.server.FontAwesome

new GridLayout().with {
	columns = 4
	FontAwesome.values().each{ fontIcon ->
		addComponent(new Label().with{
			caption = "${fontIcon.name()} (0x${Integer.toString(fontIcon.codepoint, 16)})"
			icon = fontIcon
			it
		})
	}
	it
}

