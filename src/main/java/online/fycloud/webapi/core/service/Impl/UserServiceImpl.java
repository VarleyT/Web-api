package online.fycloud.webapi.core.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.fycloud.webapi.core.data.SignInUser;
import online.fycloud.webapi.core.data.SignUpUser;
import online.fycloud.webapi.core.entity.User;
import online.fycloud.webapi.core.mapper.UserMapper;
import online.fycloud.webapi.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * @author VarleyT
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public String register(SignUpUser user) {
        String username = user.getUsername();
        String password = user.getPassword();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
        boolean flag = userMapper.exists(queryWrapper);
        if (flag) {
            return "用户名已存在";
        }
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        User newUser = new User(null, username, password, new Date(), 1);
        int insertNum = userMapper.insert(newUser);
        if (insertNum > 0) {
            return null;
        } else {
            return "注册失败！服务器出错！";
        }
    }

    @Override
    public String login(SignInUser user) {
        String userName = user.getUsername();
        String passwd = user.getPassword();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        List<User> userList = userMapper.selectList(queryWrapper);
        if (userList.isEmpty()) {
            return "用户名不存在，请注册！";
        }
        if (userList.size() > 1) {
            return "账号异常！请联系管理员";
        }
        User result = userList.get(0);
        passwd = DigestUtils.md5DigestAsHex(passwd.getBytes());
        if (!result.getPasswd().equals(passwd)) {
            return "用户名或密码错误！";
        }
        if (result.getStatus() == 0) {
            return "账号已封禁！";
        }
        return null;
    }
}
