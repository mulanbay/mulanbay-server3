
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
* 提供基于模板化的统计、计划、图表、行为配置及分析
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

### 软件要求
| 软件                    | 版本    |
| ---------------------- |-------|
| JDK                    | 17+   |
| Nginx                  | 1.17+ |
| Redis                  | 7.0+  |
| Mysql                  | 8.0+  |

备注：
(1)因为SQL语句采用了代码块等新特性，并且项目基于SpringBoot3，因此低版本的JDK不再支持。
(2)以上四个软件是项目运行的必备软件，请在部署前安装完毕。

### 硬件要求
 内存4G+

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

#### 1. 版本升级 4.0-->5.0
如果您的项目原来是基于4.0版本部署的，需要保留原来的用户数据，那么数据库数据需要进行升级，改动比较多，务必先备份数据库。
```
Step 1：备份原有数据库，避免升级失败。备份命令可参考：
mysqldump -h 127.0.0.1 -u root -p --opt -R mulanbay_db > /您的路径/mulanbay_db.sql

Step 2：数据库数据升级
在MySQL终端执行 mulanbay-server3的docs目录下v4_to_v5.sql,命令可参考：
source /您的路径/v4_to_v5.sql
```

#### 2. 数据库初始化
如果您是全新部署5.0版本项目，步骤如下：
```
初始化数据库
1. 下载源代码
2. 在mysql中创建数据库，比如:mulanbay_db
3. 初始化数据库,执行mulanbay-server3工程docs目录下的sql文件：mulanbay_init.sql

附：数据库导入方法：
1. 进入mysql终端
mysql -u root -p

2. 创建数据库
create database mulanbay_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

3. 选择数据库
use mulanbay_db

4. 导入数据库
source /xx/xx/xx/mulanbay_init.sql(数据库文件绝对路径)
```

#### 3. 配置及部署
数据库升级或部署完成后，接着进行配置文件配置。
```
# Step 1：修改配置文件

1. 在子模块mulanbay-pms/src/main/resources/目录下复制application-local-template.yml文件并重命名为application-local.yml，设置本地配置。
   其中Mysql数据库配置、Redis配置为必须配置，如果需要使用微信公众号的消息发送功能，需要配置.
   注意：您也可以直接在application.yml文件上修改配置，不配置本地化文件application-local.yml，只是后续软件升级会稍微麻烦点。
   
2. 智能客服、词云、商品重复度、饮食重复度等需要用到AHANLP的自然语言处理，需要配置hanlp.properties，ahanlp.properties
  * hanlp.properties文件中需要设置根路径，如：root=D:/ws/AHANLP_base-1.3
  * ahanlp.properties文件中需要设置里面的各个配置项
  * NLP所需要的ahanlpData文件包，请到百度网盘下载：（链接: https://pan.baidu.com/s/1demdX1GjhMiJqM58bJzriQ 提取码: gqcs ）
    或者直接去原作者项目处下载：https://github.com/jsksxs360/AHANLP
    说明：
    【模型】Google_word2vec_zhwiki210720_300d.bin
    【语料】zhwiki_210720_preprocessed.simplied.zip

注意：application-local.yml、hanlp.properties、ahanlp.properties都需要配置

微信公众号及邮件发送配置请参考文档目录下：后端手册--消息发送

# Step 2：打包&运行

（1）如果您是专业的后端开发人员，可以自己运行或者打包

1. 开发环境
  运行mulanbay-pms子工程下的cn.mulanbay.pms.web.Application

2. 正式环境
  * 进入到mulanbay-server3目录，运行mvn clean package
  * 运行mulanbay-pms/target下的mulanbay-pms-5.0.jar文件

（2）如果您不是开发者，或者没有相关后端开发经验，您可以直接使用release出来的版本进行运行，不过配置文件还是需要根据上一个步骤里面的"修改配置文件"步骤进行本地化配置，配置完后需要替换掉压缩包里面的文件。

release文件下载地址：https://gitee.com/mulanbay/mulanbay-server3/releases
注意：application-local.yml、hanlp.properties、ahanlp.properties都需要配置，且需要打包到mulanbay-pms-5.0.jar里面。

后端项目默认的端口是：8080
```
#### 4. Nginx配置
木兰湾是前后端分离版本，mulanbay-server3后端api运行后，需要再运行前端项目。
（1）如果您是开发环境以源码方式运行，那么直接可以访问前端的地址就可以了，无需使用Nginx.
（2）如果您是正式环境以jar包方式运行，那么需要使用Nginx进行前后端跨域配置才能访问，配置如下：
```
    upstream server.mulanbay{
       server 127.0.0.1:8080;
    }
    
    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;
        
        # 头像地址
        location /20 {
            root   /your_path/avatar;
            try_files $uri $uri/ /index.html;
            index  index.html index.htm;
            charset   utf-8;
        }
        
        # 后端api地址
        location /api {
            root   html;
            index  index.html index.htm;
            proxy_pass http://server.mulanbay;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
        
        # 前端地址
        location / {
            # (发布出的文件夹地址)
            root /your_path/vue;
            charset   utf-8;
            try_files $uri $uri/ /index.html;
            index  index.html index.htm;            
        }
    }
```
启动Nginx后，在浏览器输入: http://localhost/index
账号密码：mulanbay / 123456
 
## 系统架构
![系统架构](docs/%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84.png)

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
        <td><img src="https://foruda.gitee.com/images/1711693189324687407/a51df822_352331.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1711693235503695130/4f105c03_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1711693264317884407/7ab05073_352331.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1711693286994521138/14032088_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1711693307354029381/4c82d80e_352331.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1711693329137030436/6607f869_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1711693349834291703/ad902327_352331.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1711693368615440821/e65729b1_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1711693388167077973/d06544c3_352331.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1711693405769701501/8c3e86a0_352331.png"/></td>
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

### 鸣谢
谢谢JetBrains公司一直以来对木兰湾项目的支持，提供了全系列产品的免费license。

<img src="https://foruda.gitee.com/images/1712229322943756523/d28d99c2_352331.png" width="100px" height="100px">

JetBrains开源支持计划：https://jb.gg/OpenSourceSupport.
