package org.red5.examples.springmvc.controller;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BaseController {

    private static Logger log = Red5LoggerFactory.getLogger(BaseController.class, "oflaDemo");

    private static final String VIEW_INDEX = "index";

    private static int counter = 0;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcome(ModelMap model) {
        model.addAttribute("message", "Welcome");
        model.addAttribute("counter", ++counter);
        log.debug("[welcome] counter : {}", counter);
        // Spring uses InternalResourceViewResolver and return back index.jsp
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public String welcomeName(@PathVariable String name, ModelMap model) {
        model.addAttribute("message", "Welcome " + name);
        model.addAttribute("counter", ++counter);
        log.debug("[welcomeName] counter : {}", counter);
        return VIEW_INDEX;
    }

}
