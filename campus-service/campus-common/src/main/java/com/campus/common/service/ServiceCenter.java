package com.campus.common.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.bloomFilter.BloomFilterService;
import com.campus.common.pojo.Image;
import com.campus.common.util.SpringContextUtil;
import com.campus.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.MethodInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;
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
 */
@Service
@Slf4j
public class ServiceCenter {

    static final int maxRepetitions = 10; // 最大重复数
    static final String lockKey = "CACHE_RESOURCE_KEY";
    static final int minCacheDuration = 20; // 最小过期时间（分钟）
    static final int maxCacheDuration = 40; // 最大过期时间（分钟）
    static final long taskFlushInterval = 30; // 任务刷新间隔(分钟)
    static final int refreshRetryTimes = 5; // 刷新任务重试次数
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    DataSource campusDataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BloomFilterService bloomFilterService;

    private ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor(); // 线程池




    /**
     * 从用户登录开始设置一个计时器，每五分钟更新一次高频词汇，并将高频词汇写入redis
     *
     * 猜你喜欢（点击搜索框弹出下拉窗口）
     * <p>
     * 协同过滤算法获取相关产品（10个）
     * 数据库获取最近访问的产品（10个）
     * 结巴分词获取高频词汇(过滤掉无效高频词)
     */
    public <T> List<String> guessYouLikes(String uid, Class<T> clazz, String titleArg) {
        try {
            String className = getName(clazz);
            String tableName = "t_" + className + "_record";
            String itemIDColumn = className + "_id";
            List<String> list1 = getNewRecord(tableName, itemIDColumn, uid, 10);
            DataModel model = new MySQLJDBCDataModel(campusDataSource, tableName, "user_id", itemIDColumn, "score", "visit_time");
            // 计算用户之间的相似度，这里使用皮尔逊相关系数作为相似度度量
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.3, similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            List<RecommendedItem> recommendations = recommender.recommend(Long.parseLong(uid), 20 - list1.size());
            List<String> list2 = recommendations.stream().map(r -> String.valueOf(r.getItemID())).collect(Collectors.toList());
            list1.addAll(list2); // 连接列表
            List<T> sources = getTuplesByIds(list1, clazz);
            List<String> ret = new ArrayList<>();
            for (T source : sources) {
                ret.add(String.valueOf(getArg(source, titleArg)));
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * jieba分词处理
     *
     * 录入字典（从nacos获取数据）
     * 分词
     * 过滤词性（保留名词）
     * 统计高频词
     *
     * 返回最高频的10个词
     */
    private static List<String> jiebaAnalyse(String content) {
//        JiebaSegmenter segmenter = new JiebaSegmenter();
//        List<SegToken> tokens = segmenter.process(content, JiebaSegmenter.SegMode.SEARCH);
//        return tokens.stream().map(token->token.word).collect(Collectors.toList());
        return null;
    }


    private List<String> getNewRecord(String tableName, String itemIdColumn, String uid, Integer num) {
        try {
            String sql = "select " + itemIdColumn + " from " + tableName + " where user_id = '" + uid + "' limit " + num;
            return jdbcTemplate.queryForList(sql, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    /**
     * 个性化推荐(用于用户浏览【兼职/交易】之后滚到最下方的猜你喜欢推荐）
     * <p>
     * 使用基于物品的协同过滤推荐
     * 同时将新访问的数据写入记录表
     *
     * @param uid      当前登录用户id
     * @param targetId 浏览目标id（job_id/product_id/recruit_id)
     * @param num      推荐个数（懒加载）
     * @return 推荐的首页项列表
     */
    public <T> List<T> personalizedRecommendation(String uid, String targetId, Integer num, Class<T> clazz) {
        try {
            String tableName = "t_" + getName(clazz) + "_record"; // 表名为t_XXX_record
            String itemIDColumn = getName(clazz) + "_id"; // 对应字段为XXX_id
            long u = Long.parseLong(uid);
            long t = Long.parseLong(targetId);
            DataModel dataModel = new MySQLJDBCDataModel(campusDataSource, tableName, "user_id", itemIDColumn, "score", "visit_time");
            // 计算相似度
            ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
            // 构建推荐器，使用基于物品的协同过滤推荐
            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
            List<RecommendedItem> recommendedItemList = recommender.recommendedBecause(u, t, num);
            recommendedItemList.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));
            insertRecord(uid, targetId, clazz, 1.0); // 修改记录表
            List<String> ids = recommendedItemList.stream().map(item -> String.valueOf(item.getItemID())).collect(Collectors.toList());
            return getTuplesByIds(ids, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            List<String> list = JSONArray.parseArray(String.valueOf(redisTemplate.opsForList().range(getName(clazz), 0, num)), String.class);
            return getTuplesByIds(list, clazz);// 返回最新的列表
        }
    }

    /**
     * 记录用户行为(一般用于绑定用户的浏览/点赞/收藏行为）
     * 如果存在该元组（用户先前访问过）则t.score+=score
     * 否则初始化score = 1
     */
    @Async
    public <T> void insertRecord(String uid, String targetId, Class<T> clazz, Double score) {
        if (score == null)
            score = 1.0;
        String tableName = "t_" + getName(clazz) + "_record"; // 表名为t_XXX_record
        String itemIDColumn = getName(clazz) + "_id"; // 对应字段为XXX_id
        String sql = "select COUNT(*) from " + tableName + " where " + itemIDColumn + " = '" + targetId + "'";
        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class);
        assert cnt != null;
        if (cnt.equals(0)) { // 第一次访问,插入数据
            String idColumn = getName(clazz) + "_record_id";
            String id = IdWorker.getTimeId();
            String sql4insert = "insert into t_job_record(" + idColumn + ", " + itemIDColumn + ", " + itemIDColumn + ", score, visit_time) " +
                    "values(" + id + "," + uid + "," + targetId + ",1.0," + System.currentTimeMillis() + ")";
            jdbcTemplate.execute(sql4insert); // 执行插入语句
        } else { // 不是第一次访问，更新数据
            String sql4update = "update " + tableName + " set score = score " + (score > 0 ? "+" : "-") + score + " where " + itemIDColumn + " = '" + targetId + "' and user_id = '" + uid + "'";
            jdbcTemplate.execute(sql4update); // 执行更新语句
        }
    }

    /**
     * 根据id列表返回对应的元组
     * <p>
     * 根据id列表在对应的表中查找对应的元组
     * <p>
     * 在图片表中找到对应的图片列表
     * <p>
     * 实体装配上图片
     */
    public <T> List<T> getTuplesByIds(List<String> ids, Class<T> clazz) {
        String tableName = "t_" + getName(clazz);
        String idColumn = getName(clazz) + "_id";
        String idsStr = "(";
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        idsStr += ids.get(0);
        for (int i = 1; i < ids.size(); i++) {
            idsStr = idsStr + "," + ids.get(i);
        }
        idsStr += ")";
        String sql = "select * from " + tableName + " where deleted=0 and " + idColumn + " in " + idsStr;
        List ret = jdbcTemplate.query(sql, new BeanPropertyRowMapper(clazz)); // 元组列表
        String sql4photo = "select * from t_image where deleted=0 and other_type = '" + getName(clazz) + "' and other_id in " + idsStr;
        List<Image> images = jdbcTemplate.query(sql4photo, new BeanPropertyRowMapper(Image.class));
        Map<String, List<String>> imageMap = new HashMap<>();
        images.forEach(image -> {  // 将图片分类到imageMap
            List list = imageMap.getOrDefault(image.getOtherId(), new ArrayList());
            list.add(image.getImgUrl());
            imageMap.put(image.getOtherId(), list);
        });
        String id = getName(clazz) + "Id";
        for (int i = 0; i < ret.size(); i++) {
            String tId = String.valueOf(getArg(ret.get(i), id));
            setArg(ret.get(i), "images", imageMap.get(tId));
        }
        return ret;
    }


    /**
     * 插入图片数据表
     *
     * @param images  图片url列表
     * @param otherId 图片连接的id
     */
    public <T> boolean insertImage(List<String> images, String otherId, Class<T> clazz) {
        try {
            StringBuilder str = new StringBuilder("insert into t_image(img_id, img_url, other_type, other_id) values ");
            for (int i = 0; i < images.size(); i++) {
                str.append("('");
                str.append(IdWorker.getIdStr(new Image())); // img_id
                str.append("','");
                str.append(images.get(i)); // img_url
                str.append("','");
                str.append(getName(clazz)); //  other_type
                str.append("','");
                str.append(otherId); //  other_id
                str.append("')");
                if (i != images.size() - 1) {
                    str.append(",");
                }
            }
            jdbcTemplate.execute(str.toString()); // 执行sql语句
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 插入图片数据表
     *
     * @param otherId 图片连接的id
     * @param clazz   连接图片的类
     */
    public <T> List getImage(String otherId, Class<T> clazz) {
        try {
            String sql = "select img_url from t_image where deleted = 0 and other_id='" + otherId + "' and other_type='" + getName(clazz) + "';";
            return jdbcTemplate.queryForList(sql, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


//    /**
//     * kafka中间件(用于同步 redis 和 mysql数据）
//     * topic: service
//     * key: 用于设置分区，对应模块的crud
//     * value:{ method: insert/update/delete , data: 要存储/更新（主键匹配）/修改的数据 }
//     */
//    @KafkaListener(topics = "service")
//    public void tradeListener(ConsumerRecord<String, String> record, Acknowledgment ack) {
//        try {
//            ServiceData serviceData = JSONObject.parseObject(record.value(), ServiceData.class);
//            String json = String.valueOf(serviceData.getData());
//            String type = serviceData.getType();
//            Class<?> cls = Class.forName(type);
//            Object data = JSONObject.parseObject(json, cls); // 类型转换
//            switch (serviceData.getMethod()) {
//                case ServiceData.INCREMENT: { // 自增
////                    increment(JSONObject.parseObject(json, IncrementData.class));
//                    break;
//                }
//                case ServiceData.INSERT: { // 插入
//                    insertMySql(data);
//                    break;
//                }
//                case ServiceData.UPDATE: { // 更新
//                    update(data, serviceData.getId());
//                    break;
//                }
//                case ServiceData.DELETE: { // 删除
//                    deleteMySql(data.getClass(), serviceData.getId());
//                    break;
//                }
//                case ServiceData.SELECT: { // 查找
//                    selectMySqlForCache(serviceData.getId(), cls);
//                    break;
//                }
//            }
//            ack.acknowledge();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 自增函数(对应字段数据类型必须为Integer)
     * 因为此类方法对高并发要求较高，因此采用异步更新持久化数据方式
     * 更新redis和mysql的自增字段（mysql异步更新）
     * redis缓存结构必须为value，且键为className+id（如product111）
     * 如果不是这个数据结构的话就只更新mysql
     * <p>
     * 用于访问量/收藏量/关注数量 ++
     *
     * @param id    需要自增的元组主键（如product的productId)
     * @param clazz 需要自增的元组所属的表对应的类（如 t_product -> clazz = Product.class)
     * @param args  需要自增的属性（驼峰命名）（如productNum）
     * @param async 是否异步
     * @return 是否修改成功（即修改已有的缓存以及发出异步mysql修改信息）
     */
    public <T> boolean increment(String id, Class<T> clazz, Boolean async, String... args) {
        //更新redis
        String h = getName(clazz) + id;
        String jsonStr = redisTemplate.opsForValue().get(h);
        if (jsonStr != null) { // 缓存格式匹配且存在缓存
            T t = JSONObject.parseObject(jsonStr, clazz);
            for (String arg : args) { // 依次修改变量值
                setArg(t, arg, ((Integer) getArg(t, arg)) + 1); // 自增
            }
            jsonStr = JSONObject.toJSONString(t);
            redisTemplate.opsForValue().set(h, jsonStr, getCacheTime(), TimeUnit.SECONDS); // 重新写入redis

        }
        if (async) {
            asyncIncrementMysql(id, clazz, args);
        } else {
            incrementMysql(id, clazz, args);
        }

        //异步更新数据库
//        IncrementData<T> data = new IncrementData(id, clazz, args);
//        ServiceData serviceData = new ServiceData(ServiceData.INCREMENT, data, clazz.getName());
//        String dataStr = JSONObject.toJSONString(serviceData);
//        kafkaTemplate.send("service", getName(clazz), dataStr); // 异步更新数据库
        return true;
    }

    /**
     * 异步修改数据库进行自增
     */
    @Async
    <T> boolean asyncIncrementMysql(String id, Class<T> clazz, String... args) {
        BaseMapper<T> mapper = getMapper(clazz);
        T t = mapper.selectById(id);
        for (String arg : args) {
            setArg(t, arg, ((Integer) getArg(t, arg)) + 1); // 自增
        }
        mapper.updateById(t);
        log.info("异步写入数据成功");
        return true;
    }

    /**
     * 同步自增
     */
    private <T> boolean incrementMysql(String id, Class<T> clazz, String... args) {
        BaseMapper<T> mapper = getMapper(clazz);
        T t = mapper.selectById(id);
        for (String arg : args) {
            setArg(t, arg, ((Integer) getArg(t, arg)) + 1); // 自增
        }
        mapper.updateById(t);
        return true;
    }


    /**
     * 自减函数(对应字段数据类型必须为Integer)
     */
    public <T> boolean decrement(String id, Class<T> clazz, Boolean async, String... args) {
        //更新redis
        String h = getName(clazz) + id;
        String jsonStr = redisTemplate.opsForValue().get(h);
        if (jsonStr != null) { // 缓存格式匹配且存在缓存
            T t = JSONObject.parseObject(jsonStr, clazz);
            for (String arg : args) { // 依次修改变量值
                setArg(t, arg, ((Integer) getArg(t, arg)) - 1); // 自减
            }
            jsonStr = JSONObject.toJSONString(t);
            redisTemplate.opsForValue().set(h, jsonStr, getCacheTime(), TimeUnit.SECONDS); // 重新写入redis

        }
        if (async) {
            asyncDecrementMysql(id, clazz, args);
        } else {
            decrementMysql(id, clazz, args);
        }

        //异步更新数据库
//        IncrementData<T> data = new IncrementData(id, clazz, args);
//        ServiceData serviceData = new ServiceData(ServiceData.INCREMENT, data, clazz.getName());
//        String dataStr = JSONObject.toJSONString(serviceData);
//        kafkaTemplate.send("service", getName(clazz), dataStr); // 异步更新数据库
        return true;
    }

    /**
     * 异步修改数据库进行自减
     */
    @Async
    <T> boolean asyncDecrementMysql(String id, Class<T> clazz, String... args) {
        BaseMapper<T> mapper = getMapper(clazz);
        T t = mapper.selectById(id);
        for (String arg : args) {
            setArg(t, arg, ((Integer) getArg(t, arg)) - 1); // 自减
        }
        mapper.updateById(t);
        log.info("异步写入数据成功");
        return true;
    }

    /**
     * 同步自减
     */
    private <T> boolean decrementMysql(String id, Class<T> clazz, String... args) {
        BaseMapper<T> mapper = getMapper(clazz);
        T t = mapper.selectById(id);
        for (String arg : args) {
            setArg(t, arg, ((Integer) getArg(t, arg)) - 1); // 自减
        }
        mapper.updateById(t);
        return true;
    }



    /**
     * 注册刷新任务
     * <p>
     * 用于定时刷新首页数据
     */
    public <T> boolean registerTask(Class<T> clazz) {
        Task<T> task = new Task(this.threadPool, this.jdbcTemplate, clazz);
        this.threadPool.schedule(task, 1, TimeUnit.SECONDS); // 1s后启动
        return true;
    }


    /**
     * 查询数据(查询统一从数据库获取，不做缓存）
     * and连接
     * 结合具体业务
     */
    public <T> List<T> search(Map<String, Object> condition, Class<T> clazz) {
        try {
            BaseMapper<T> mapper = getMapper(clazz);
            QueryWrapper<T> wrapper = new QueryWrapper<>();
            if (clazz == null || condition == null) { // 参数校验
                return null;
            }
            if (condition.isEmpty()) { // 查询所有
                mapper.selectList(wrapper);
            } else {// 条件搜索
                Field[] fields = clazz.getDeclaredFields();
                List<Field> fieldList = Arrays.asList(fields); // 域变量集合
                List<String> fieldNames = fieldList.stream().map(Field::getName).collect(Collectors.toList()); // 域变量名称集合
                Set<String> set = new HashSet<>(condition.keySet()); // 拷贝
                for (String arg : set) {
                    if (!fieldNames.contains(arg) && !arg.equals("order") && !arg.equals("limit")) { // 删除掉没有域变量对应的条件
                        condition.remove(arg);
                    }
                }
                for (String arg : condition.keySet()) { // 遍历过滤后的条件
                    String ad = "";
                    if (arg.equals("order")) {
                        String[] str = String.valueOf(condition.get(arg)).split(" ");
                        int i = 0;
                        while (i < str.length) {
                            if (fieldNames.indexOf(str[i]) > 0) { // 有效域变量
                                if (i + 1 >= str.length) {
                                    log.info("参数个数错误,默认升序");
                                    ad = "asc";
                                } else {
                                    ad = str[i + 1];
                                }
                                if (ad.equals("asc")) {
                                    wrapper.orderByAsc(camel2under(str[i]));
                                } else if (ad.equals("desc")) {
                                    wrapper.orderByDesc(camel2under(str[i]));
                                } else {
                                    log.info("order参数有误");
                                }
                            } else {
                                log.info("排序参数字段不存在");
                            }
                            i += 2;
                        }
                    } else if (arg.equals("limit")) {
                        String limit = String.valueOf(condition.get(arg)).replace(" ", ",");
                        wrapper.last("limit " + limit);
                    } else {
                        Object value = condition.get(arg); // 条件要求
                        Field field = clazz.getDeclaredField(arg);
                        String fieldTypeName = field.getType().getTypeName();
                        switch (fieldTypeName) { // 不考虑Float，Short，Character
                            case "java.lang.Boolean": { // 判断是否值相同
                                Boolean finalValue = Boolean.parseBoolean(value.toString());
                                wrapper.eq(camel2under(arg), finalValue);
                                break;
                            }
                            case "java.lang.Integer": { // 先判断为单值匹配还是区间匹配
                                if (String.valueOf(value).matches("\\d")) { // 单值匹配
                                    Object finalValue = Integer.parseInt(value.toString());
                                    wrapper.eq(camel2under(arg), finalValue);
                                } else { // 区间匹配格式 '1#2' 用#隔开[1,2]
                                    String finalValue = String.valueOf(value);
                                    String[] se = finalValue.split("\\s");
                                    Integer s = Integer.parseInt(se[0]);
                                    Integer e = Integer.parseInt(se[1]);
                                    if (s < e) {
                                        wrapper.ge(camel2under(arg), s).le(camel2under(arg), e);
                                    } else {
                                        wrapper.ge(camel2under(arg), e).le(camel2under(arg), s);
                                    }
                                }
                                break;
                            }
                            case "java.lang.Long": {
                                if (String.valueOf(value).matches("\\d")) { // 单值匹配
                                    Object finalValue = Long.parseLong(value.toString());
                                    wrapper.eq(camel2under(arg), finalValue);
                                } else { // 区间匹配格式 '1#2' 用#隔开
                                    String finalValue = String.valueOf(value);
                                    String[] se = finalValue.split("\\s");
                                    Long s = Long.parseLong(se[0]);
                                    Long e = Long.parseLong(se[1]);
                                    if (s < e) {
                                        wrapper.ge(camel2under(arg), s).le(camel2under(arg), e);
                                    } else {
                                        wrapper.ge(camel2under(arg), e).le(camel2under(arg), s);
                                    }
                                }
                                break;
                            }
                            case "java.lang.Double": {
                                if (String.valueOf(value).matches("\\d")) { // 单值匹配
                                    Object finalValue = Double.parseDouble(value.toString());
                                    wrapper.eq(camel2under(arg), finalValue);
                                } else { // 区间匹配格式 '1#2' 用#隔开
                                    String finalValue = String.valueOf(value);
                                    String[] se = finalValue.split("\\s");
                                    Double s = Double.parseDouble(se[0]);
                                    Double e = Double.parseDouble(se[1]);
                                    if (s < e) {
                                        wrapper.ge(camel2under(arg), s).le(camel2under(arg), e);
                                    } else {
                                        wrapper.ge(camel2under(arg), e).le(camel2under(arg), s);
                                    }
                                }
                                break;
                            }
                            case "java.lang.String": { // 判断是否为时间（时间则区间对比，字符串则关键字匹配）
                                String finalValue = String.valueOf(value);
                                if (arg.endsWith("Time")) { // 时间
                                    String[] se = finalValue.split("#");
                                    Date s = TimeUtil.parse(se[0]); // 开始时间
                                    Date e = TimeUtil.parse(se[1]); // 结束时间
                                    wrapper.between(camel2under(arg), s, e);
                                } else {
                                    wrapper.like(camel2under(arg), "%" + finalValue + "%");
                                }
                                break;
                            }
                        }
                    }

                }
                return mapper.selectList(wrapper);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    class Task<T> implements Runnable {
        ScheduledExecutorService service;
        JdbcTemplate jdbcTemplate;
        Class<T> clazz;

        Task(ScheduledExecutorService service, JdbcTemplate jdbcTemplate, Class<T> clazz) {
            this.service = service;
            this.jdbcTemplate = jdbcTemplate;
            this.clazz = clazz;
        }

        @Override
        public void run() {
            int cnt = 0;
            while (!refreshCacheId(clazz) && cnt < refreshRetryTimes) { // 执行refreshRetryTimes次重试
                cnt++;
            }
            this.service.schedule(this, taskFlushInterval, TimeUnit.MINUTES); // 准备下一次刷新
            log.info(clazz.getSimpleName() + "已完成刷新【" + TimeUtil.getCurrentTime() + "】");
        }
    }

    /**
     * 刷新缓存列表
     * (用于计时器定期更新缓存数据)
     */
    public <T> boolean refreshCacheId(Class<T> clazz) {
        try {
            String h = getName(clazz);
            String xxx_id = h + "_id";
            String t_xxx = "t_" + h;
            String sql = "select " + xxx_id + " from " + t_xxx + " where deleted = 0 order by  update_time desc limit 50";
            List<String> list = jdbcTemplate.queryForList(sql, String.class);
            redisTemplate.delete(h);
            if (list == null || list.size() == 0) {
                return true;
            }
            redisTemplate.opsForList().leftPushAll(h, list);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 加载数据【新】[left,right]【旧】
     * <p>
     * 懒加载则传递已有数据量到后端，
     * 如果前端数据量+10小于redisId缓存量，
     * 则直接从缓存中获取，
     * 如果前端数据量+10大于
     * 则从数据库中获取新的数据凑到10条数据，
     * 并加入缓存，设置过期时间，也写入id缓存
     *
     * @param num   前端已有的数据量
     * @param clazz 要加载的数据类型
     */
    public <T> List<T> loadData(int num, Class<T> clazz) {
        String h = getName(clazz);
        Long size = redisTemplate.opsForList().size(h);
        List<String> idList = redisTemplate.opsForList().range(h, 0, size - 1); // 获取对应的id列表
        List<T> ret = new ArrayList<>();
        List<String> getIdList = new ArrayList<>(); // 存放要从数据中获取数据的id
        if (num + 10 < size) { //如果前端数据量+10小于redis缓存量，
            for (int i = num; i < num + 10; i++) {
                String id = idList.get(i); // 要返回的数据对应的id
                if (hasCache(id, clazz)) { // 如果存在缓存，则直接获取数据
                    ret.add(JSONObject.parseObject(redisTemplate.opsForValue().get(h + id), clazz));
                } else { //不存在缓存，则从数据库中获取（先添加到列表中，再一次获取）
                    getIdList.add(id);
                }
            }
            if (getIdList.size() > 0) { // 需要从数据库添加数据
                List<T> datas = getDataByIds(getIdList, clazz);
                for (int i = 0; i < datas.size(); i++) { // 从后面依次添加，保证时间顺序
                    String json = JSONObject.toJSONString(datas.get(i));
                    String id = String.valueOf(getArg(datas.get(i), getName(clazz) + "Id"));
                    redisTemplate.opsForValue().set(h + id, json, getCacheTime(), TimeUnit.SECONDS);
                    ret.add(datas.get(i));
                }
            }
            return ret;
        } else if (num + 10 > size) { //如果前端数据量+10大于redis缓存量，
            /**
             * 考虑特殊情况，后端已经刷新了数据，但是前端用户一直懒加载获取数据，导致数据刷新后缓存数小于前端缓存数据
             * 排除误差：前端缓存数据不可能大于后端缓存，所以不存在误差
             * */
            if (num > size) { // 后端数据已刷新前端仍选择懒加载,直接从数据库获取10条数据并返回，不做缓存
                List<T> otherData = getOtherData(num, 10, clazz);
                ret.addAll(otherData);
                return ret;
            } else { //正常情况（凑齐10条数据，并添加缓存）
                int cnt = (int) (num + 10 - size); // 需要从数据库中获取的数据
                for (int i = num; i < size; i++) {
                    String id = idList.get(i); // 要返回的数据对应的id
                    if (hasCache(id, clazz)) { // 如果存在缓存，则直接获取数据
                        ret.add(JSONObject.parseObject(redisTemplate.opsForValue().get(h + id), clazz));
                    } else { //不存在缓存，则从数据库中获取（先添加到列表中，再一次获取）
                        getIdList.add(id);
                    }
                }
                if (getIdList.size() > 0) { // 需要从数据库添加数据
                    List<T> datas = getDataByIds(getIdList, clazz);
                    for (int i = 0; i < datas.size(); i++) { // 从后面依次添加，保证时间顺序
                        String json = JSONObject.toJSONString(datas.get(i));
                        String id = String.valueOf(getArg(datas.get(i), getName(clazz) + "Id"));
                        redisTemplate.opsForValue().set(h + id, json, getCacheTime(), TimeUnit.SECONDS);
                        ret.add(datas.get(i));
                    }
                }
                List<T> list = getOtherData(num, cnt, clazz); // 直接从数据库中获取的数据
                for (int i = 0; i < list.size(); i++) {
                    String id = String.valueOf(getArg(list.get(i), getName(clazz) + "Id")); // 获取id
                    if (!idList.contains(id)) { // 没有并发原因导致的冲突
                        String json = JSONObject.toJSONString(list.get(i));
                        redisTemplate.opsForValue().set(h + getArg(list.get(i), getName(clazz) + "Id"), json, getCacheTime(), TimeUnit.SECONDS);
                        redisTemplate.opsForList().rightPush(h, id);
                        ret.add(list.get(i));
                    }
                }
                return ret;
            }
        }
        return null;
    }

    /**
     * 获取额外的数据
     */
    private <T> List<T> getOtherData(int start, int end, Class<T> clazz) {
        String limit = "limit " + start + " , " + end;
        BaseMapper<T> baseMapper = getMapper(clazz);
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("update_time");
        wrapper.last(limit);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据id列表获取对应的数据集合
     */
    private <T> List<T> getDataByIds(List<String> ids, Class<T> clazz) {
        BaseMapper<T> baseMapper = getMapper(clazz);
        String primaryKey = getName(clazz) + "_id";
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        Consumer<QueryWrapper<T>> consumer = wrap -> {
            for (int i = 0; i < ids.size() - 1; i++) {
                wrap = wrap.eq(primaryKey, ids.get(i)).or();
            }
            wrap.eq(primaryKey, ids.get(ids.size() - 1));
        };
        wrapper.eq("deleted", false).and(consumer);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 判断对应数据是否存在缓存
     */
    public <T> boolean hasCache(String id, Class<T> clazz) {
        String h = getName(clazz);
        String k = h + id;
        return redisTemplate.opsForValue().get(k) != null;
    }


    /**
     * 查询数据（单个）
     */
    public <T> Object search(String id, Class<T> clazz) {
        try {
            String h = getName(clazz);
            // 校验id
            if (h.equals("job") || h.equals("product")){
                if (!bloomFilterService.bloomContain(h+id)) {
                    return null;
                }
            }
            String cache = redisTemplate.opsForValue().get(h + id);
            if (cache != null && cache.length() > 0) { // 如果存在对应缓存
                Object ret = JSONObject.parseObject(redisTemplate.opsForValue().get(h + id), clazz);
                return ret;
            } else { // 没有缓存
                boolean b = tryLock(h, id); // true则表示获取锁成功
                while (!b) { // 没有得到互斥锁
                    b = tryLock(h, id);
                }
                return selectMySqlForCache(id, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 异步写入数据库
     */
    @Async
    public <T> boolean asyInsert(T t) {
        try {
            insertMySql(t);
            log.info("异步写入数据库成功");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
//        ServiceData serviceData = new ServiceData(ServiceData.INSERT, t, t.getClass().getName());
//        String data = JSONObject.toJSONString(serviceData);
//        kafkaTemplate.send("service", getName(t, ""), data); // 异步更新数据库
        return true;
    }

    /**
     * 插入数据(先写入数据库，再写入缓存）
     *
     * @return id
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
            redisTemplate.opsForList().leftPush(h, id); // 添加到id列表缓存
            redisTemplate.opsForValue().set(key, json, getCacheTime(), TimeUnit.SECONDS); // 写入redis
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新数据（不走kafka，后面再看看需不需要修改）
     */
    public <T> boolean update(T t) {
        try {
            // 删除redis
            String id = String.valueOf(getArg(t, getName(t, "Id")));
            String h = getName(t, id);
            redisTemplate.delete(h);// 删除redis缓存
            updateMySql(t);
            redisTemplate.delete(getName(t, id)); // 二次删除redis缓存
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


//    /**
//     * 更新数据（kafka老是失灵)
//     */
//    public <T> boolean update(T t) {
//        try {
//            // 删除redis
//            String id = String.valueOf(getArg(t, getName(t, "Id")));
//            ServiceData serviceData = new ServiceData(ServiceData.UPDATE, t, t.getClass().getName(), id);
//            String data = JSONObject.toJSONString(serviceData);
//            redisTemplate.delete(getName(t, id));// 删除redis缓存
//            redisTemplate.opsForList().remove(getName(t, ""), 1, id); // 删除id缓存
//            kafkaTemplate.send("service", getName(t, ""), data); // 异步更新数据库
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }


    /**
     * 删除数据
     */
    public <T> boolean delete(T t) {
        try {
            String methodName = "get" + t.getClass().getSimpleName() + "Id";
            Method method = t.getClass().getMethod(methodName);
            String id = String.valueOf(method.invoke(t));
            if (deleteMySql(t.getClass(), id)) {
                if (redisTemplate.opsForValue().get(getName(t, id)) != null) {
                    redisTemplate.delete(getName(t, id));
                }
                redisTemplate.opsForList().remove(getName(t, ""), 1, id); // 删除id缓存
            } else { // 删除失败
                return false;
            }
//            ServiceData serviceData = new ServiceData(ServiceData.DELETE, t, t.getClass().getName(), id);
//            String data = JSONObject.toJSONString(serviceData);

//            kafkaTemplate.send("service", getName(t, ""), data); // 异步更新数据库
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除数据
     */
    public <T> boolean delete(String id, Class<T> clazz) {
        try {
            deleteMySql(clazz, id);
            if (redisTemplate.opsForValue().get(getName(clazz) + id) != null) {
                redisTemplate.delete(getName(clazz) + id);
            }
            redisTemplate.opsForList().remove(getName(clazz), 1, id); // 删除id列表缓存
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 根据id查询数据
     * 补充上查询图片（前提：clazz类对象包含photos/images 字段 ，且该字段类型为List<Sring>
     */
    public <T> Object selectMySql(String id, Class<T> clazz) {
        try {
            BaseMapper<T> mapper = getMapper(clazz);
            Object ret = mapper.selectById(id); // 获取数据
            try {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                final List<Method> methodList = Arrays.stream(declaredMethods).filter(m -> m.getName().equals("setImages")).collect(Collectors.toList());
                if (methodList.size() > 0) {
                    Method setImages = clazz.getDeclaredMethod("setImages", List.class);
                    List list = getImage(id, clazz);
                    setImages.invoke(ret, list); // 设置图片
                } else {
                    log.info("该实体类不包含images属性，不进行图片装配");
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.info("图片获取异常");
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据id查询数据
     */
    private <T> Object selectMySqlForCache(String id, Class<T> clazz) {
        try {
            BaseMapper<T> mapper = getMapper(clazz);
            String h = getName(clazz);
            Object ret = mapper.selectById(id); // 获取数据
            String value = JSONObject.toJSONString(ret);
            redisTemplate.opsForValue().set(h + id, value, getCacheTime(), TimeUnit.SECONDS); // 设置缓存
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
            BaseMapper<T> mapper = getMapper(t);
            mapper.insert(t);
            try {
                Method method = t.getClass().getMethod("getImages");
                List<String> list = (List<String>) method.invoke(t);
                insertImage(list, String.valueOf(getArg(t, getName(t, "Id"))), t.getClass());
            } catch (Exception e) {
                e.printStackTrace();
                log.info("图片插入异常");
            }
            log.info("insertMySql is ok");
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
            setArg(t, "updateTime", TimeUtil.getCurrentTime());
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
    private void update(Object t, String id) {
        // 删除mysql中的数据
        int flag = 2;
        while (0 < flag && flag <= maxRepetitions) { // flag 在 ( 0 , maxRepetitions ] 区间循环，每次错误就flag++
            flag = updateMySql(t) ? 0 : flag + 1;
        }
        if (flag == 0) { // 更新成功
            // 双删
            redisTemplate.delete(getName(t, id)); // 二次删除redis缓存
            redisTemplate.opsForList().remove(getName(t, ""), 1, id); // 删除id缓存
        }
    }

    /**
     * 逻辑删除(Mysql)
     */
    public <T> boolean deleteMySql(Class<T> clazz, String id) {
        try {
            BaseMapper<T> mapper = getMapper(clazz);
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

    private <T> BaseMapper<T> getMapper(Class<T> clazz) { // 获取mapper对象
        return (BaseMapper<T>) SpringContextUtil.getBean(getName(clazz) + "Dao");
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
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, key, Duration.ofSeconds(10))); // 超时时间为10s
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
            Method method;
            if (value instanceof List) { // ArrayList 特殊处理
                method = t.getClass().getMethod(methodName, List.class);
            } else {
                method = t.getClass().getMethod(methodName, value.getClass());
            }
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
        int time = minCacheDuration * 60 + (int) (Math.floor(ran * (maxCacheDuration - minCacheDuration)) * 60);
        return time;
    }

    /**
     * 获取redis中某个类的缓存列表
     */
    private <T> List getCacheList(Class<T> cls) {
        List<T> ret = new ArrayList<>();
        String h = getName(cls);
        List<String> list = redisTemplate.opsForList().range(h, 0, -1);
        list.forEach(s -> {
            String st = redisTemplate.opsForValue().get(h + s);
            if (st != null && st.length() > 0) { // 存在缓存
                ret.add(JSONObject.parseObject(st, cls));
            } else { // 不存在缓存
                redisTemplate.opsForList().remove(h, 1, s); // 删除id缓存
            }
        });
        return ret;
    }

    /**
     * 驼峰命名转下划线分隔
     */
    public static String camel2under(String c) {
        String separator = "_";
        c = c.replaceAll("([a-z])([A-Z])", "$1" + separator + "$2").toLowerCase();
        return c;
    }

}
