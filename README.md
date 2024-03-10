
## 项目介绍
木兰湾是用于管理个人消费、锻炼、音乐、阅读、健康、饮食、人生经历等各个衣食住行信息的系统，由统计、计划模块利用调度系统来统计分析执行情况。
并通过积分和评分体系来综合评估个人的总体状态，同时采用机器学习算法对数据进行预测。

系统作为一个个人综合管理系统，它主要解决三个问题：
* 我的计划(期望)是什么？--> (模块：统计、计划)
* 我要做什么？--> (模块：日历)
* 我做了什么？--> (模块：行为分析)

基于以上三个问题，我们是否可以去思考以下两个问题：
* 我是怎么样的人？（用户画像）
* 我将会怎么样？ （机器学习）-->（模块：数据预测）

新版本的木兰湾项目采用了SpringBoot3，Vue3重构设计。

该系统是前后端分离的项目，当前项目mulanbay-server3为后端API项目，只提供系统的api接口，整个系统必须要同时运行前端才能完整访问。

前端项目
* 基于Vue3的前端(PC端)[mulanbay-ui-vue3](https://gitee.com/mulanbay/mulanbay-ui-vue3)

算法端项目：
* 基于sklearn的机器学习(python)[mulanbay-sklearn](https://gitee.com/mulanbay/mulanbay-sklearn)

(对于数据预测，mulanbay-sklearn负责算法，生成pmml模型文件，java端mulanbay-server通过jpmml库加载模型文件对业务数据进行预测)

### 木兰湾项目概况

[木兰湾项目说明](https://gitee.com/mulanbay)

### 功能简介

* 基于RBAC的用户权限管理
* 支持分布式运行的调度功能
* 基于AHANLP的自然语言学习服务
* 提供消费、锻炼、音乐、阅读、健康、饮食、人生经历等常用模块
* 统一的日志管理及日志流分析
* 提供基于模板化的提醒、计划、图表、行为配置及分析
* 统一的日历管理，提供日历自动新增、完成功能
* 提供磁盘、CPU、内存的监控及报警，并可以自动恢复
* 数据库数据、备份文件自动清理
* 统一及强大的图表统计分析功能
* 基于微信公众号消息、邮件的消息提醒服务
* 基于错误代码的消息发送可配置化
* 基于Hibernate的配置化的查询便捷封装
* 提供可配置的个人积分和评分体系
* 提供多角度的用户行为分析
* 提供词云、相似度、智能问答等分析功能 
* 基于sklearn的机器学习对一些数据进行预测

### 所用技术

* 前端：Vue3、Element Plus、Echarts
* 后端：SpringBoot3、Hibernate、Quartz、NLP、Redis & Jwt

| 核心依赖                | 版本    |
| ---------------------- |-------|
| Spring Boot            | 3.2.X |
| Hibernate              | 6.X   |
| Quartz                 | 2.3.X |

### 项目结构
``` lua
mulanbay-server
├── mulanbay-ai          -- 机器学习模块，数据预测
├── mulanbay-business    -- 通用业务类
├── mulanbay-common      -- 公共模块
├── mulanbay-persistent  -- 持久层基于hibernate的封装
├── mulanbay-pms         -- 木兰湾API接口层
├── mulanbay-schedule    -- 调度模块封装
├── mulanbay-web         -- 基于SpringMVC的一些封装

```

### 项目运行与部署
参考文档(V3版本章节) [https://www.yuque.com/mulanbay/rgvt6k/uy08n4](https://www.yuque.com/mulanbay/rgvt6k/uy08n4)

### 软件要求
| 软件                    | 版本    |
| ---------------------- |-------|
| JDK                    | 17+   |
| Nginx                  | 1.17+ |
| Redis                  | 7.0+  |
| Mysql                  | 8.0+  |

备注：因为SQL语句采用了代码块等新特性，并且项目基于SpringBoot3，因此低版本的JDK不再支持。

### 硬件要求
 内存4G+
 
## 系统架构

### 系统模块
![系统模块](https://images.gitee.com/uploads/images/2020/1116/153208_763552c9_352331.png "系统模块.png")

### 系统结构
![系统结构](https://images.gitee.com/uploads/images/2020/1116/153229_0e719916_352331.png "系统结构.png")

### 业务流程
![业务流程](https://images.gitee.com/uploads/images/2020/1116/153249_202177a7_352331.png "业务流程.png")

### 图表分类
![图表分类](https://images.gitee.com/uploads/images/2020/1116/153330_e6cdf020_352331.png "图表统计.png")

## 在线演示
暂未提供

## 技术交流
* QQ群：562502224

## 参考/集成项目

木兰湾参考、集成了一些项目，有些功能自己也只是一个搬运工，先感谢大家的开源。

* 前端(PC)日历组件：[full-calendar](https://fullcalendar.io/)
* 自然语言学习：[AHANLP](https://github.com/jsksxs360/AHANLP)
* Vue3版本前端(PC)：[RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue)，[Element-Plus](https://element-plus.org/zh-CN/)
* 自动表单生成：[form-create](https://www.form-create.com/)
    
## 项目展望

## 项目截图

### 基于Vue3的PC端
<table>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2020/1116/153409_2b13c53b_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2020/1116/153455_ed3c4b64_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2020/1116/153513_6448ba23_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2020/1116/153529_a818b611_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2020/1116/153547_c18e229c_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2020/1116/153603_da9a31f2_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2020/1215/175924_d4f8a9ac_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2020/1215/175948_a77fa716_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2021/0324/163144_7da250d2_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2021/0324/163209_772602c4_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2021/0324/163237_935cfb98_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2021/0324/163258_4e3143a0_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2021/0324/163342_5eae1415_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2021/0324/163359_69c7a4db_352331.png"/></td>
    </tr>
</table>

### 基于Vue的移动端
vue3版本未开发

### 微信公众号消息推送
新版微信公众号的消息推送机制消息体如果字数很多则不完全显示。

<table>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2020/1116/154050_af85354a_352331.jpeg "Screenshot_20201015_150843_com.tencent.mm.jpg"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2020/1116/154104_31b29a07_352331.jpeg "Screenshot_20201015_150911_com.tencent.mm.jpg"/></td>
    </tr>

</table>
