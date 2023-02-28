package library.plugin;

import com.five.MyCliHandle;
import com.five.PluginService;
import org.apache.commons.cli.Option;

public class LibraryDisplayPlugin implements PluginService {
    @Override
    public void server(MyCliHandle myCliHandle) {
        Option option = new Option("t", "test", false, "just test");
        myCliHandle.addOption(option, (Object[] args) -> {
            System.out.println("this is a test");
        });
    }
}
