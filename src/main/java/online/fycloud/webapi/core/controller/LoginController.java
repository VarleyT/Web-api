package online.fycloud.webapi.core.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import lombok.extern.slf4j.Slf4j;
import online.fycloud.webapi.core.annotation.CheckLogin;
import online.fycloud.webapi.core.annotation.LimitRequest;
import online.fycloud.webapi.core.common.R;
import online.fycloud.webapi.core.data.LoginUser;
import online.fycloud.webapi.core.data.SignInUser;
import online.fycloud.webapi.core.data.SignUpUser;
import online.fycloud.webapi.core.exception.ServerException;
import online.fycloud.webapi.core.service.UserService;
import online.fycloud.webapi.core.service.UserSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private UserSignService userSignService;

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
                           @RequestBody @Validated SignInUser user) throws ServerException {
        Object obj = request.getSession().getAttribute(LOGIN_USER);
        if (obj != null) {
            return R.success("用户已登录!");
        }
        Object object = userService.login(user);
        if (object instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) object;
            request.getSession().setAttribute(LOGIN_USER, loginUser);
            return R.success("登录成功");
        }
        throw new ServerException(((String) object));
    }

    /**
     * 用户注册
     *
     * @param request
     * @param user
     * @return
     */
    @LimitRequest
    @PostMapping("/register")
    public Object register(HttpServletRequest request,
                           @RequestBody @Validated SignUpUser user) throws ServerException {
        String verifyCode;
        try {
            Object obj = request.getSession(false).getAttribute(VERIFY_CODE);
            verifyCode = (String) obj;
        } catch (NullPointerException e) {
            throw new ServerException("请先获取验证码！");
        }
        if (!verifyCode.equals(user.getVerify())) {
            throw new ServerException("验证码错误！");
        }
        String msg = userService.register(user);
        if (msg == null) {
            return R.success("注册成功！");
        }
        throw new ServerException(msg);
    }

    /**
     * 用户登出
     *
     * @param request
     * @return
     */
    @LimitRequest
    @GetMapping("/logout")
    public R<String> logout(HttpServletRequest request) throws ServerException {
        if (request.getSession().getAttribute(LOGIN_USER) == null) {
            throw new ServerException("未登录！");
        }
        request.getSession().removeAttribute(LOGIN_USER);
        return R.success();
    }

    /**
     * 验证码获取
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @LimitRequest(time = 30000, count = 3)
    @GetMapping("/verify")
    public R<String> verify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(300, 100, 4, 4);
        captcha.write(response.getOutputStream());
        String verifyCode = captcha.getCode();
        request.getSession().setAttribute(VERIFY_CODE, verifyCode);
        return null;
    }

    /**
     * 用户签到
     *
     * @param request
     * @return
     */
    @CheckLogin
    @LimitRequest
    @GetMapping("/sign")
    public R<String> sign(HttpServletRequest request) throws ServerException {
        LoginUser loginUser = (LoginUser) request.getSession(false).getAttribute(LOGIN_USER);
        if (loginUser == null) {
            log.error("签到失败！loginUser为空！");
            throw new ServerException("签到失败，请先登录再进行操作！");
        }
        String msg = userSignService.sign(loginUser);
        if (msg != null) {
            return R.success(msg);
        } else {
            throw new ServerException("签到失败！");
        }
    }
}
