
import com.five.library.ioc.Inject;
import com.five.library.ioc.IocContainer;
import org.junit.jupiter.api.Test;


class TestA {
    int a;
    int b;

    public TestA(int a, int b) {
        this.a = a;
        this.b = b;
    }
}

class TestB {
    @Inject
    TestA a;
}

public class Main {
    @Test
    public void main() throws Exception {
        IocContainer iocContainer = new IocContainer();
        iocContainer.registerBean("testA", TestA.class, 1, 2);
        iocContainer.registerBean("testB", TestB.class);
        var a = iocContainer.getBean("testA");
        var b = iocContainer.getBean("testB");

    }

}


