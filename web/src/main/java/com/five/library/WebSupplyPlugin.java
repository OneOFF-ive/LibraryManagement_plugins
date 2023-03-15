package com.five.library;

import com.OneFive.MyCli;
import com.five.Application;
import com.five.plugin.IPlugin;
import org.apache.commons.cli.Option;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static spark.Spark.*;

public class WebSupplyPlugin implements IPlugin {
    private Application application;

    @Override
    public void apply(Application application) {
        this.application = application;

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
        port(7070);
        post("/", (req, res) -> {
            String content = req.body();
            String[] newArgs = content.split(" ");
            MyCli myCli = application.getMyCli();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream standardOut = System.out;
            System.setOut(ps);

            myCli.parseAllOptions(newArgs);

            System.setOut(standardOut);
            return baos.toString();
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
