package com.bailun.kai.fastclick;

import android.app.Activity;
import android.view.View;
/**
 * 实现 findViewById
 * @author : kai.mao
 * @date :  2019/9/18
 */
public enum ViewFinder {

    /**
     * 自定义 View 中使用
     */
    VIEW {
        @Override
        public View findViewById(Object source, int id) {
            return ((View)source).findViewById(id);
        }
    },


    /**
     * Activity / Fragment 中使用
     */
    ACTIVITY{
        @Override
        public View findViewById(Object source, int id) {
            return ((Activity)source).findViewById(id);
        }
    };

    public abstract View findViewById(Object source, int id);

}
