import com.five.Book;
import com.five.library.mirror.ObjectMirror;
import org.junit.jupiter.api.Test;

public class TestObjectMirror {
    @Test
    public void test() {
        var objMirror = ObjectMirror.createFromJavaBean(Book.class);
        System.out.println(objMirror);
        var book = objMirror.create();
        System.out.println(book);
        objMirror.set(book, "title", "My Title");
        System.out.println(book);
        System.out.println((String) objMirror.get(book, "title"));
    }
}

