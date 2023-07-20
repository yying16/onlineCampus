##接口文档

以登录接口为例

1.在文档中找到登录接口路径为/login

![image-20230711113042811](E:\YY\yying\搞个项目\assets\image-20230711113042811.png)

2.确定请求参数

![image-20230711113209811](E:\YY\yying\搞个项目\assets\image-20230711113209811.png)

3.根据对应模块拼接请求路径

localhost:9000/[服务名称]/[接口路径]

用户模块：campusUser

消息模块：campusMessage

交际模块：campusContact

4.登录请求的最终路径为 localhost:9000/campusUser/login

请求：

```
POST localhost:9000/campusUser/login
Content-Type: application/json

{
  "loginName": "1712131536",
  "password": "123456"
}
```



返回值：

```
{
  "code": 0,
  "data": {
    "uid": "1",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6IjEiLCJleHAiOjE2ODkwNjUyNTcsInVzZXIiOnsidXNlcklkIjoiMSIsImFjY291bnQiOiIxNzEyMTMxNTM2Iiwib3JpZ2luUGFzc3dvcmQiOiIxMjM0NTYiLCJwYXNzd29yZCI6ImUxMGFkYzM5NDliYTU5YWJiZTU2ZTA1N2YyMGY4ODNlIiwidXNlcm5hbWUiOiJ5eWluZyIsInRlbGVwaG9uZSI6IjEzMTI4NDM4NzAzIiwiZW1haWwiOiIxNzEyMTMxNTM2QHFxLmNvbSIsImF1dG9SZXBseSI6Iuivtu-8jOaIkeWcqCIsInN0YXR1cyI6ZmFsc2UsImNyZWRpdCI6MCwiZGVsZXRlZCI6ZmFsc2UsImNyZWF0ZVRpbWUiOiIyMDIzLTA3LTA1IDEzOjIwOjEyLjAiLCJ1cGRhdGVUaW1lIjoiMjAyMy0wNy0xMSAwMjo1NTo0NC4wIn19.2cpIEgm3nU0g7cECnEOe9tJcouIwPrJMSe3CeKyjlHyyaV-2h_ptcRgs6a4IqXt2IQZ70e1fq5N2OgBrqMf3Rg"
  },
  "msg": null
}
```



##数据校验
因为数据校验在api文档中没有体现，所以在这里写一下
以注册用例为例

请求格式：

```
POST http://localhost:7001/campusUser/register
Content-Type: application/json

{
  "account": "1712131536",
  "password": "123456",
  "username":"yying",
  "telephone": "1312843878"
}
```

响应体数据：

```json
{
  "message": "请求参数有误",
  "errors": {
    "telephone": "手机号不合法"
  },
  "code": 400
}
```



