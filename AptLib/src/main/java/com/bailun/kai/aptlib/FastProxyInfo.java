package com.bailun.kai.aptlib;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;

/**
 * 生成代码工具类
 * 用于存储生成代码的信息
 * @author : kai.mao
 * @date :  2019/9/17
 */
public class FastProxyInfo {

    public final static String LINK = "_";
    public final static String VIEW_JET = "ViewJet";
    private final static String NEXT_LINE = "\n";

    private String  mTargetClassName;

    private String mProxyClassName;

    private String mPackageName;

    private TypeElement mTypeElement;

    private List<FastMethod> mFastMethodList;

    public FastProxyInfo(String targetClassName, String packageName) {
        this.mTargetClassName = targetClassName;
        this.mPackageName = packageName;
        this.mProxyClassName = targetClassName+LINK+ VIEW_JET;
    }



    public void addMethod(FastMethod fastMethod){
        if (mFastMethodList == null){
            mFastMethodList = new ArrayList<>();
        }
            mFastMethodList.add(fastMethod);


    }

    /**
     * 当 List 未初始化,防止空指针
     * @return
     */
    public List<FastMethod> getFastMethodList(){
        return mFastMethodList == null ? new ArrayList<FastMethod>() : mFastMethodList;
    }

    public String getProxyClassName(){

        return mProxyClassName;

    }

    public String getProxyClassFullName(){

        return mPackageName+"."+mProxyClassName;
    }

    public String getTargetClassName(){

        return mTargetClassName;
    }

    public String getTargetClassFullName(){

        return mTargetClassName+"."+mPackageName;
    }


    public TypeElement getTypeElement(){

        return mTypeElement;
    }

    public void setTypeElement(TypeElement typeElement){

        mTypeElement = typeElement;

    }


    /**
     * 生成 Java 类
     */
    public String generateJavaCode() throws FastClickException {

        StringBuilder codeBuilder  =  new StringBuilder();
        codeBuilder.append("// 以下代码使用 APT 在编译阶段自动生成,请勿手动修改");
        codeBuilder.append(NEXT_LINE);
        // 导包

        codeBuilder.append("package ").append(mPackageName).append(";").append(NEXT_LINE);
        codeBuilder.append("import android.view.View;").append(NEXT_LINE);
        codeBuilder.append("import com.bailun.kai.fastclick.ViewFinder;").append(NEXT_LINE);
        codeBuilder.append("import android.util.Log;").append(NEXT_LINE);
        codeBuilder.append("import com.bailun.kai.fastclick.ICodeInjector;").append(NEXT_LINE);


        // 定义 Class 文件
        codeBuilder.append("public class ").append(mProxyClassName).append(" <T extends ").append(mTargetClassName).append(" > ")
                .append(" implements ICodeInjector<T> {")
                .append(NEXT_LINE)
                .append(NEXT_LINE);

        // 插入方法
        generateMethodCode(codeBuilder);


        // 定义 class 文件结尾

        codeBuilder.append("}");


        return codeBuilder.toString();

    }

    private void generateMethodCode(StringBuilder codeBuilder) throws FastClickException {

            // 实例化控件并设置点击监听
        codeBuilder.append(" public long interalTime;").append(NEXT_LINE)
        .append("@Override").append(NEXT_LINE)
        .append("public void inject(final ViewFinder viewFinder,final T target,Object source){ ").append(NEXT_LINE)
        .append("View view;").append(NEXT_LINE);
              // 遍历所有 FastClick 注解的方法
        for (FastMethod fastMethod: getFastMethodList()) {

                //1. 实例化控件
                codeBuilder.append("view = viewFinder.findViewById(source,").append(fastMethod.getId()).append(");")
                        .append(NEXT_LINE)
                         .append(" if(viewFinder == null) {")
                         .append("    Log.e(\"ViewJet\",\"viewFinder 对象为空指针\");").append(NEXT_LINE)
                         .append("}").append(NEXT_LINE)
                         .append("if(view == null) {").append(NEXT_LINE)
                         .append("Log.e(\"ViewJet\",\"view 对象为空指针\");")
                         .append("return ;").append(NEXT_LINE)
                         .append("}").append(NEXT_LINE);
                        //2. 设置监听器
                codeBuilder.append("view.setOnClickListener(new View.OnClickListener() {").append(NEXT_LINE)
                        //3. 控制点击间隔
                        .append("long time = 0L;").append(NEXT_LINE)
                        .append("  @Override").append(NEXT_LINE)
                        .append(" public void onClick(View v) {").append(NEXT_LINE)
                        .append(" long temp = System.currentTimeMillis(); ").append(NEXT_LINE)

                         .append(" if(temp - time >=  interalTime){").append(NEXT_LINE);
                        //4. 执行注解了的方法
                         if(fastMethod.getPramasSize() == 0){
                             codeBuilder.append("target.").append(fastMethod.getMethodName()).append("();").append(NEXT_LINE);
                         }else if (fastMethod.getPramasSize() == 1){
                             // 参数必须是 View 类型
                             if (fastMethod.getMethodPramas().get(0).equals("android.view.View")){
                                 codeBuilder.append("target.").append(fastMethod.getMethodName()).append("(v);").append(NEXT_LINE);
                             }else {
                                 throw new FastClickException("参数类型必须为 android.view.View");
                             }

                         }else {
                             throw new FastClickException("入参数量只能为1 或者 0");
                         }

                         codeBuilder.append("}")
                         .append(" time = temp;").append(NEXT_LINE)
                        .append("  }")
                        .append(NEXT_LINE);


        };
        codeBuilder.append("});").append(NEXT_LINE);
        codeBuilder.append("}").append(NEXT_LINE);

        // 设置间隔时间
        codeBuilder.append("@Override").append(NEXT_LINE)
        .append("public void setInteralTime(long time){").append(NEXT_LINE)
        .append("this.interalTime = time;")
        .append("}").append(NEXT_LINE);
    }

}
