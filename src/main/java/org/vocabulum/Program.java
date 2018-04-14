package org.vocabulum;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;

import org.vocabulum.data.Relation;
import org.vocabulum.data.Unit;
import org.vocabulum.parser.VokParser;
import org.vocabulum.persist.PersistDriver;
import org.vocabulum.persist.PersistFactory;
import org.vocabulum.question.PlainTextQuestioner;
import org.vocabulum.report.PlainTextReporter;


public class Program {

    private class AddUnit {
        @Parameter(names = { "--vok", "-v" }, description = "Path to vok file", required = true, variableArity = true)
        private List<String> vokFiles = new ArrayList<>();

        @Parameter(names = { "--name", "-n" }, description = "Unit name", required = true)
        private String unitName = null;

        @Parameter(names = { "--source", "-s" }, description = "Path to source", required = true)
        private String source = null;
    
        @Parameter(names = { "--driver", "-d" }, description = "Source driver", required = true)
        private String driverName = null;

        public AddUnit(String[] args) throws Exception {
            JCommander.newBuilder()
                .addObject(this)
                .build()
                .parse(args);
    
            PersistDriver driver = null;
            if ("sqlite".equals(driverName)) {
                driver = PersistFactory.createSqlite(source);
            }

            VokParser parser = new VokParser();

            for (String vokFile : vokFiles) {
                FileReader fReader = new FileReader(new File(vokFile));
                List<Relation> rs = parser.parse(new BufferedReader(fReader));
            
                driver.storeUnit(new Unit(unitName, rs)); 
            } 
        }
    }

    private class Tester {
        @Parameter(names = { "--random", "-r" }, description = "Pick random words")
        private boolean random = false;
    
        @Parameter(names = { "--quantity", "-q" }, description = "Quantity of words")
        private int quantity = 20;
    
        @Parameter(names = { "--source", "-s" }, description = "Path to source", required = true)
        private String source = null;
    
        @Parameter(names = { "--driver", "-d" }, description = "Source driver", required = true)
        private String driverName = null;

        public Tester(String[] args) throws Exception {
            JCommander.newBuilder()
                .addObject(this)
                .build()
                .parse(args);
    
            PersistDriver driver = null;
            if ("sqlite".equals(driverName)) {
                driver = PersistFactory.createSqlite(source);
            }
    
            List<Relation> relations = new ArrayList<Relation>();
            if (random == true) {
                for (String unitName : driver.getUnitNames()) {
                    relations.addAll(driver
                            .retrieveUnitByName(unitName)
                            .getRelations());
                }
    
                Collections.shuffle(relations);
                relations = relations.subList(0, quantity);
            } 
    
            PlainTextReporter reporter = new PlainTextReporter();
            PlainTextQuestioner questioner = new PlainTextQuestioner();
            questioner
                .addRelations(relations)
                .registerReporter(reporter);
    
            Scanner input = new Scanner(System.in);
            while (questioner.hasNext()) {
                System.out.println(questioner.next() + "?");
                System.out.print("> ");
                System.out.flush();
                if (questioner.answer(input.nextLine())) {
                    System.out.println("Right!");
                } else {
                    System.out.println("WRONG!");
                    System.out.println(questioner.getCorrectAnswer());
                }
                System.out.println("");
            }

            System.out.println("\n" + reporter.getReport() + "\n");
        }
    }

    private Program(String cmd, String[] args) throws Exception {
        if (cmd.equals("test")) {
            new Tester(args);
        } else if (cmd.equals("addunit")) {
            new AddUnit(args);
        } 
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 1) {
            String cmd = args[0];
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);

            new Program(cmd, newArgs);
        }
    }
}
