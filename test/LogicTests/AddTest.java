package test.LogicTests;

import logic.Add;
import logic.Delete;
import logic.Logic;
import logic.Model;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import parser.MyParser;
import parser.ParsedCommand;
import storage.Storage;

import java.io.File;

import static org.junit.Assert.*;

//@@author A0124777W
public class AddTest {


    @Before
    public void initialize() {
        File dataFile = new File("Data.txt");
        dataFile.renameTo(new File("temp.txt"));
        File testFile = new File("Test_Data2.txt");
        testFile.renameTo(new File("Data.txt"));

    }

    @After
    public void reset() {
        File dataFile = new File("Data.txt");
        dataFile.renameTo(new File("Test_Data2.txt"));
        File testFile = new File("temp.txt");
        testFile.renameTo(new File("Data.txt"));
    }


    @Test
    public void testExecute() throws Exception {
        Storage storage = new Storage();
        Model model = new Model(storage);
        Model correct = new Model(storage);

        // Test add task
        ParsedCommand specifications = MyParser.parseCommand("add test task");

        Add add = new Add(specifications,storage,model);
        add.execute();
        correct.setConsoleMessage("test task added");

        boolean result = consoleMessageChecker(correct,model);
        assertEquals(true,result);

        add.undo();
        result = storage.getAllTasks().size() == 0;

        assertEquals(true,result);


        // Test add task with description
        specifications = MyParser.parseCommand("add test task \"description test\" ");

        add = new Add(specifications,storage,model);
        add.execute();
        correct.setConsoleMessage("test task added");

        result = consoleMessageChecker(correct,model);
        assertEquals(true,result);

        add.undo();
        result = storage.getAllTasks().size() == 0;

        assertEquals(true,result);


        // Test add task with tags
         specifications = MyParser.parseCommand("add test task #CheckTags");

         add = new Add(specifications,storage,model);
        add.execute();
        correct.setConsoleMessage("test task added");

         result = consoleMessageChecker(correct,model);
        assertEquals(true,result);

        add.undo();
        result = storage.getAllTasks().size() == 0;

        assertEquals(true,result);


        // Test add DeadlineTask
        specifications = MyParser.parseCommand("add test deadlinetask 20 november 1990");

        add = new Add(specifications,storage,model);
        add.execute();
        correct.setConsoleMessage("test deadlinetask added");

        result = consoleMessageChecker(correct, model);
        assertEquals(true,result);

        add.undo();
        result = storage.getAllTasks().size() == 0;
        assertEquals(true,result);

        // Test add task with description
        specifications = MyParser.parseCommand("add test deadlinetask 20 november 1990 \"description test\"  ");

        add = new Add(specifications,storage,model);
        add.execute();
        correct.setConsoleMessage("test deadlinetask added");

        result = consoleMessageChecker(correct,model);
        assertEquals(true,result);

        add.undo();
        result = storage.getAllTasks().size() == 0;

        assertEquals(true,result);

        // Test add Event
        specifications = MyParser.parseCommand("add test event 20 november 1990 to 20 december 1990");

        add = new Add(specifications,storage,model);
        add.execute();
        correct.setConsoleMessage("test event added");

        add.undo();
        result = storage.getAllTasks().size() == 0;
        assertEquals(true, result);

        // Test add Event with description
        specifications = MyParser.parseCommand("add test event 20 november 1990 to 20 december 1990 \"test description\"");

        add = new Add(specifications,storage,model);
        add.execute();
        correct.setConsoleMessage("test event added");

        add.undo();
        result = storage.getAllTasks().size() == 0;
        assertEquals(true, result);
    }

    @Test
    public void testUndo() throws Exception {
        Storage storage = new Storage();
        Model model = new Model(storage);
        Model correct = new Model(storage);
        // Test undo task
        ParsedCommand specifications = MyParser.parseCommand("add test undo");

        Add add = new Add(specifications,storage,model);
        add.execute();
        correct.setConsoleMessage("test undo added");

        boolean result = consoleMessageChecker(correct, model);
        assertEquals(true,result);

        add.undo();
        result = storage.getAllTasks().size() == 0;
        assertEquals(true,result);

        // Test undo DeadlineTask
        specifications = MyParser.parseCommand("add test deadlinetask 15 november 3001");

        add = new Add(specifications,storage,model);
        add.execute();
        correct.setConsoleMessage("test deadlinetask added");

        result = consoleMessageChecker(correct,model);
        assertEquals(true,result);

        add.undo();
        result = storage.getAllTasks().size() == 0;
        assertEquals(true,result);

        // Test undo Event
        specifications = MyParser.parseCommand("add test event 1 november 3000 to 30 december 3000");

        add = new Add(specifications,storage,model);
        add.execute();
        correct.setConsoleMessage("test event added");

        result = consoleMessageChecker(correct,model);
        assertEquals(true,result);

        add.undo();
        result = storage.getAllTasks().size() == 0;
        assertEquals(true,result);
    }

    public static boolean consoleMessageChecker(Model model1, Model model2) {
        if (!model1.getConsoleMessage().equals(model2.getConsoleMessage())) {
            System.out.println("ConsoleMessage different");
            return false;
        }
        return true;
    }

}