import com.five.Book;
import com.five.library.sql.DbUtil;
import com.five.library.sql.SqlSession;
import org.junit.jupiter.api.Test;


public class Main {
    @Test
    public void main() throws Exception {
        var sqlSession = new SqlSession(DbUtil.getConnection());
        var book = new Book("test1", "i223153", "wu", 100, 100);
        var hasResult = sqlSession.execute("com.example.book.selectAmount", 100);
        if (hasResult) {
            var res = sqlSession.getResult();
            for (var r : res) {
                System.out.println(r);
            }
        }
    }
}


//    boolean hasResult = sqlSession.execute("com.example.book.selectAll", null);
//        if (hasResult) {
//                var res = sqlSession.getResult();
//                for (var r : res) {
//                System.out.println(r);
//                }
//                }
//
//                hasResult = sqlSession.execute("com.example.book.selectTitle", "iu");
//                if (hasResult) {
//                var res = sqlSession.getResult();
//                for (var r : res) {
//                System.out.println(r);
//                }
//                }