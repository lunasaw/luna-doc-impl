# luna-doc-impl

[![GitHub license](https://img.shields.io/badge/MIT_License-blue.svg)](https://raw.githubusercontent.com/lunasaw/luna-mt-doc/master/LICENSE)

[www.isluna.ml](http://www.isluna.ml) 

美团文档技术实现案例

| 案例           | 文章                                                         | 实现模块                                                |
| -------------- | ------------------------------------------------------------ | ------------------------------------------------------- |
| 优雅的日志打印 | [文章路径](https://tech.meituan.com/2021/09/16/operational-logbook.html) | https://lunasaw.github.io/luna-doc-impl/gracefully-log/ |

### 注意点

#### spel表达式

SpEL支持使用@符号来引用Bean，在引用Bean时需要使用BeanResolver接口实现来查找Bean，Spring提供BeanFactoryResolver实现。eg.`io.github.lunasaw.log.controller.UserController`

1. spring bean 引用

   `@userController.getUserStr(#req) `

2. 属性引用

   `#req.userName`

3. Root目标类引用

   `#root?.#root.req2do(#req)` 其中 `?.`是安全处理。不为null则执行

4. 访问目标实例方法引用

   `getUserStr(#req)` 这里的`getUserStr`方法为实例方法，同调用的接口同一个对象内

5. 自定义静态方法引用

   `context.registerFunction("value", parseInt);` 手动注入

6. 静态方法引用

   Type必须是类全限定名，java.lang包除外 `(T(io.github.lunasaw.log.controller.UserController).req2do(#req))`

具体可见，有详细分级例子[参考链接](https://blog.csdn.net/JokerLJG/article/details/124434854?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-124434854-blog-107700386.235%5Ev32%5Epc_relevant_default_base3&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-124434854-blog-107700386.235%5Ev32%5Epc_relevant_default_base3&utm_relevant_index=2)

#### aop处理

##### 接口日志

`io.github.lunasaw.log.aspect.LpLogAspect` 打印接口的入参和出参

##### 操作日志

`io.github.lunasaw.log.aspect.LogRecordAspect` 打印用户操作日志，大多为上层调用
