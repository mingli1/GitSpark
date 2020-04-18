package com.gitspark.gitspark.ui.custom

import br.tiagohm.markdownview.css.styles.Github

class LightMarkdownStyle : Github() {

    init {
        addRule("body", "height: 100%", "line-height: 1.6", "padding-top: 16px")
    }
}