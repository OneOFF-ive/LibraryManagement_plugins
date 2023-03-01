package library.plugin;

import com.five.MyCliHandle;
import com.five.PluginService;
import org.apache.commons.cli.Option;

public class LibraryDisplayPlugin implements PluginService {
    @Override
    public void server(MyCliHandle myCliHandle) {
        Option option = Option.builder("display")
                .hasArg(false)
                .desc("print all books")
                .build();
        myCliHandle.addOption(option, (Object[] args) -> {
            System.out.println(myCliHandle.getDataList());
        });
    }
}
