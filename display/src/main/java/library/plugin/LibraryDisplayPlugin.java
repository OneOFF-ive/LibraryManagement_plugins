package library.plugin;

import com.five.Application;
import com.five.data.DataAccess;
import com.five.data.JsonDataAccessImpl;
import com.five.plugin.PluginService;
import org.apache.commons.cli.Option;

public class LibraryDisplayPlugin implements PluginService {
    @Override
    public void server(Application application) {
        Option option = Option.builder("display")
                .hasArg(false)
                .desc("print all books")
                .build();
        application.addOption(option, (Object[] args) -> {
            JsonDataAccessImpl dataAccess = (JsonDataAccessImpl)application.getBookManager().getDataAccess();
            var allData = dataAccess.getDataList();
            System.out.println(allData);
        });
    }
}
