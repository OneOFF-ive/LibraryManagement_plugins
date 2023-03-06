
import org.junit.jupiter.api.Test;


public class Main {
    @Test
    public void main() throws Exception {
        System.out.println(capitalize("isbn"));
    }

    public static String capitalize(String str) {
        if (str == null) return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}


