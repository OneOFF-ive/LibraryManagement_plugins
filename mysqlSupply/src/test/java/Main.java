import com.five.Book;
import com.five.library.DbUtil;
import com.five.library.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.List;


public class Main {
    @Test
    public void main() throws Exception {
        var sqlSession = new SqlSession(DbUtil.getConnection());
        boolean hasResult = sqlSession.execute("com.example.book.selectAll", null);
        if (hasResult) {
            var res = sqlSession.getResult();
            for (var r : res) {
                System.out.println(r);
            }
        }

        hasResult = sqlSession.execute("com.example.book.selectTitle", "iu");
        if (hasResult) {
            var res = sqlSession.getResult();
            for (var r : res) {
                System.out.println(r);
            }
        }

    }
}