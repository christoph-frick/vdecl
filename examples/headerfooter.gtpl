"v-vertical-layout"("size-full":true, spacing: true) {
	includeGroovy("header.gtpl")
	"v-panel"("size-full":true, ":expand": "1") {
		"v-vertical-layout" {
			(99..2).each{ "v-label"("${it} Bottles of Beer") }
		}
	}
	includeGroovy("footer.gtpl")
}
