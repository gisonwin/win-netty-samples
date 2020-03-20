# windows 环境下杀掉已知端口的进程

1. 现象 

    在windows环境下进行springboot的开发,由于电脑卡死后,将idea进程杀掉后.

   然后再次运行springboot,一直提示端口被占用(如8190)

2. 分析原因

   由于 springboot运行时是启动一个进程,所以这种现象就是上次启动的进程没有释放掉.

3. 如何杀死

   - 查找是否存在占用该端口

     ```bash
     netstat -nao |findstr "8190"
     #查找是否存在8190
     #C:\Users\ds>netstat -nao |findstr "8190"
     #  TCP    0.0.0.0:8190           0.0.0.0:0              LISTENING       31792
     #  TCP    [::]:8190              [::]:0                 LISTENING       31792
     ```

     最后一列显示出的为pid.本实例为31792

   - 通过任务管理器找到占用该端口的服务名称

     ```bash
     #我们通过tasklist查看该pid对应的服务名称
     tasklist |findstr "31792"
     #此时我们会看到该服务名称对应的是java.exe,说明是我们启动的springboot应用.
     #java.exe                     31792 Console                    1     72,420 K
     ```

   - 强制杀死该服务或pid即可.

     ```bash
     #我们将该pid杀掉
     taskkill /f /t /im 31792
     #成功: 已终止 PID 31792 (属于 PID 26068 子进程)的进程。
     ```

     
