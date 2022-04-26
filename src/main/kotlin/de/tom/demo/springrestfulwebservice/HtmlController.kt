package de.tom.demo.springrestfulwebservice

import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HtmlController {

    @GetMapping(path = ["/hello"])
    fun blog(model: Model): String {
        model["title"] = "Blog"
        return "<h1>Hello</h1>"
    }

}