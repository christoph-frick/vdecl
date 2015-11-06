import com.vaadin.ui.*
import com.vaadin.server.Responsive
import com.vaadin.ui.themes.ValoTheme
import com.vaadin.server.FontAwesome
import com.vaadin.shared.ui.label.ContentMode

class MainMenu extends CssLayout {

	private Label title
	private CssLayout menuItemsLayout

	MainMenu() {
		addStyleName(ValoTheme.MENU_ROOT)
		addComponent(new CssLayout(
			new HorizontalLayout(
				title = new Label("Application", ContentMode.HTML).with{
					setSizeUndefined()
					it
				}
			).with{
				addStyleName(ValoTheme.MENU_TITLE)
				getComponent(0).with{
					setExpandRatio(it, 1.0)
					setComponentAlignment(it, Alignment.MIDDLE_CENTER)
				}
				setWidth("100%")
				it
			},
			new Button("Menu").with{
				setIcon(FontAwesome.LIST)
				addStyleName("primary small valo-menu-toggle")
				addClickListener{ toggleMenu() }
				it
			},
			menuItemsLayout = new CssLayout().with{
				addStyleName('valo-menuitems')	
				it
			}
		).with{
			addStyleName(ValoTheme.MENU_PART)
			it
		})
	}

	void setTitle(String html) {
		title.value = html
	}

	void addMenuItem(Button b) {
		b.setPrimaryStyleName(ValoTheme.MENU_ITEM)
		menuItemsLayout.addComponent(b)
	}

	private static final String MENU_VISIBLE_STYLE = 'valo-menu-visible'

	void toggleMenu() {
		(styleName.contains(MENU_VISIBLE_STYLE) ? this.&removeStyleName : this.&addStyleName)(MENU_VISIBLE_STYLE)
	}
}

/*
 * Example
 */

def _dummyContent = {
	new Panel("Some Content", 
		new VerticalLayout(new Label("Some Content")).with{
			setMargin(true)
			it
		})
}

new HorizontalLayout( // layout for page; needs some responsive setup too (see below)
	new MainMenu().with{ // the menu
		setTitle("Corp Inc. <strong>Application</strong>")
		addMenuItem(new Button(caption:"Home", icon:FontAwesome.HOME))
		addMenuItem(new Button(caption:"Users", icon:FontAwesome.USERS))
		addMenuItem(new Button(caption:"Corp Stuff", icon:FontAwesome.PAPER_PLANE))
		it
	}, 
	new Panel( // make the main content scrollable
		new VerticalLayout(
			_dummyContent().with{ addStyleName(ValoTheme.PANEL_WELL); it }
		).with{
			20.times{ addComponent(_dummyContent()) }
			setSpacing(true)
			setMargin(true)
			setWidth("100%")
			setHeight(null)
			it
		}
	).with{
		setPrimaryStyleName(ValoTheme.PANEL_BORDERLESS)	
		setSizeFull()
		it
	}
).with{
	setSizeFull()
	setExpandRatio(getComponent(1), 1.0)
	// XXX: the the horizontal layout of the page needs some setup too, to make the menu work
	Responsive.makeResponsive(it)
	addStyleName('valo-menu-responsive')
	it
}
