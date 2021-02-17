# ThreadFlow

#### 介绍
线程编排的小工具. 可以执行任意串并行组合的线程;  
支持单个单元的超时控制;  
每个单元可以有自己的参数及返回值. 支持单个单元结果的异步回调.   
支持总体任务的超时调用及总体任务结束后的结果调用;  
支持动态的决定不必要再执行的单元跳过执行, 亦可将不必要继续执行而正在执行的线程进行中断;   
每个单元可适用在不同的任务编排中
支持动态修改后置节点   
如果喜欢的话,点个星标  

#### 软件架构
了解本项目可以从test里的测试用例看起.  
基本的功能:  
 **1. 串行线程调用** :  
&nbsp;&nbsp;1.1. 单元执行  A -> B -> C    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;单元耗时&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp;单位:秒    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;是否异常  否&nbsp;&nbsp;&nbsp;否&nbsp;&nbsp;&nbsp;否    
&nbsp;&nbsp;&nbsp;那么当A单元执行成功后,会启动B执行, B单元执行成功后会启动C    
     
&nbsp;&nbsp;1.2.  单元执行  A -> B -> C  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;单元耗时&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp;单位:秒  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;是否异常  否&nbsp;&nbsp;&nbsp;是&nbsp;&nbsp;&nbsp;否  
&nbsp;&nbsp;&nbsp;那么当A单元执行成功后,会启动B, B线程执行异常会直接跳过C单元的执行  
 
**2. 简单的并行调用:**   
&nbsp;&nbsp;2.1 全部非必须  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A --非必须 正常 1秒-->  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;B --非必须 正常 2秒-->  D --正常 0.5秒--   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C --非必须 正常 2秒-->  
&nbsp;&nbsp;&nbsp;&nbsp;谁执行的快, D单元用谁的参数  
&nbsp;&nbsp;&nbsp;&nbsp;预期执行: A执行成功后会启动D线程, D线程开始执行并将B,C线程中断, 整体执行1.5秒   
&nbsp;&nbsp;2.2 既有必须也有非必须  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A -- 必须 &nbsp;&nbsp;正常 1秒-->    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;B --非必须 正常 2秒-->  D --正常 0.5秒--  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C --非必须 正常 2秒-->    
&nbsp;&nbsp;&nbsp;&nbsp;A单元先执行成功  
&nbsp;&nbsp;&nbsp;&nbsp;B,C单元谁先执行成功,谁启动D  
&nbsp;&nbsp;&nbsp;&nbsp;预期执行 2.5秒  
  2.3 全部必须项  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A -- 必须 &nbsp;&nbsp;正常 1秒-->   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;B -- 必须 &nbsp;&nbsp;正常 2秒-->  D --正常 0.5秒--  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C -- 必须 &nbsp;&nbsp;正常 3秒-->     
&nbsp;&nbsp;&nbsp;&nbsp;A,B,C单元对于D单元必须  
&nbsp;&nbsp;&nbsp;&nbsp;预期: A,B,C都执行成功, D再执行   

  2.4 全部都必须且有异常  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A -- 必须 &nbsp;&nbsp;正常 1秒-->   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;B -- 必须 &nbsp;&nbsp;异常 1秒-->  D --正常 0.5秒--  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C -- 必须 &nbsp;&nbsp;正常 2秒-->   
&nbsp;&nbsp;&nbsp;&nbsp;A,B,C单元对于D单元必须_   
&nbsp;&nbsp;&nbsp;&nbsp;A,B都执行1秒, C执行2秒   
&nbsp;&nbsp;&nbsp;&nbsp;其中B会执行失败  
&nbsp;&nbsp;&nbsp;&nbsp;预期: A执行成功, B执行异常后将D 设置为Skipped,跳过执行. 并将C中断.   

 **3. 串并行组合**   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A ---->  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C ---->  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;B ---->  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
F  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;D ---->E ---->  
&nbsp;&nbsp;&nbsp;&nbsp;A,B,D同时启动  
&nbsp;&nbsp;&nbsp;&nbsp;A,B都执行完成之后执行C  
&nbsp;&nbsp;&nbsp;&nbsp;D执行完成之后执行E  
&nbsp;&nbsp;&nbsp;&nbsp;C,E执行完成之后执行F  
&nbsp;&nbsp;&nbsp;&nbsp;其中C,E对F都是非必须,  假如C很快执行完成,启动F执行. 此时D接口还在执行, 那么在F启动执行时会设置E为Skipped 跳过执行, 并将D线程中断.  

 **4. 其中WorkHandler可动态拓展**  
        例如 1.可以设置如果A单元执行不成功,则启动B  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.可以设置根据A单元执行结果, 动态的决定后一级单元B,C哪一个执行.  
#### 安装教程
  直接拉取
#### 使用说明
  简单的java项目, 可直接打jar包引入你的项目
#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
