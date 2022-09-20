package online.fycloud.webapi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import online.fycloud.webapi.core.data.SignInUser;
import online.fycloud.webapi.core.data.SignUpUser;
import online.fycloud.webapi.core.entity.User;

/**
 * @author VarleyT
 */

public interface UserService extends IService<User> {
    /**
     * 新增用户
     *
     * @param user
     * @return 错误信息
     */
    String register(SignUpUser user);

    /**
     * 用户登录
     *
     * @param user
     * @return 错误信息
     */
    Object login(SignInUser user);
}
