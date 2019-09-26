# FastClick
使用 Apt 动态生成代码,实现按钮的点击控制(防止 App 在弱网下快速点击引起的未知 bug)

### 远程依赖

在模块的 build.gradle 文件中设置如下

`defaultConfig {`

```
javaCompileOptions {
    annotationProcessorOptions {
        includeCompileClasspath true
    }
}
```

```
repositories {
    // 代码已提交 JCenter 审核,需添加 maven 仓库地址
    maven{ url 'https://dl.bintray.com/ethanmao/FastClick'
    }
}
```

```
dependencies {
    implementation 'com.bailun.kai:FastClick:1.1'
    }
```





### 相关链接
[仿照 ButterKnife 的 Android 注解实例](https://www.cnblogs.com/huansky/p/9544640.html)

[Android 编译时注解-提升](https://juejin.im/post/587d81295c497d0058b17a16)

[Android编译时注解APT实战](https://www.jianshu.com/p/07ef8ba80562)

[详细介绍编译时注解的使用方法](https://juejin.im/entry/57ad3fa47db2a200540c925)

### APT 原理
APT 全名 Annation Processor Tool,就是借助 Javax 的注解库,在编译阶段扫描代码,将自定义注解的元素传入到注解处理器的 process() 方法中,然后生成我们想要的代码.比如生成 Java 文件.

生成代码原理很简单,就是用一个 StringBuilder 拼接字符串,然后通过工具类生成 Java 文件

### 构建过程分析
1. 构建工程,划份代码生成模块/Apt 调用模块
2. 继承 AbstarctProcessor ,重写 process 方法
3. Build 代码生成模块打 jar 包
3. Apt 调用模块导入该 jar 包
4. Apt 调用模块将生成的代码注入源代码


### 关于 Apt 的几个问题
* 为什么 ButterKnife 注解方法的修饰符不能是 private?

之前我也遇到过这一问题,在接触了 Apt 后迎刃而解,这是因为 Apt 技术会在同一个包中自动生成代码,并且在使用时注入到源代码中,在同一个包, private 修饰的方法对 apt 来说是不可见的.


 * Apt 生成的代码在什么时候介入?

ButterKnife 以及我们今天要手撸的 FastClick,它们都是跟随 Activity/View 初始化,在 setContentView 方法被执行后,我们会通过 FastClick.init() 方法,在该方法将Apt生成的方法注入源代码.

* 为什么需要单独创建一个 Java 库模块?

这是因为 Apt 相关的代码被放在一个叫做 Javax 的库中,而我们的 Android 库无法使用到 Javax 包,除非去网上下载一个 jar 文件作为我们的库文件.所以我们需要单独创建,刚好将模块功能隔离开,该模块只负责使用 Apt 生成代码.


* 使用 Apt 和使用反射有什么区别?

Apt 是基于编译时注解,
之前我们使用反射加注解是基于运行时注解,但是这种方式会在运行阶段做耗时的遍历操作,因此性能问题也一直被人们诟病; Apt 也需要用到反射来创建对象,但是不包含耗时操作.所以在性能上有它的优势.