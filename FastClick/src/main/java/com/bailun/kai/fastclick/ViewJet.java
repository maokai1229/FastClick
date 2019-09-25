package com.bailun.kai.fastclick;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.bailun.kai.aptlib.FastProxyInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author : kai.mao
 * @date :  2019/9/17
 */
public class ViewJet {

    private static final Map<Class<?>, ICodeInjector<Object>> injectorMap = new LinkedHashMap<Class<?>, ICodeInjector<Object>>();



    private static final long DEFAULT_INTERVAL_TIME = 1000;



    public static void init(Activity activity,long intervalTime){
        ICodeInjector<Object> iCodeInjector = findInjector(activity);
        iCodeInjector.inject(ViewFinder.ACTIVITY,activity,activity);
        iCodeInjector.setInteralTime(intervalTime);

    }

    public static void init(View view, long intervalTime){
        ICodeInjector<Object> iCodeInjector = findInjector(view);
        iCodeInjector.inject(ViewFinder.VIEW,view,view);
        iCodeInjector.setInteralTime(intervalTime);
    }


    public static void init(Activity activity){
        ICodeInjector<Object> iCodeInjector = findInjector(activity);
        iCodeInjector.inject(ViewFinder.ACTIVITY,activity,activity);
        iCodeInjector.setInteralTime(DEFAULT_INTERVAL_TIME);
    }




    private static ICodeInjector<Object> findInjector(Object activity) {
        Class<?> clazz = activity.getClass();
        ICodeInjector<Object> injector = injectorMap.get(clazz);
        if (injector == null) {
            try {
                Class injectorClazz = Class.forName(clazz.getName() + FastProxyInfo.LINK
                        + FastProxyInfo.VIEW_JET);
                injector = (ICodeInjector<Object>) injectorClazz
                        .newInstance();
                injectorMap.put(clazz, injector);
            } catch (Exception e) {
                Log.e("檢查",e.getMessage());
                e.printStackTrace();
            }
        }
        return injector;
    }

}
