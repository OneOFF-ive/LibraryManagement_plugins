# mysql
## 为[LibraryManagement](https://github.com/OneOFF-ive/LibraryManagement)开发的插件  
一开始只想用jdbc连接MySQL数据库实现，后来突发奇想，为何不实现一个类似于mybatis那样的orm框架？于是便有了这个项目。  

book-mapper.xml 文件用于定义 SQL 映射语句以及与之相应的映射关系，实现了数据源与 Java 对象之间的映射。在指定参数类型或者返回类型时，一定要指定类型的完整类名。  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.book">
    <insert id="insert" parameterType="com.five.Book">
        INSERT INTO book (isbn, title, author, totalAmount, currentAmount)
        VALUES (#{isbn}, #{title}, #{author}, #{totalAmount}, #{currentAmount})
    </insert>

    <update id="update" parameterType="com.five.Book">
        UPDATE book SET title=#{title}, author=#{author}, totalAmount=#{totalAmount}, currentAmount=#{currentAmount}
        WHERE isbn = #{isbn}
    </update>

    <delete id="delete" parameterType="java.lang.String">
        DELETE FROM book WHERE isbn = #{isbn}
    </delete>
    
    <select id="selectAll" resultType="com.five.Book">
        SELECT isbn, title, author, totalAmount, currentAmount
        FROM book
    </select>
</mapper>
```
XMLMapperParser.java 用于解析book-mapper.xml文件，用id2TagInfo储存id和tag信息的映射关系
```java
    public static class TagInfo {
    public String paraType;
    public String sql;
    public String resType;

    public TagInfo(String sql, String paraType, String resType) {
        this.paraType = paraType;
        this.sql = sql;
        this.resType = resType;
    }
}
```
SqlSession.java 是最主要的类，有一个内部类SqlBuilder负责构建可执行的sql语句，execute方法会调用SqlBuilder的buildSql方法，并执行构建的sql语句，可以根据execute的返回值来确定是否有查询结果。
```java
var hasResult = sqlSession.execute("com.example.book.selectAmount", 100);
if (hasResult) {
    var res = sqlSession.getResult();
    for (var r : res) {
        System.out.println(r);
    }
}
```  
ResultParser.java 用于解析执行sql语句后返回的结果，将数据映射为book-mapper.xml中指定resultType。  
  
ObjectMirror.java 利用反射机制，调用object的get和set方法，是实现SqlBuilder和ResultParser的关键。  
  
BookDao.java 实现了DataAccess接口，可以接入[LibraryManagement](https://github.com/OneOFF-ive/LibraryManagement)中。   
  
MysqlSupplyPlugin.java 插件入口  
  
2023/3/8 手写了一个[数据库连接池](https://github.com/OneOFF-ive/MyConnectionPool),并在该项目中投入使用，利用pool下的DbUtil类和SQLConnectionFactory类为连接池创建连接  
  
2023/3/9 将数据库配置文件交给[LibraryManagement](https://github.com/OneOFF-ive/LibraryManagement)统一管理，通过MysqlSupplyPlugin类的初始化获得pluginContext，从而获取配置信息。  
```java
public MysqlSupplyPlugin(PluginContext pluginContext) {
    File connfigFile = pluginContext.config;
    try {
        String content = new String(Files.readAllBytes(connfigFile.toPath()));
        Gson gson = new Gson();
        config = gson.fromJson(content, Config.class);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```  
但是这样会造成另一个问题,初始化一个BookDao实例相当麻烦，模块之间的耦合性过高，考虑手写一个IOC容器来解决这个问题
```java
public void apply(Application application) {
    var bookManger = application.getBookManager();
    var databaseConfig = new DatabaseConfig(config.url, config.user, config.password);
    var poolConfig = new PoolConfig(config.maxSize, config.maxIdleTime, config.heartBeat, config.checkTimeOut, config.validateConnection, config.checkAlways);
    var sqlConnectionFactory = new SQLConnectionFactory(databaseConfig);
    var sqlSessionFactory = new SqlSessionFactory(new MyConnectionPool<>(poolConfig, sqlConnectionFactory));

    try {
        bookManger.setDataAccess(new BookDao(sqlSessionFactory));
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```  
2023/3/13 去南京玩了一个周末，手写了一个Ioc容器来管理各个实例，修改了各个组件使其支持该Ioc容器，并且将[数据库连接池](https://github.com/OneOFF-ive/MyConnectionPool)内置到该项目中并做出修改，关键代码如下：  
```java
public void registerBean(String beanName, Class<?> beanClass, Object... constructorArgs) throws InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    // 利用反射创建Bean对象
    Object bean = createBeanInstance(beanClass, constructorArgs);
    // 将Bean对象存储到Map中
    beanMap.put(beanName, bean);
    // 注入Bean对象的依赖
    injectBeanDependencies(bean);

    // 如果有initByIoc方法则执行
    try {
        var mtd = beanClass.getMethod("initByIoc");
        mtd.invoke(bean);
    } catch (NoSuchMethodException ignore) {}
}
```  
使用注解来表明依赖关系，示例如下：  
```java
@Inject(clz = "com.five.library.pool.MyConnectionPool")
private MyConnectionPool<Connection> connectionPool;
```  
因为时间和精力有限，没有实现用xml文件注册的功能，只能硬编码注册，使用示例如下：  
```java
void registerBean(IocContainer iocContainer) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
    iocContainer.registerBean(DatabaseConfig.class.getName(), DatabaseConfig.class, config.url, config.user, config.password);
    iocContainer.registerBean(PoolConfig.class.getName(), PoolConfig.class, config.maxSize, config.maxIdleTime, config.heartBeat, config.checkTimeOut, config.validateConnection, config.checkAlways);
    iocContainer.registerBean(SQLConnectionFactory.class.getName(), SQLConnectionFactory.class);
    iocContainer.registerBean(MyConnectionPool.class.getName(), MyConnectionPool.class);
    iocContainer.registerBean(SqlSessionFactory.class.getName(), SqlSessionFactory.class);
    iocContainer.registerBean(BookDao.class.getName(), BookDao.class);
}
```