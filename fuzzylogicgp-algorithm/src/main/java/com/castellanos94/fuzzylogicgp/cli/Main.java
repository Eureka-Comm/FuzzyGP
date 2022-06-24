package com.castellanos94.fuzzylogicgp.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import com.castellanos94.fuzzylogicgp.core.OperatorException;
import com.castellanos94.fuzzylogicgp.core.Query;
import com.castellanos94.fuzzylogicgp.core.Utils;
import com.castellanos94.fuzzylogicgp.examples.Examples;
import com.castellanos94.fuzzylogicgp.parser.EDNParser;
import com.castellanos94.fuzzylogicgp.parser.FLGPObjectParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "FLJF", description = "@|bold Demonstrating FLJF |@", headerHeading = "@|bold,underline Demonstration Usage|@:%n%n")
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    @Option(names = { "-f", "--file" }, description = "Path and name of file")
    private String fileName;
    @Option(names = { "--seed" }, description = "Seed random")
    private Long seed;
    @Option(names = {
            "--parallel" }, description = "Parallel support, if seed is set then sequential process is active.")
    private boolean parallelSupport = true;

    @Option(names = { "-h", "--help" }, description = "Display help/usage.", help = true)
    private boolean help;

    @Option(names = { "-p", "--plot" }, description = "Plot linguistic states, Evaluation script is requiered.")
    private ArrayList<String> plot;

    @Option(names = { "--evaluation-demo" }, description = "Run a evaluation demo.")
    private boolean evaluationDemo;
    @Option(names = { "--discovery-demo" }, description = "Run a discovery demo.")
    private boolean discoveryDemo;

    @Option(names = { "--iris" }, description = "Run a discovery demo with iris dataset.")
    private boolean irisDemo;
    @Option(names = { "--generator" }, description = "Run a discovery demo with a generator as root.")
    private boolean generatorDemo;
    @Option(names = { "--EDN" }, description = "Supported EDN script.")
    private boolean formatEdn;
    @Option(names = { "--N" }, description = "No run task.")
    private boolean executeTask;

    public static void main(String[] args)
            throws OperatorException, CloneNotSupportedException, IOException, URISyntaxException {
        final Main main = CommandLine.populateCommand(new Main(), args);

        if (main.help) {
            CommandLine.usage(main, System.out, CommandLine.Help.Ansi.AUTO);
        } else {
            Query query = null;
            if (main.seed != null) {
                main.parallelSupport = false;
            }
            TaskExecutor.parallelSupport = main.parallelSupport;
            if (main.fileName != null) {

                if (main.formatEdn) {
                    EDNParser ednParser = new EDNParser(main.fileName);
                    query = ednParser.parser();
                } else {
                    FLGPObjectParser flgpop = new FLGPObjectParser();
                    query = (Query) flgpop.fromJson(Paths.get(main.fileName), Query.class);
                }
                if (!main.executeTask) {
                    TaskExecutor.execute(query);
                }

                if (main.plot != null && main.plot.size() > 0) {
                    TaskExecutor.plotting(query, main.plot);
                }
            }
            if (main.evaluationDemo) {
                logger.info("Running demo evaluation");
                query = Examples.evaluation();
                TaskExecutor.execute(demoToFile(query));

                if (main.plot != null && main.plot.size() > 0) {
                    TaskExecutor.plotting(query, main.plot);
                }
            } else if (main.discoveryDemo) {
                logger.info("Running demo discovery");
                query = Examples.discovery();
                TaskExecutor.execute(demoToFile(query));
            } else if (main.irisDemo) {
                logger.info("Running irs demo");
                query = Examples.irisQuery();
                TaskExecutor.execute(demoToFile(query));
            } else if (main.generatorDemo) {
                logger.info("Running generator demo");
                query = Examples.generator();
                TaskExecutor.execute(demoToFile(query));
            }

        }
    }

    private static Query demoToFile(Query query) throws IOException {
        InputStream resourceAsStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream("datasets" + File.separator + query.getDb_uri());
        logger.info("Relative path: " + "datasets" + File.separator + query.getDb_uri());
        if (resourceAsStream == null) {
            resourceAsStream = ClassLoader.getSystemClassLoader()
                    .getResourceAsStream(File.separator + "datasets" + File.separator + query.getDb_uri());
        }
        if (resourceAsStream == null) {
            resourceAsStream = Main.class
                    .getResourceAsStream(File.separator + "datasets" + File.separator + query.getDb_uri());
        }
        if (resourceAsStream == null) {
            resourceAsStream = Main.class.getResourceAsStream("datasets" + File.separator + query.getDb_uri());
        }
        if (resourceAsStream == null) {
            resourceAsStream = Main.class.getClass()
                    .getResourceAsStream("datasets" + File.separator + query.getDb_uri());
        }
        if (resourceAsStream == null) {
            resourceAsStream = Main.class.getClass()
                    .getResourceAsStream(File.separator + "datasets" + File.separator + query.getDb_uri());
        }
        if (resourceAsStream == null) {
            File f = new File("datasets" + File.separator + query.getDb_uri());
            if (f.exists()) {
                resourceAsStream = new FileInputStream(f);
            }
        }

        logger.info(resourceAsStream.toString());
        Path path = Paths.get("dataset.csv");
        logger.info("Path: " + path);
        Files.copy(resourceAsStream, path, StandardCopyOption.REPLACE_EXISTING);
        query.setDb_uri(path.toFile().getAbsolutePath());
        Path p = Paths.get("demo-script.txt");
        if (p.toFile().exists()) {
            p.toFile().delete();
        }
        FLGPObjectParser flgpop = new FLGPObjectParser();
        Files.write(p, flgpop.toJson(query).getBytes(), StandardOpenOption.CREATE_NEW);
        return query;

    }

}
