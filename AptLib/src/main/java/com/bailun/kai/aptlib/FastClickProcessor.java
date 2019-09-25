package com.bailun.kai.aptlib;

import com.google.auto.service.AutoService;


import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * 自定义注解处理器
 * 用於编译阶段自动生成代码
 * @author : kai.mao
 * @date :  2019/9/17
 */

@AutoService(Processor.class)
public class FastClickProcessor extends AbstractProcessor {

    private Elements mElementUtils;
    private Messager mMessage;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnvironment.getElementUtils();
        mMessage = processingEnvironment.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //支持的java版本
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //支持的注解
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(FastClick.class.getCanonicalName());
        return annotations;
    }



    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<String, FastProxyInfo> fastProxyInfoMap;
        // 扫描所有包含该注解的 element 元素
        fastProxyInfoMap = scanSourceCodeFile(roundEnvironment);

        //  生成代码
        generateFastProxyCode(fastProxyInfoMap);

        return true;
    }

    /**
     * 生成
     * @param fastProxyInfoMap
     */
    private void generateFastProxyCode(Map<String, FastProxyInfo> fastProxyInfoMap) {

        // 遍历map,生成代码
        for (String mapKey : fastProxyInfoMap.keySet()){
            FastProxyInfo proxyInfo = fastProxyInfoMap.get(mapKey);
            writeCode(proxyInfo);
        }

    }

    private  Map<String, FastProxyInfo>  scanSourceCodeFile(RoundEnvironment roundEnvironment) {
        Map<String, FastProxyInfo> fastProxyInfoMap = new HashMap<>();
        for (Element annotatedElement  :roundEnvironment.getElementsAnnotatedWith(FastClick.class)){
            ExecutableElement executableElement = (ExecutableElement) annotatedElement;
            TypeElement classElement = (TypeElement) annotatedElement
                    .getEnclosingElement();

            PackageElement packageElement = mElementUtils.getPackageOf(classElement);

            // 获取 SourceCode java 文件信息
            String fullClassName = classElement.getQualifiedName().toString();
            String className = classElement.getSimpleName().toString();
            String packageName = packageElement.getQualifiedName().toString();
            String methodName = executableElement.getSimpleName().toString();
            int viewId = executableElement.getAnnotation(FastClick.class).value();


            print("fullClassName: "+ fullClassName +
                    ",  methodName: "+methodName +
                    ",  viewId: "+viewId);


            FastMethod fastMethod = new FastMethod(viewId,methodName,getMethodParameterTypes(executableElement));

            FastProxyInfo fastProxyInfo = fastProxyInfoMap.get(fullClassName);

            // 保存信息
            if (fastProxyInfo != null){
                fastProxyInfo.addMethod(fastMethod);
            }else {
                FastProxyInfo currentProxyInfo = new FastProxyInfo(className,packageName);
                currentProxyInfo.setTypeElement(classElement);
                currentProxyInfo.addMethod(fastMethod);
                fastProxyInfoMap.put(fullClassName,currentProxyInfo);
            }
        }

        return fastProxyInfoMap;
    }


    /**
     * 取得方法参数类型列表
     */
    private List<String> getMethodParameterTypes(ExecutableElement executableElement) {
        List<? extends VariableElement> methodParameters = executableElement.getParameters();
        if (methodParameters.size()==0){
            return null;
        }
        List<String> types = new ArrayList<>();
        for (VariableElement variableElement : methodParameters) {
            TypeMirror methodParameterType = variableElement.asType();
            if (methodParameterType instanceof TypeVariable) {
                TypeVariable typeVariable = (TypeVariable) methodParameterType;
                methodParameterType = typeVariable.getUpperBound();
            }
            types.add(methodParameterType.toString());
        }
        return types;
    }


    private void writeCode(FastProxyInfo fastProxyInfo) {
        try {
            JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                    fastProxyInfo.getProxyClassFullName(),
                    fastProxyInfo.getTypeElement());
            Writer writer = jfo.openWriter();
            writer.write(fastProxyInfo.generateJavaCode());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            error(fastProxyInfo.getTypeElement(),
                    "Unable to write injector for type %s: %s",
                    fastProxyInfo.getTypeElement(), e.getMessage());
        } catch (FastClickException e){
            error(fastProxyInfo.getTypeElement(),
                    "The use of irregular %s: %s",
                    fastProxyInfo.getTypeElement(), e.getMessage());
        }
    }

    private void print(String message) {
        mMessage.printMessage(Diagnostic.Kind.NOTE, message);
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        mMessage.printMessage(Diagnostic.Kind.ERROR, message, element);
    }



}
