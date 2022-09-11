package online.fycloud.webapi.core.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import lombok.extern.slf4j.Slf4j;
import online.fycloud.webapi.core.annotation.LimitRequest;
import online.fycloud.webapi.core.common.R;
import online.fycloud.webapi.core.data.SignInUser;
import online.fycloud.webapi.core.data.SignUpUser;
import online.fycloud.webapi.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;

/**
 * @author VarleyT
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
public class LoginController {

    private final String LOGIN_USER = "loginUser";
    private final String VERIFY_CODE = "verifyCode";
    @Autowired
    private UserService userService;

    /**
     * 登录
     *
     * @param request
     * @param user
     * @return
     */
    @LimitRequest(time = 5000, count = 3)
    @PostMapping("/login")
    public R<String> login(HttpServletRequest request,
                           @RequestBody @Validated SignInUser user) {
        Object obj = request.getSession().getAttribute(LOGIN_USER);
        if (obj != null) {
            return R.success("用户已登录!");
        }
        String msg = userService.login(user);
        if (msg == null) {
            request.getSession().setAttribute(LOGIN_USER, user.getUsername());
            return R.success("登录成功");
        }
        return R.error(msg);
    }

    @LimitRequest
    @PostMapping("/register")
    public R<String> register(HttpServletRequest request,
                              @RequestBody @Validated SignUpUser user,
                              @RequestParam(value = "verify") @NotEmpty String code) {
        Object obj;
        try {
            obj = request.getSession(false).getAttribute(VERIFY_CODE);
        } catch (NullPointerException e) {
            return R.error("请先获取验证码！");
        }
        String verifyCode = (String) obj;
        if (!verifyCode.equals(code)) {
            return R.error("验证码错误！");
        }
        String msg = userService.register(user);
        if (msg == null) {
            return R.success("注册成功！");
        }
        return R.error(msg);
    }

    @LimitRequest
    @GetMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        if (request.getSession().getAttribute(LOGIN_USER) == null) {
            return R.error("未登录！");
        }
        request.getSession().removeAttribute(LOGIN_USER);
        return R.success();
    }

    @LimitRequest(time = 30000, count = 3)
    @GetMapping("/verify")
    public R<String> verify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 100, 4, 4);
        captcha.write(response.getOutputStream());
        String verifyCode = captcha.getCode();
        request.getSession().setAttribute(VERIFY_CODE, verifyCode);
        return null;
    }
}
