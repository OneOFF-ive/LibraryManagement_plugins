
import com.five.Application;
import com.five.library.MysqlSupplyPlugin;
import com.five.library.ioc.Inject;
import com.five.library.ioc.IocContainer;
import com.five.plugin.PluginContext;
import com.five.plugin.PluginInfo;
import com.five.plugin.PluginManager;
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
        iocContainer.registerBean("test", TestA.class, 1, 1);
        if (int.class.isAssignableFrom(Integer.class)) {
            int a = 1;
        }
    }

}


