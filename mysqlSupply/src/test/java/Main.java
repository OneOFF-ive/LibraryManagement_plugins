import com.five.Book;
import com.five.library.DbUtil;
import com.five.library.SqlSession;
import com.five.library.XMLMapperParser;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    @Test
    public void main() throws Exception {
//        XMLMapperParser xmlMapperParser = new XMLMapperParser("book-mapper.xml");
//        xmlMapperParser.paresXml();
//
//
//        var book = new Book("xasda", "i231441", "w31", 13, 13);
//        var sql = xmlMapperParser.getSql("com.example.book.dao.BookDAO.delete", "i231441");
//
//        var conn = DbUtil.getConnection();
//        var state = conn.createStatement();
//        System.out.println(sql);
//        state.execute(sql);

        var sqlSession = new SqlSession(DbUtil.getConnection());
        sqlSession.execute("com.example.book.dao.BookDAO.selectAll", null);


//        var res = state.getResultSet();
//        while (res.next()) {
//            System.out.print(res.getString("title") + "\t");
//            System.out.print(res.getString("isbn") + "\t");
//            System.out.print(res.getString("author") + "\t");
//            System.out.print(res.getInt("totalAmount") + "\t");
//            System.out.print(res.getInt("currentAmount") + "\n");
//        }
    }
}