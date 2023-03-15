import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static spark.Spark.*;

public class Test {
    public static void main(String[] args) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream standardOut = System.out;
        System.setOut(ps);

        System.out.println("123");

        System.setOut(standardOut);

        System.out.println("123312313");

    }
}
