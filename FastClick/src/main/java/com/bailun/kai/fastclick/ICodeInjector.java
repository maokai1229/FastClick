package com.bailun.kai.fastclick;

/**
 * 抽象代码绑定器
 * @author : kai.mao
 * @date :  2019/9/18
 */
public interface ICodeInjector<T> {


    void inject(ViewFinder viewFinder,T target,Object source);


    void setInteralTime(long time);

}
