package online.fycloud.webapi.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import online.fycloud.webapi.core.data.LoginUser;
import online.fycloud.webapi.core.entity.UserSign;

/**
 * @author VarleyT
 * @date 2022/9/20
 */
public interface UserSignService extends IService<UserSign> {
    /**
     * 用户签到
     *
     * @param loginUser
     * @return
     */
    String sign(LoginUser loginUser);
}
