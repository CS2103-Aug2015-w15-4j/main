package test.LogicTests;

import logic.*;
import org.junit.Test;
import parser.MyParser;
import parser.ParsedCommand;
import storage.Storage;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

//@@author A0124777W
public class LogicTest {

    @Test
    public void testExecuteCommand() throws Exception {
        Storage storage = new Storage();
        Model correct = new Model(storage);
        Logic logic = new Logic(storage);
        Model executed;
        boolean result;

        // test empty command
        executed = logic.executeCommand(null);
        correct.updateModel(logic.MESSAGE_INVALID_FORMAT);
        result = modelChecker(correct, executed);
        assertEquals(true, result);

        // test add Task
        ParsedCommand command = MyParser.parseCommand("add task");
        executed = logic.executeCommand(command);
        correct.updateModel("task added");
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test add DeadlineTask
        command = MyParser.parseCommand("add DeadlineTask 27/5/2000");
        logic.executeCommand(command);
        correct.updateModel("DeadlineTask added");
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test add Event
        command = MyParser.parseCommand("add Event 27/5/2000 28/5/2000");
        logic.executeCommand(command);
        correct.updateModel("Event added");
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test delete valid
        command = MyParser.parseCommand("delete 1");
        logic.executeCommand(command);
        correct.updateModel("Task 1 deleted");
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test delete invalid
        command = MyParser.parseCommand("delete 500");
        logic.executeCommand(command);
        correct.updateModel(Logic.ERROR_INVALID_ID);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test update invalid
        command = MyParser.parseCommand("Edit 500 New name ");
        logic.executeCommand(command);
        correct.updateModel(Logic.ERROR_INVALID_ID);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test update valid
        command = MyParser.parseCommand("Edit 1 new name");
        logic.executeCommand(command);
        correct.updateModel(Logic.ERROR_INVALID_ID);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test flag done Task
        command = MyParser.parseCommand("flag done 2");
        logic.executeCommand(command);
        correct.updateModel("DeadlineTask updated");
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test flag done invalid task
        command = MyParser.parseCommand("flag done 500");
        logic.executeCommand(command);
        correct.updateModel(Logic.ERROR_INVALID_ID);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test flag to do Task
        command = MyParser.parseCommand("flag todo 2");
        logic.executeCommand(command);
        correct.updateModel("DeadlineTask updated");
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test flag to do invalid task
        command = MyParser.parseCommand("flag todo 500");
        logic.executeCommand(command);
        correct.updateModel(Logic.ERROR_INVALID_ID);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test invalid command
        command = MyParser.parseCommand("this is an invalid command");
        logic.executeCommand(command);
        correct.updateModel(command.getErrorMessage());
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test search
        command = MyParser.parseCommand("search pikachu");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_NO_RESULTS_FOUND);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // test display
        command = MyParser.parseCommand("show 2");
        logic.executeCommand(command);
        Search search = new Search();
        correct.updateSearch(Logic.MESSAGE_1_RESULT_FOUND, command, search.multiSearch(storage.getAllTasks(), command));
        result = modelChecker(correct,executed);
        assertEquals(true,result);

//        // test set folder
//        command = MyParser.parseCommand("set folder /");
//        logic.executeCommand(command);
//        result = modelChecker(correct,executed);
//        assertEquals(true,result);

//        // test set avatar
//        command = MyParser.parseCommand("set avatar avatar.jpg");
//        logic.executeCommand(command);
//        correct.updateModel("Avatar switched");
//        result = modelChecker(correct,executed);
//        assertEquals(true,result);

        // test undo
        command = MyParser.parseCommand("undo number 1");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_UNDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        command = MyParser.parseCommand("undo number 2");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_UNDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        command = MyParser.parseCommand("undo number 3");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_UNDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        command = MyParser.parseCommand("undo number 4");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_UNDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        command = MyParser.parseCommand("undo number 5");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_UNDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        command = MyParser.parseCommand("undo number 6");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_UNDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // Test nothing to undo
        command = MyParser.parseCommand("undo number 7");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_NOTHING_TO_UNDO);
        result = modelChecker(correct,executed);
        assertEquals(true,result);



        // test redo
        command = MyParser.parseCommand("redo number 1");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_REDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        command = MyParser.parseCommand("redo number 2");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_REDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        command = MyParser.parseCommand("redo number 3");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_REDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        command = MyParser.parseCommand("redo number 4");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_REDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        command = MyParser.parseCommand("redo number 5");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_REDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        command = MyParser.parseCommand("redo number 6");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_REDO_SUCCESSFUL);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

        // Test nothing to redo
        command = MyParser.parseCommand("redo number 7");
        logic.executeCommand(command);
        correct.updateModel(Logic.MESSAGE_NOTHING_TO_REDO);
        result = modelChecker(correct,executed);
        assertEquals(true,result);

    }

    @Test
    public void testCheckID() throws Exception {
        assertEquals(true,Logic.checkID(2));
        //TODO test if deleted id is valid
        assertEquals(false,Logic.checkID(500));
    }

    /*
     *  Compares two models and check if their fields are the same
     */
    public static boolean modelChecker(Model model1, Model model2) {
        boolean isSame = true;
        if (!model1.getConsoleMessage().equals(model2.getConsoleMessage())) {
            System.out.println("ConsoleMessage different");
            return false;
        }
        if (!model1.getAvatarLocation().equals(model2.getAvatarLocation())) {
            System.out.println("Avatar location different");
            return false;
        }
        if (model1.getAllTasks().size() != model2.getAllTasks().size()) {
            System.out.println("AlltasksList different");
            return false;
        } else {
            int i = 0;
            for(Task task : model1.getAllTasks()) {
                if (task.compareTo(model2.getAllTasks().get(i++)) != 0) {
                    System.out.println("AlltasksList different");
                    return false;
                }
            }
        }
        if (model1.getSearchList().size() != model2.getSearchList().size()) {
            System.out.println("searchList different");
            return false;
        } else {
            int i = 0;
            for(Task task : model1.getSearchList()) {
                if (task.compareTo(model2.getSearchList().get(i++)) != 0) {
                    System.out.println("searchList different");
                    return false;
                }
            }
        }
        if (model1.getFloatingList().size() != model2.getFloatingList().size()) {
            System.out.println("floatingList different");
            return false;
        } else {
            int i = 0;
            for(Task task : model1.getFloatingList()) {
                if (task.compareTo(model2.getFloatingList().get(i++)) != 0) {
                    System.out.println("floatingList different");
                    return false;
                }
            }
        }
        if (model1.getMainList().size() != model2.getMainList().size()) {
            System.out.println("mainList different");
            return false;
        } else {
            int i = 0;
            for(Task task : model1.getMainList()) {
                if (task.compareTo(model2.getMainList().get(i++)) != 0) {
                    System.out.println("mainList different");
                    return false;
                }
            }
        }
        if (model1.getTodayList().size() != model2.getTodayList().size()) {
            System.out.println("todayList different");
            return false;
        } else {
            int i = 0;
            for(Task task : model1.getTodayList()) {
                if (task.compareTo(model2.getTodayList().get(i++)) != 0) {
                    System.out.println("todayList different");
                    return false;
                }
            }
        }
        if (model1.getOverdueList().size() != model2.getOverdueList().size()) {
            System.out.println("overdueList different");
            return false;
        } else {
            int i = 0;
            for(Task task : model1.getOverdueList()) {
                if (task.compareTo(model2.getOverdueList().get(i++)) != 0) {
                    System.out.println("overdueList different");
                    return false;
                }
            }
        }

        return isSame;
    }
}