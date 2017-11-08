package pers.com.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import pers.com.constant.CommonConstant;

import java.util.Set;

/**
 * Created by minming.he on 2017/11/8.
 */
@Repository
public class RedisDao {

    @Autowired
    private RedisTemplate redisTemplate;

    public Set getSuccessList(){
        return redisTemplate.opsForSet().members(CommonConstant.RedisKey.SUCCESS_LIST);
    }

    public ListOperations getPacketsList(){
        return redisTemplate.opsForList();
    }

}
