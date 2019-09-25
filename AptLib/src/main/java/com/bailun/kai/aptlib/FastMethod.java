package com.bailun.kai.aptlib;

import java.util.List;

/**
 * Java 方法工具类
 * @author : kai.mao
 * @date :  2019/9/17
 */
public class FastMethod {

    private int id;
    private String methodName;
    private List<String> methodPramas;

    public FastMethod(int id, String methodName, List<String> methodPramas) {
        this.id = id;
        this.methodName = methodName;
        this.methodPramas = methodPramas;
    }

    public int getPramasSize(){
        return methodPramas == null ? 0:methodPramas.size();
    }

    public int getId() {
        return id;
    }

    public List<String> getMethodPramas() {
        return methodPramas;
    }

    public String getMethodName() {
        return methodName;
    }
}


