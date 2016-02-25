# cooltoo_backend
Cool too backend project


使用Gradle作为构建工具，运行 gradle build 来编译打包代码。

运行环境采用springboot，打出的jar包放在，build/libs目录下，直接运行 java -jar ***.jar 启动tomcat容器

运行之前需要配置一下文件：

－ envs/dev/application.properties
	该文件中记录了数据库的地址和用户名密码
	
数据库配置：
－ 数据库采用MySQL，安装好后，创建名称为"cooltoo"的数据库
－ 然后运行 gradle flywayMigrate，这条语句用来建立数据库的表

