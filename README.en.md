
## Project Introduction
Mulanbay is a comprehensive system designed for the management of personal consumption, exercise, music, reading, health, diet, and life experiences, among other facets of daily living. It integrates statistical and planning modules with a scheduling system to analyze and execute tasks efficiently. Furthermore, it employs a sophisticated point and rating system to provide a holistic assessment of individuals' overall well-being. Additionally, advanced machine learning algorithms are utilized for data prediction and trend analysis.

The system, serving as a comprehensive personal management platform, primarily addresses three key issues:
* "What are my plans (expectations)?" --> (Modules: Statistics, Planning)
* "What do I need to do?" --> (Module: Calendar)
* "What have I done?" --> (Module: Behavioral Analysis)

Based on the above three questions, can we contemplate the following two questions:
* "What kind of person am I?" (User Profile)
* "How will I be?" (Machine Learning) --> (Module: Data Prediction)

The new version of Mulanbay project redesigned with SpringBoot3 and Vue3.

The project includes frontend and backend，mulanbay-server3 is the backend project,only supplies API。

frontend
* Based on Vue3(PC)[mulanbay-ui-vue3](https://github.com/mulanbay/mulanbay-ui-vue3)

Algorithm：
* Based on scikit-learn(python)[mulanbay-sklearn](https://gitee.com/mulanbay/mulanbay-sklearn)

### Mulanbay Introduction

[Mulanbay Introduction](https://gitee.com/mulanbay)

### Function

* based on RBAC user authorization
* distributed schedule 
* based on AHANLP NLP
* provide consume、exercise、music practice、reading、health、diet、experience modules
* unified log management and analysis
* template-based statistics, plans, charts, user behavior
* unified calendar management,auto create and finish
* apply disk、CPU、memory monitor
* unified chart(based on echarts)
* message service(wechat message and email)
* data predict(scikit-learn)

### Technology

* frontend：Vue3、Element Plus、Echarts
* backend：SpringBoot3、Hibernate、Quartz、NLP、Redis & Jwt

| core dependency | version |
|-----------------|---------|
| Spring Boot     | 3.2.X   |
| Hibernate       | 6.X     |
| Quartz          | 2.3.X   |

### Project Structure
``` lua
mulanbay-server
├── mulanbay-ai          -- machine learning，data predict
├── mulanbay-business    -- common business
├── mulanbay-common      -- common classes,utils
├── mulanbay-persistent  -- hibernate
├── mulanbay-pms         -- API
├── mulanbay-schedule    -- schedule
├── mulanbay-web         -- SpringMVC

```

### Run & Deploy

#### 1. version update from 4.0-->5.0
if your environment based on v4.0，and wants to keep former data。
```
Step 1：backup database：
mysqldump -h 127.0.0.1 -u root -p --opt -R mulanbay_db > /your_path/mulanbay_db.sql

Step 2：update database
execute the v4_to_v5.sql from mulanbay-server3\docs：
source /your_path/v4_to_v5.sql
```

#### 2. init database
if you are initial installation：
```
execute the mulanbay_init.sql from mulanbay-server3\docs：

Attached：how to init database and execute the mulanbay_init.sql:
1. enter mysql console
mysql -u root -p

2. create database
create database mulanbay_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

3. choose database
use mulanbay_db

4. import
source /your_path/mulanbay_init.sql
```

#### 3. config and deploy
```
# Step 1：create local yml.

1. copy mulanbay-pms/src/main/resources/application-local-template.yml and rename to application-local.yml，set the local variables。
   Mysql items、Redis items are must configed，wechat items are needed if want to send messages to phone.
   
2. wordcloud、goods similary、diet similary are based on AHANLP，configed in hanlp.properties，ahanlp.properties 
  * hanlp.properties:set root path，E.g：root=D:/ws/AHANLP_base-1.3
  * ahanlp.properties: all items are needed to config
  * ahanlpData download link：（https://pan.baidu.com/s/1demdX1GjhMiJqM58bJzriQ code: gqcs ）
    direct download link from original author：https://github.com/jsksxs360/AHANLP
    the data：
    【model】Google_word2vec_zhwiki210720_300d.bin
    【corpus】zhwiki_210720_preprocessed.simplied.zip

# Step 2：package&run

（1）if you are a professional backend developer, you can run or package it by yourself

1. develop enviroment
  run mulanbay-pms/cn.mulanbay.pms.web.Application

2. product enviroment
  * mvn clean package
  * run:mulanbay-pms/target下/mulanbay-pms-5.0.jar

（2）if you are not a backend developer, you can use the releases

release link：https://gitee.com/mulanbay/mulanbay-server3/releases
Attention：application-local.yml、hanlp.properties、ahanlp.properties are still need to config，also need package into mulanbay-pms-5.0.jar。

default port：8080
```
#### 4. Nginx config

```
    upstream server.mulanbay{
       server 127.0.0.1:8080;
    }
    
    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;
        
        # avatar path
        location /20 {
            root   /your_path/avatar;
            try_files $uri $uri/ /index.html;
            index  index.html index.htm;
            charset   utf-8;
        }
        
        # api
        location /api {
            root   html;
            index  index.html index.htm;
            proxy_pass http://server.mulanbay;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
        
        # web
        location / {
            root /your_path/vue;
            charset   utf-8;
            try_files $uri $uri/ /index.html;
            index  index.html index.htm;            
        }
    }
```
Address: http://localhost/index
Default Account：mulanbay / 123456

### Software Requirement
| software | version |
|----------|---------|
| JDK      | 17+     |
| Nginx    | 1.17+   |
| Redis    | 7.0+    |
| Mysql    | 8.0+    |


### Hardware Requirement
 memory 4G+
 
## System Structure
![structure](docs/%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84.png)

## Online Demo
unsupported

## Contact
* email：fenghong007@hotmail.com

## Reference/Integration project

* calendar UI：[full-calendar](https://fullcalendar.io/)
* NLP：[AHANLP](https://github.com/jsksxs360/AHANLP)
* Vue3 Admin：[RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue)，[Element-Plus](https://element-plus.org/zh-CN/)
* form-create：[form-create](https://www.form-create.com/)

## Project Screenshot

### Web
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

### WeChat message

<table>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2020/1116/154050_af85354a_352331.jpeg "Screenshot_20201015_150843_com.tencent.mm.jpg"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2020/1116/154104_31b29a07_352331.jpeg "Screenshot_20201015_150911_com.tencent.mm.jpg"/></td>
    </tr>

</table>

### Acknowledgment

Thanks to JetBrains for their continued support of Mulanbay project by providing a free license for the full range of products.
<img src="https://foruda.gitee.com/images/1712229322943756523/d28d99c2_352331.png" width="100px" height="100px">
JetBrains OpenSourceSupport：https://jb.gg/OpenSourceSupport.