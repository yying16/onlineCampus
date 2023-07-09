package com.campus.common.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.pojo.ServiceData;
import com.campus.common.util.IPageUtil;
import com.campus.common.util.R;
import com.campus.common.util.SpringContextUtil;
import com.campus.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author yying
 * 统一服务中心
 * redis缓存存储规则(key:表名+id）
 * 数据最终一致性（双删策略）
 * 缓存雪崩 设置随机的过期时间
 * 缓存穿透 （过滤id,缓存空对象）
 * 缓存击穿 （互斥锁）
 *
 * <h2>使用要点</h2>
 * 类的字段类型声明要用对象类型（不能是基本数据类型）
 * 必须包含如下变量
 * xxxxId
 * deleted
 * createTime
 * updateTime
 * <p>
 * 未实现多表连接
 */
@Service
public class ServiceCenter {

    final int maxRepetitions = 10; // 最大重复数
    final String lockKey = "CACHE_RESOURCE_KEY";
    final int minCacheDuration = 20; // 最小过期时间（分钟）
    final int maxCacheDuration = 60; // 最小过期时间（分钟）

    @Autowired
    public StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


//    /**
//     * 查询数据(错误）
//     * 结合具体业务
//     */
//    public <T> IPage search(Map<String, Object> condition, Class<T> clazz, long currentPage, long pageSize) {
//        try {
//            if (clazz == null || currentPage <= 0 || pageSize <= 0) { // 参数校验
//                return null;
//            }
//            String h = getName(clazz);
//            List<T> list = getCacheList(clazz);
//            list = list.stream().map(i -> JSONObject.parseObject(String.valueOf(i), clazz)).collect(Collectors.toList()); // 将json格式映射成clazz
//            if (condition == null || condition.isEmpty()) { // 查询所有
//                IPage iPage = IPageUtil.listToIPage(list, currentPage, pageSize);
//                return iPage;
//            } else { // 条件搜索
//                Field[] fields = clazz.getDeclaredFields();
//                List<Object> ans = new ArrayList(list);
//                List<Field> fieldList = Arrays.asList(fields); // 域变量集合
//                Map<String, Field> map = new HashMap<>(); // 域变量名称->域变量
//                List<String> fieldNames = fieldList.stream().map(Field::getName).collect(Collectors.toList()); // 域变量名称集合
//                Set<String> set = new HashSet<>(condition.keySet()); // 拷贝
//                for (String arg : set) {
//                    if (!fieldNames.contains(arg)) { // 删除掉没有域变量对应的条件
//                        condition.remove(arg);
//                    }
//                }
//                for (String arg : condition.keySet()) { // 遍历过滤后的条件
//                    Object value = condition.get(arg);
//                    Field field = clazz.getDeclaredField(arg);
//                    String fieldTypeName = field.getType().getTypeName();
//                    switch (fieldTypeName) { // 不考虑Float，Short，Character
//                        case "java.lang.Boolean": { // 判断是否值相同
//                            Boolean finalValue = Boolean.parseBoolean(value.toString());
//                            ans = ans.stream().filter(a -> Objects.equals(getArg(a, arg), finalValue)).collect(Collectors.toList());
//                            break;
//                        }
//                        case "java.lang.Integer": { // 先判断为单值匹配还是区间匹配
//                            if (String.valueOf(value).matches("\\d")) { // 单值匹配
//                                Object finalValue = Integer.parseInt(value.toString());
//                                ans = ans.stream().filter(a -> Objects.equals(getArg(a, arg), finalValue)).collect(Collectors.toList());
//                            } else { // 区间匹配格式 '1#2' 用#隔开
//                                String finalValue = String.valueOf(value);
//                                String[] se = finalValue.split("#");
//                                Integer s = Integer.parseInt(se[0]);
//                                Integer e = Integer.parseInt(se[0]);
//                                ans = ans.stream().filter(a -> {
//                                    Integer d = (Integer) getArg(a, arg);
//                                    return s < d && d < e;
//                                }).collect(Collectors.toList());
//                            }
//                            break;
//                        }
//                        case "java.lang.Long": {
//                            if (String.valueOf(value).matches("\\d")) { // 单值匹配
//                                Object finalValue = Long.parseLong(value.toString());
//                                ans = ans.stream().filter(a -> Objects.equals(getArg(a, arg), finalValue)).collect(Collectors.toList());
//                            } else { // 区间匹配格式 '1#2' 用#隔开
//                                String finalValue = String.valueOf(value);
//                                String[] se = finalValue.split("#");
//                                Long s = Long.parseLong(se[0]);
//                                Long e = Long.parseLong(se[0]);
//                                ans = ans.stream().filter(a -> {
//                                    Long d = (Long) getArg(a, arg);
//                                    return s < d && d < e;
//                                }).collect(Collectors.toList());
//                            }
//                            break;
//                        }
//                        case "java.lang.Double": {
//                            if (String.valueOf(value).matches("\\d")) { // 单值匹配
//                                Object finalValue = Double.parseDouble(value.toString());
//                                ans = ans.stream().filter(a -> Objects.equals(getArg(a, arg), finalValue)).collect(Collectors.toList());
//                            } else { // 区间匹配格式 '1#2' 用#隔开
//                                String finalValue = String.valueOf(value);
//                                String[] se = finalValue.split("#");
//                                Double s = Double.parseDouble(se[0]);
//                                Double e = Double.parseDouble(se[0]);
//                                ans = ans.stream().filter(a -> {
//                                    Double d = (Double) getArg(a, arg);
//                                    return s < d && d < e;
//                                }).collect(Collectors.toList());
//                            }
//                            break;
//                        }
//                        case "java.lang.String": { // 判断是否为时间（时间则区间对比，字符串则关键字匹配）
//                            String finalValue = String.valueOf(value);
//                            if (arg.endsWith("Time")) { // 时间
//                                String[] se = finalValue.split("#");
//                                Date s = TimeUtil.parse(se[0]); // 开始时间
//                                Date e = TimeUtil.parse(se[1]); // 结束时间
//                                long st = s.getTime();
//                                long et = e.getTime();
//                                ans = ans.stream().filter(a -> {
//                                    Date d = TimeUtil.parse(String.valueOf(getArg(a, arg)));
//                                    if (d == null) // 空值处理
//                                        return false;
//                                    long dt = d.getTime();
//                                    return st < dt && dt < et;
//                                }).collect(Collectors.toList());
//                                break;
//                            } else {
//                                ans = ans.stream().filter(a -> String.valueOf(getArg(a, arg)).contains(finalValue)).collect(Collectors.toList());
//                                break;
//                            }
//                        }
//                    }
//
//                }
//                return IPageUtil.listToIPage(ans,currentPage,pageSize);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    /**
     * 查询数据（单个）
     */
    public <T> Object search(String id, Class<T> clazz) {
        try {
            // 校验id
            if (id == null || id.length() < 19) {
                return null;
            }
            String h = getName(clazz);
            String cache = redisTemplate.opsForValue().get(h+id);
            if (cache!=null && cache.length()>0) { // 如果存在对应缓存
                Object ret = redisTemplate.opsForValue().get(h+id);
                return ret;
            } else { // 没有缓存
                redisTemplate.opsForValue().set(h+id,"{}",getCacheTime(),TimeUnit.SECONDS); // 返回空数据
                boolean b = tryLock(h, id); // true则表示获取锁成功
                if (!b) { // 没有得到互斥锁
                    return null;
                }
                ServiceData serviceData = new ServiceData(ServiceData.SELECT, null, clazz.getName());
                String data = JSONObject.toJSONString(serviceData);
                kafkaTemplate.send("service", getName(clazz), data);
                return clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 异步写入数据库
     * */
    public <T> boolean asyInsert(T t){
        ServiceData serviceData = new ServiceData(ServiceData.INSERT, t, t.getClass().getName());
        String data = JSONObject.toJSONString(serviceData);
        kafkaTemplate.send("service", getName(t, ""), data); // 异步更新数据库
        return true;
    }

    /**
     * 插入数据(先写入数据库，再写入缓存）
     */
    public <T> String insert(T t) {
        try {
            String h = getName(t, ""); //user
            String id = IdWorker.getIdStr(t);
            String key = h + id;
            setArg(t, h + "Id", id); // 设置id
            setArg(t, "deleted", false);
            setArg(t, "createTime", TimeUtil.getCurrentTime());
            setArg(t, "updateTime", TimeUtil.getCurrentTime());
            insertMySql(t);
            String json = JSONObject.toJSON(t).toString(); // 要写入redis的数据
            redisTemplate.opsForValue().set(key, json, getCacheTime(), TimeUnit.SECONDS); // 写入redis
            redisTemplate.opsForList().rightPush(h,id); // 记录缓存的id值（用于判断该id的内容是否存在缓存中）
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新数据
     */
    public <T> boolean update(T t) {
        try {
            // 删除redis
            String id = String.valueOf(getArg(t, getName(t, "Id")));
            ServiceData serviceData = new ServiceData(ServiceData.UPDATE, t, t.getClass().getName(), id);
            String data = JSONObject.toJSONString(serviceData);
            redisTemplate.opsForValue().getAndDelete(getName(t, id));// 删除redis缓存
            redisTemplate.opsForList().remove(getName(t,""),1,id); // 删除id缓存
            kafkaTemplate.send("service", getName(t, ""), data); // 异步更新数据库
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除数据
     */
    public <T> boolean delete(T t) {
        try {
            String methodName = "get" + t.getClass().getSimpleName() + "Id";
            Method method = t.getClass().getMethod(methodName);
            String id = String.valueOf(method.invoke(t));
            ServiceData serviceData = new ServiceData(ServiceData.DELETE, t, t.getClass().getName(), id);
            String data = JSONObject.toJSONString(serviceData);
            if(redisTemplate.opsForValue().get(getName(t,id))!=null){
                redisTemplate.opsForValue().getAndDelete(getName(t,id));
            }
            redisTemplate.opsForList().remove(getName(t,""),1,id); // 删除id缓存
            kafkaTemplate.send("service", getName(t, ""), data); // 异步更新数据库
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * kafka中间件(用于同步 redis 和 mysql数据）
     * topic: service
     * key: 用于设置分区，对应模块的crud
     * value:{ method: insert/update/delete , data: 要存储/更新（主键匹配）/修改的数据 }
     */
    @KafkaListener(topics = "service")
    public void tradeListener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            System.out.println("is ok");
            ServiceData serviceData = JSONObject.parseObject(record.value(), ServiceData.class);
            String json = String.valueOf(serviceData.getData());
            String type = serviceData.getType();
            Class<?> cls = Class.forName(type);
            Object data = JSONObject.parseObject(json, cls);
            switch (serviceData.getMethod()) {
                case ServiceData.INSERT: { // 插入
                    insertMySql(data);
                    break;
                }
                case ServiceData.UPDATE: { // 更新
                    update(data, serviceData.getId());
                    break;
                }
                case ServiceData.DELETE: { // 删除
                    deleteMySql(data, serviceData.getId());
                    break;
                }
                case ServiceData.SELECT: { // 删除
                    System.out.println("ServiceData.SELECT");
                    selectMySql(serviceData.getId(), cls);
                    break;
                }
            }
            ack.acknowledge();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id查询数据
     */
    public <T> Object selectMySql(String id, Class<T> clazz) {
        try {
            BaseMapper<T> mapper = getMapper(clazz.newInstance());
            String h = getName(clazz);
            Object ret = mapper.selectById(id);
            String value = JSONObject.toJSONString(ret);
            redisTemplate.opsForValue().set(h+id,value,getCacheTime(),TimeUnit.SECONDS);
            unlock(h, id); // 解锁
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 插入(Mysql)
     */
    public <T> boolean insertMySql(T t) {
        try {
            System.out.println("insertMySql");
            BaseMapper<T> mapper = getMapper(t);
            mapper.insert(t);
            System.out.println("insertMySql is ok");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新(Mysql)
     */
    public <T> boolean updateMySql(T t) {
        try {
            BaseMapper<T> mapper = getMapper(t);
            mapper.updateById(t);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 用于异步更新
     */
    public void update(Object t, String id) {
        // 删除mysql中的数据
        int flag = 2;
        while (0 < flag && flag <= maxRepetitions) { // flag 在 ( 0 , maxRepetitions ] 区间循环，每次错误就flag++
            flag = updateMySql(t) ? 0 : flag + 1;
        }
        if (flag == 0) { // 更新成功
            // 双删
            redisTemplate.opsForValue().getAndDelete(getName(t,id)); // 二次删除redis缓存
            redisTemplate.opsForList().remove(getName(t,""),1,id); // 删除id缓存
        }
    }

    /**
     * 逻辑删除(Mysql)
     */
    public <T> boolean deleteMySql(T t, String id) {
        try {
            BaseMapper<T> mapper = getMapper(t);
            mapper.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private <T> BaseMapper<T> getMapper(T t) { // 获取mapper对象
        return (BaseMapper<T>) SpringContextUtil.getBean(getName(t, "Dao"));
    }

    private String getName(Object object, String suffix) { // 类名小写+后缀
        String className = object.getClass().getSimpleName();
        char[] chars = className.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]); // 转为小写
        return String.valueOf(chars) + suffix;
    }

    private String getName(Class<?> clazz) { // 类名小写+后缀
        String className = clazz.getSimpleName();
        char[] chars = className.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]); // 转为小写
        return String.valueOf(chars);
    }

//    /**
//     * 随机获取缓存时长(30,60)
//     * 用于解决缓存雪崩
//     * */
//    public int getCacheDuration(){
//        Random random = new Random();
//        int time = minCacheDuration + random.nextInt() % (maxCacheDuration-minCacheDuration); // 随机生成1800-3600
//        return time;
//    }

    /**
     * 加锁
     */
    private boolean tryLock(String h, String id) {
        String key = lockKey + "_" + h + "_" + id;
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, key, Duration.ofSeconds(20)));
    }

    /**
     * 解锁
     */
    private boolean unlock(String h, String id) {
        String key = lockKey + "_" + h + "_" + id;
        redisTemplate.delete(key);
        return true;
    }

    /**
     * 通过反射设置变量值
     *
     * @param argName 变量名称
     * @param value   变量值
     */
    private <T> boolean setArg(T t, String argName, Object value) {
        try {
            char[] chars = argName.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]); // 首字母转为大写
            argName = String.valueOf(chars);
            String methodName;
            methodName = "set" + argName;
            Method method = t.getClass().getMethod(methodName, value.getClass());
            method.invoke(t, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 通过反射获取变量值
     *
     * @param argName 变量名称
     */
    private <T> Object getArg(T t, String argName) {
        try {
            char[] chars = argName.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]); // 首字母转为大写
            argName = String.valueOf(chars);
            String methodName = "get" + argName;
            Method method = t.getClass().getMethod(methodName);
            Object ret = method.invoke(t);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取[minCacheDuration,maxCacheDuration]之间的随机值
     */
    private int getCacheTime() {
        Random random = new Random();
        double ran = random.nextDouble();
        int time = minCacheDuration * 60 + (int) (Math.floor(ran * (maxCacheDuration - minCacheDuration))*60);
        return time;
    }

    /**
     * 获取redis中某个类的缓存列表
     */
    private <T> List getCacheList(Class<T> cls) {
        List<T> ret = new ArrayList<>();
        String h = getName(cls);
        List<String> list = redisTemplate.opsForList().range(h,0,-1);
        list.forEach(s -> {
            String st = redisTemplate.opsForValue().get(h+s);
            if(st!=null&&st.length()>0){ // 存在缓存
                ret.add(JSONObject.parseObject(st,cls));
            }else{ // 不存在缓存
                redisTemplate.opsForList().remove(h,1,s); // 删除id缓存
            }
        });
        return ret;
    }

//    public static void main(String[] args) throws NoSuchFieldException {
//        ServiceCenter center = new ServiceCenter();
//        Test test = new Test();
//        center.setArg(test, "A", 10);
//        center.setArg(test, "B", 20);
//
//    }
//
//    static class Test {
//        Integer a;
//        Long b;
//        Date c;
//        Double d;
//        String e;
//        Boolean f;
//
//
//        @Override
//        public String toString() {
//            return "Test{" + "a=" + a + ", b=" + b + '}';
//        }
//    }
}
