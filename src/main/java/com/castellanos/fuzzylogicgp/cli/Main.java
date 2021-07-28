package com.castellanos.fuzzylogicgp.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import com.castellanos.fuzzylogicgp.base.OperatorException;
import com.castellanos.fuzzylogicgp.base.Utils;
import com.castellanos.fuzzylogicgp.examples.Examples;
import com.castellanos.fuzzylogicgp.parser.EDNParser;
import com.castellanos.fuzzylogicgp.parser.Query;
import com.castellanos.fuzzylogicgp.parser.TaskFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import static java.lang.System.out;

@Command(name = "FLJF", description = "@|bold Demonstrating FLJF |@", headerHeading = "@|bold,underline Demonstration Usage|@:%n%n")
public class Main {

    @Option(names = { "-f", "--file" }, description = "Path and name of file")
    private String fileName;
    @Option(names = { "--seed" }, description = "Seed random")
    private Long seed;

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
    @Option(names = { "--EDN" }, description = "Supported EDN script.")
    private boolean formatEdn;
    @Option(names = { "--N" }, description = "No run task.")
    private boolean executeTask;

    public static void main(String[] args)
            throws OperatorException, CloneNotSupportedException, IOException, URISyntaxException {
        final Main main = CommandLine.populateCommand(new Main(), args);

        if (main.help) {
            CommandLine.usage(main, out, CommandLine.Help.Ansi.AUTO);
        } else {
            Query query = null;
            if (main.seed != null) {
                Utils.random.setSeed(main.seed);
            }
            if (main.fileName != null) {

                if (main.formatEdn) {
                    EDNParser ednParser = new EDNParser(main.fileName);
                    query = ednParser.parser();
                } else {
                    query = Query.fromJson(Paths.get(main.fileName));
                }
                if (!main.executeTask)
                    TaskFactory.execute(query);

                if (main.plot != null && main.plot.size() > 0) {
                    TaskFactory.plotting(query, main.plot);
                }
            }
            if (main.evaluationDemo) {
                out.println("Running demo evaluation");
                query = Examples.evaluation();
                TaskFactory.execute(demoToFile(query));

                if (main.plot != null && main.plot.size() > 0) {
                    TaskFactory.plotting(query, main.plot);
                }
            } else if (main.discoveryDemo) {
                out.println("Running demo discovery");
                query = Examples.discovery();
                TaskFactory.execute(demoToFile(query));
            } else if (main.irisDemo) {
                out.println("Running irs demo");
                query = Examples.irisQuery();
                TaskFactory.execute(demoToFile(query));
            }

        }
    }

    private static Query demoToFile(Query query) throws IOException {
        InputStream resourceAsStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream("datasets" + File.separator + query.getDb_uri());
            System.out.println("Relative path: "+"datasets" + File.separator + query.getDb_uri());
            if(resourceAsStream== null){
                resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream(File.separator+"datasets" + File.separator + query.getDb_uri());
            }
            if(resourceAsStream== null){
                resourceAsStream = Main.class.getResourceAsStream(File.separator+"datasets" + File.separator + query.getDb_uri());//ClassLoader.getSystemClassLoader().getResourceAsStream(File.separator+"datasets" + File.separator + query.getDb_uri());
            }
            if(resourceAsStream==null){
                resourceAsStream = Main.class.getResourceAsStream("datasets" + File.separator + query.getDb_uri());//ClassLoader.getSystemClassLoader().getResourceAsStream(File.separator+"datasets" + File.separator + query.getDb_uri());
            }
            if(resourceAsStream==null){
                resourceAsStream = Main.class.getClass().getResourceAsStream("datasets" + File.separator + query.getDb_uri());
            }
            if(resourceAsStream==null){
                resourceAsStream = Main.class.getClass().getResourceAsStream(File.separator+"datasets" + File.separator + query.getDb_uri());
            }
            
        System.out.println(resourceAsStream.toString());
        Path path = Paths.get("dataset.csv");
        System.out.println("Path: "+path);
        Files.copy(resourceAsStream, path, StandardCopyOption.REPLACE_EXISTING);
        query.setDb_uri(path.toFile().getAbsolutePath());
        Path p = Paths.get("demo-script.txt");
        if (p.toFile().exists())
            p.toFile().delete();
        Files.write(p, query.toJSON().getBytes(), StandardOpenOption.CREATE_NEW);
        return query;

    }

}