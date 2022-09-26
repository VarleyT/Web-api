package online.fycloud.webapi.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author VarleyT
 */
@RestController
public class IndexController {
    @RequestMapping({"/index", "/"})
    public String index() {
        return "ok";
    }
}
