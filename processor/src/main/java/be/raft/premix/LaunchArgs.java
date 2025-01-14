/*
  AtlasWorld's Proprietary License

  Copyright (c) 2022 - 2024 AtlasWorld Studio. All Rights Reserved.

  This software is proprietary to AtlasWorld Studio and may only be used internally
  within the organization obtaining the software. Any commercial use, copying, modification,
  distribution, or exploitation of the software requires express written permission from AtlasWorld Studio.
*/
package be.raft.premix;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LaunchArgs {
    private static OptionSpec<File> INPUT_OPTION;
    private static OptionSpec<File> OUTPUT_OPTION;

    private static final OptionParser OPTION_PARSER = new OptionParser(false) {
        {
            // Informative
            acceptsAll(asList("version", "v"), "Displays the server version.");
            acceptsAll(asList("help", "?"), "Lists every possible arguments and their usages.");

            // Processing
            INPUT_OPTION = acceptsAll(asList("input", "in"), "Input jar to be processed.")
                    .withRequiredArg()
                    .ofType(File.class);

            OUTPUT_OPTION = acceptsAll(asList("output", "out"), "Output for processed jar.")
                    .withRequiredArg()
                    .ofType(File.class);
        }
    };

    public static LaunchArgs instance;
    private final OptionSet launchOpt;

    private LaunchArgs(OptionSet launchOpt) {
        this.launchOpt = launchOpt;
    }

    public static LaunchArgs parse(String[] args) {
        OptionSet options = null;

        try {
            options = OPTION_PARSER.parse(args);
        } catch (OptionException e) {
            System.err.println("\nFailed to start " + MixinProcessor.class.getSimpleName() + ": " + e.getMessage());
            System.err.println("Use '--help' or '--?' to show help menu.\n");
            System.exit(-1);
        }

        if (options.has("help"))
            printHelp();

        if (options.has("version"))
            printVersion();

        instance = new LaunchArgs(options);
        return get();
    }

    private static void printVersion() {
        long compileTime = BuildProperties.getInstance().getBuildTime().getTime();
        long currentTime = System.currentTimeMillis();

        long differenceTime = currentTime - compileTime;

        System.out.println();
        System.out.println(BuildProperties.getInstance());
        System.out.println("Built " + getTimeAgo(differenceTime) + " ago");
        System.out.println();

        System.exit(0);
    }

    private static void printHelp() {
        try {
            System.out.println(); // Spacing
            LaunchArgs.OPTION_PARSER.printHelpOn(System.out);
            System.out.println(); // Spacing
        } catch (IOException e) {
            System.err.println("Failed to print help menu.");
            e.printStackTrace(System.err);
        }
        System.exit(0);
    }

    private static String getTimeAgo(long timeDifference) {
        long days = TimeUnit.DAYS.convert(timeDifference, TimeUnit.MILLISECONDS);
        long hours = TimeUnit.HOURS.convert(timeDifference, TimeUnit.MILLISECONDS) % 24;
        long minutes = TimeUnit.MINUTES.convert(timeDifference, TimeUnit.MILLISECONDS) % 60;

        if (days > 0)
            return days + " days";
        else if (hours > 0)
            return hours + " hours";
        else if (minutes > 0)
            return minutes + " minutes";
        else return "less than a minute";
    }

    public static LaunchArgs get() {
        if (instance == null)
            throw new IllegalStateException("Arguments not initialized!");

        return instance;
    }

    @NotNull
    public File input() {
        File in = this.launchOpt.valueOf(INPUT_OPTION);
        if (in == null)
            throw new IllegalStateException("Input jar not specified");

        if (!in.isFile())
            throw new IllegalStateException("Input jar does not exist!: " + in);

        return in;
    }

    @NotNull
    public File output() {
        File out = this.launchOpt.valueOf(OUTPUT_OPTION);
        if (out == null)
            throw new IllegalStateException("No output file specified.");

        return out;
    }

    private static List<String> asList(String... args) {
        return List.of(args);
    }
}
