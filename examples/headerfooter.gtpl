"v-vertical-layout"("size-full":true, spacing: true) {
	"v-vertical-layout" {
		"v-label"("style-name":"h1 colored", "My Header")
	}
	"v-panel"("size-full":true, ":expand": "1") {
		"v-vertical-layout" {
			(99..2).each{ "v-label"("${it} Bottles of Beer") }
		}
	}
	"v-vertical-layout" {
		"v-label"("style-name": "small", "© (FIXME: utf-8 encoding borked, but entity works) &copy; Copyright 2015 Zomg Corp")
	}
}
