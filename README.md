# wabei-wallet-android

#### 项目介绍
瓦贝手机钱包Android版

#### 软件架构
1. kotlin 语言开发
2. 使用dbflow数据库
3. 参考mvp设计，所有的presenter都是静态类，presenter返回值均通过参数中的listener完成
4. 异步执行采用kotlin协程技术

#### 安装教程

1. 使用Android Studio 3.1.3开发
2. gradle 插件使用3.1.3
3. kotlin-gradle-plugin使用1.2.51

#### 使用说明

使用Android Studio打开项目后，不能直接编译成功，需要在local.properties文件中定义以下属性
1. keyAlias=xxx
1. keyPassword=xxx
1. storeFile=xxx
1. storePassword=xxx

如果不需要签名操作，请删除`app/build.gradle`文件中的相关签名内容

**需要注意gradle.properties中的代理配置，导入工程时，如果发现无法下载相关依赖包，请删除这个文件中的代理设置**


1.如何对接智能合约？
创建Contract对象，创建对象需要拥有智能合约的一个编译后的值abi文件，合约地址，等