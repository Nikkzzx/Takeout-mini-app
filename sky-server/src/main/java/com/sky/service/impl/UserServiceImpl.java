package com.sky.service.impl;

import com.alibaba.druid.util.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    //微信服务接口地址
    public static final String WX_LOGIN= "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;

    // 微信登录
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenId(userLoginDTO.getCode());
        //判断openid是否为空，如果为空则表示登陆失败，抛出业务异常
        if (openid==null){
            log.error("微信登录失败");
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //调用用户 Mapper层方法获取用户信息
        User user = userMapper.getByOpenId(openid);
        //如果是新用户则需要自动完成注册
        if (user==null){
            user=User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回当前微信用户对象
        return user;
    }

    //调用微信接口
    private String getOpenId(String code) {
        //给当前登录的微信账户信息赋值，发送HTTP请求，获得JSON格式的数据
        Map<String,String> map=new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json= HttpClientUtil.doGet(WX_LOGIN,map);

        //把JSON格式的数据转换为JSONObject对象，方便获取当前微信用户的 openid
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
