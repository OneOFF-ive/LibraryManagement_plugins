# Web
## 为[LibraryManagement](https://github.com/OneOFF-ive/LibraryManagement)开发的插件  
利用[Spark](https://sparkjava.com/)搭建小型的服务器，实现插件接口即可，关键代码如下：  
```java
void startService(Object[] args) {
    port(7070);
    post("/", (req, res) -> {
        String content = req.body();
        String[] newArgs = content.split(" ");
        MyCli myCli = application.getMyCli();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream standardOut = System.out;
        System.setOut(ps);

        myCli.parseAllOptions(newArgs);

        System.setOut(standardOut);
        return baos.toString();
    });
    awaitInitialization();
}
```    
接口实现代码如下:  
```java
@Override
public void apply(Application application) {
    this.application = application;

    Option startServiceOption = Option.builder("server")
            .hasArg(false)
            .desc("start a web service at port 7070")
            .build();

    Option stopServiceOption = Option.builder("stop")
            .hasArg(false)
            .desc("stop the web service")
            .build();

    application.addOption(startServiceOption, this::startService);
    application.addOption(stopServiceOption, this::stopService);
}
```

