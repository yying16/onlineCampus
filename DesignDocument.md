#系统设计
##注意事项
启动类记得扫描下common模块

##网关（9000）

####过滤器
token过滤器：

token在redis中的存储结构{key:uid,value:{token:token,data:User()}}
#### 拦截器
*后续把跳过的路径写入nacos中*

##用户模块user(7001)
登录接口

注册接口

获取用户信息详情接口

##消息模块message(7002)

####websocket(http 前->后 ; ws 后->前)
*onMessage:*
只对用户在聊天窗口点击【发送】按钮
系统发送的消息（sender="system")
####redis结构
*自动回复消息*（【autoReply】Hash: {key:用户id，value:自动回复内容}）
    
(放在里面的都是有设置自动回复内容的并且已经下线了的)

【不过期，只手动添加删除】

每次用户退出登录时插入数据（，登录时删除数据）

*用户的消息列表*（Hash:{key:对方id/request/system,value:聊天缓存列表}）

用户消息:

{
    receiver:{用户详细数据}
    dialog:{聊天记录}
}



【不过期】

用户登录时添加，退出登录时删除，懒加载时添加列表元素



##交际模块contact(7003)

##交易模块trade(7004)

##兼职模块job（7005）

##招募模块recruit（7006）

##管理模块manage（7007）