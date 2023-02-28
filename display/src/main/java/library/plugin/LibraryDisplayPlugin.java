package library.plugin;

import com.five.AddOptionAble;
import com.five.MyCliHandle;
import org.apache.commons.cli.Option;

public class LibraryDisplayPlugin implements AddOptionAble {

    @Override
    public void addOption(MyCliHandle myCliHandle) {
        Option option = new Option("t", "test", false, "just test");
        myCliHandle.addOption(option, (Object[] args) -> {
            System.out.println("this is a test");
        });
    }
}
