"v-panel"(caption:"Please Login") {
	"v-vertical-layout"("size-auto": true, margin: true,  spacing: true) {
		"v-text-field"(caption:"Username")
		"v-password-field"(caption:"Password")
		"v-check-box"(caption: "Remember Me")
		"v-horizontal-layout"(spacing: true) {
			"v-button"("Reset password")
			"v-button"("style-name":"primary", "Login")
		}
	}
}
