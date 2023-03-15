# Display
## 为[LibraryManagement](https://github.com/OneOFF-ive/LibraryManagement)开发的插件
实现插件接口，输出所有库存，关键代码如下：
```java
@Override
public void apply(Application application) {
        Option option = Option.builder("display")
        .hasArg(false)
        .desc("print all books")
        .build();
        application.addOption(option, (Object[] args) -> {
        var dataAccess = application.getBookManager().getDataAccess();
        for (var data : dataAccess.getAllData()) {
        System.out.println(data);
        }
        });
        }
```    
