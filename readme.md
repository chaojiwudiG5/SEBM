# 结构设计

## 对象：
### po: persistent object 持久化对象，和数据库表一一对应
### bo: business object 业务对象，包含业务逻辑
### dto: data transfer object 数据传输对象，controller和service之间传输数据
### vo: view object 视图对象，controller和前端之间传输数据
### po: 实体对象，和数据库表一一对应，一般用在mybatis

## 层次：
### controller里面只有vo dto
### service负责vo dto po和bo的转化
### 逻辑部分由bo负责自己的业务逻辑，涉及到对象转化、数据访问、复杂逻辑service负责
### dao只负责拿数据然后创建po
