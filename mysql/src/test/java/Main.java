
import com.five.library.ioc.IocContainer;
import org.junit.jupiter.api.Test;

import java.io.File;


class TestA {
    int a;
    int b;

    public TestA(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public TestA(Integer a, Integer b) {
        this.a = a;
        this.b = b;
    }

    public TestA() {
    }
}

class TestB {

    TestA a;
}

public class Main {
    @Test
    public void main() throws Exception {
        IocContainer iocContainer = new IocContainer();
        iocContainer.registerBeanByClass("test", TestA.class, 1, 1);

    }

}


