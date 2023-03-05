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
DbUtil.java 用于获取数据库连接，未来会改动，目标是手写一个数据库连接池  
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