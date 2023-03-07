
import com.five.library.dao.BookDao;
import org.junit.jupiter.api.Test;


public class Main {
    @Test
    public void main() throws Exception {
        BookDao bookDao = new BookDao();
    }

    public static String capitalize(String str) {
        if (str == null) return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}


