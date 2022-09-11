package online.fycloud.webapi.common.controller;

import online.fycloud.webapi.core.annotation.CheckLogin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author VarleyT
 */
@RestController
public class IndexController {
    @RequestMapping({"/index", "/"})
    @CheckLogin
    public String index() {
        return "ok";
    }
}
