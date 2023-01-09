package online.fycloud.webapi.core.service.Impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.fycloud.webapi.core.data.LoginUser;
import online.fycloud.webapi.core.entity.UserSign;
import online.fycloud.webapi.core.mapper.UserSignMapper;
import online.fycloud.webapi.core.service.UserSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author VarleyT
 * @date 2022/9/20
 */
@Service
public class UserSignServiceImpl extends ServiceImpl<UserSignMapper, UserSign> implements UserSignService {
    @Autowired
    private UserSignMapper userSignMapper;

    @Override
    public String sign(LoginUser loginUser) {
        Integer id = loginUser.getId();
        String userName = loginUser.getUserName();
        LambdaQueryWrapper<UserSign> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSign::getId, id);
        boolean exist = userSignMapper.exists(wrapper);
        if (exist) {
            UserSign user = userSignMapper.selectOne(wrapper);
            Date signTime = user.getSignTime();
            Date now = new Date();
            boolean sameDay = DateUtil.isSameDay(signTime, now);
            if (!sameDay) {
                UserSign userSign = new UserSign(id, userName, 1, now);
                DateTime yesterday = DateUtil.offsetDay(now, -1);
                if (DateUtil.isSameDay(signTime, yesterday)) {
                    userSign.setFrequency(user.getFrequency() + 1);
                }
                userSignMapper.updateById(userSign);
                return "签到成功！您已累计签到：" + user.getFrequency() + "次";
            } else {
                return "您今天已经签到过啦！";
            }
        } else {
            UserSign userSign = new UserSign(id, userName, 1, new Date());
            int insert = userSignMapper.insert(userSign);
            if (insert == 1) {
                return "签到成功！";
            }
        }
        log.error("签到出错！");
        return null;
    }
}
