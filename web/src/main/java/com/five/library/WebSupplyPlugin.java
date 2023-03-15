package com.five.library;

import com.five.Application;
import com.five.plugin.IPlugin;
import org.apache.commons.cli.Option;


import static spark.Spark.*;

public class WebSupplyPlugin implements IPlugin {
    @Override
    public void apply(Application application) {
        Option startServiceOption = Option.builder("server")
                .hasArg(false)
                .desc("start a web service at port 7070")
                .build();

        Option stopServiceOption = Option.builder("stop")
                .hasArg(false)
                .desc("stop the web service")
                .build();

        application.addOption(startServiceOption, this::startService);
        application.addOption(stopServiceOption, this::stopService);
    }

    void startService(Object[] args) {
        port(7071);
        post("/", (req, res) -> {
            String content = req.body();
            String[] newArgs = content.split(" ");
            return content;
        });
        awaitInitialization();
    }

    void stopService(Object[] args) {
        stop();
    }

    @Override
    public void close() {
        stopService(null);
    }
}
