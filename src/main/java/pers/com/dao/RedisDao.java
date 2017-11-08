package pers.com.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import pers.com.constant.CommonConstant;

import java.util.Set;

/**
 * Created by chenmutime on 2017/11/8.
 */
@Repository
public class RedisDao {

    @Autowired
    private RedisTemplate redisTemplate;

    public void addToSuccessList(String ele){
        redisTemplate.opsForSet().add(CommonConstant.RedisKey.SUCCESS_LIST, ele);
    }

    public boolean isMemberOfSuccessList(String ele){
        return redisTemplate.opsForSet().isMember(CommonConstant.RedisKey.SUCCESS_LIST, ele);
    }

    public Long getSizeOfSuccessList(){
        return redisTemplate.opsForSet().size(CommonConstant.RedisKey.SUCCESS_LIST);
    }

    public ListOperations getPacketsList(){
        return redisTemplate.opsForList();
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }
}
