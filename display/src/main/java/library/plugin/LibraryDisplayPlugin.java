package library.plugin;

import com.five.Application;
import com.five.plugin.IPlugin;
import org.apache.commons.cli.Option;

public class LibraryDisplayPlugin implements IPlugin {

    @Override
    public void apply(Application application) {
        Option option = Option.builder("display")
                .hasArg(false)
                .desc("print all books")
                .build();
        application.addOption(option, (Object[] args) -> {
            var dataAccess = application.getBookManager().getDataAccess();
            for (var data : dataAccess.getAllData()) {
                System.out.println(data);
            }
        });
    }

}
