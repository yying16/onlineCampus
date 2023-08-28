package com.campus.recommend.service;

import com.campus.recommend.domain.T;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.core.*;
import weka.core.converters.ConverterUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 随机森林预测模型
 * <p>
 * 每当新产品发布时，会获取用户特征和产品特征进行预测
 * 如果满意度达到阈值，则进行实时推荐
 * <p>
 * 每隔10分钟进行模型训练，只有新模型的准确性大于旧模型才进行更替，否则每隔5分钟重新训练，
 * 直到有新模型能够替代，或者加速训练次数超过10次，则按照新规则进行更替（保证模型数据的更替）
 * 新规则下允许模型准确率的降低，但必须满足一定的公式（待定）
 */
@Service
@Slf4j
public class RandomForestPrediction {

    /**
     * 从数据库中获取数据（jdbc）
     * */
//    public List getJobData(){
//
//    }


    /**
     * 转化数据集
     * <p>
     * 从数据库中获取数据列表,将其转化为
     */
    public static <T> Instances convertedData(List<T> data) {
        Field[] declaredFields = data.get(0).getClass().getDeclaredFields();
        List<String> features = Arrays.stream(declaredFields).map(Field::getName).collect(Collectors.toList());
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            if(i==0){
                Attribute attribute = new Attribute(features.get(i),true);
                attribute.setStringValue("value1");
                attribute.setStringValue("value2");
                attribute.setStringValue("value3");
                attribute.setStringValue("value4");
                attribute.setStringValue("value5");
                attributes.add(attribute);
            }else{
                Attribute attribute = new Attribute(features.get(i));
                attributes.add(attribute);
            }
        }
        Instances dataset = new Instances("dataset", attributes, 0);
        dataset.setClass(attributes.get(0));
        for (T datum : data) { // 遍历每一个元组
            Instance instance = new DenseInstance(dataset.numAttributes());
            instance.setDataset(dataset);
            for (Attribute attribute : attributes) {
                Object arg = getArg(datum, attribute.name());
                if (arg instanceof Double) {
                    instance.setValue(attribute, Double.parseDouble(String.valueOf(arg)));
                } else if (arg instanceof String) {
                    instance.setValue(attribute, String.valueOf(arg));
                } else {
                    System.out.println("数据格式不为Double");
                }
            }
            dataset.add(instance);
        }
        return dataset;
    }

    public static void main(String[] args) {
        List<T> list = new ArrayList<>();
        list.add(new T("value1",1.0,1.0));
        list.add(new T("value2",2.0,1.0));
        list.add(new T("value3",3.0,1.0));
        list.add(new T("value4",4.0,1.0));
        list.add(new T("value5", 5.0, 1.0));
//        list.add(new T(1.0,1.0,1.0));
//        list.add(new T(1.0,1.0,1.0));
//        list.add(new T(1.0,1.0,1.0));
//        list.add(new T(1.0,1.0,1.0));
        System.out.println(convertedData(list));
    }


    /**
     * 模型训练
     */
    public void trainingModel() {
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource("dataset.arff");
            Instances dataset = source.getDataSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static <T> Object getArg(T t, String argName) {
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

}
