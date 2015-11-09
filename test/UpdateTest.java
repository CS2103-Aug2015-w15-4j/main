package test;

import logic.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import parser.MyParser;
import parser.ParsedCommand;
import storage.Storage;

import static org.junit.Assert.*;

//@@author A0124777W
public class UpdateTest {

    private String temp;

    @Before
    public void initialize() {
        File dataFile = new File("Data.txt");
        dataFile.renameTo(new File("temp.txt"));
        File testFile = new File("Test_Data.txt");
        testFile.renameTo(new File("Data.txt"));

    }

    @Test
    public void testExecute() throws Exception {
        Storage storage = new Storage();
        Model model = new Model(storage);
        Model correct = new Model();

        // test updating 1 field
        ParsedCommand command = MyParser.parseCommand("Edit 30 new name ");
        Update update = new Update(command,storage,model);

        update.execute();
        correct.setConsoleMessage("find firestone updated");

        boolean result = AddTest.consoleMessageChecker(correct, model);
        assertEquals(true, result);

        update.undo();

        // test updating 2 field
        // test updating 3 field
        // test updating 4 field
        // test updating 5 field
        // test updating into event
        // test updating into deadline task
        // test changing event into deadline task
    }

    @Test
    public void testUndo() throws Exception {
        // test updating 1 field
        // test updating 2 field
        // test updating 3 field
        // test updating 4 field
        // test updating 5 field
        // test updating into event
        // test updating into deadline task
        // test changing event into deadline task
    }

    @Test
    public void testUpdateTask() throws Exception {
        Task toUpdate = new Task();
        Task correct = new Task();
        correct.setId(1);
        toUpdate.setId(1);
        String title = null;
        String description = null;
        ArrayList<String> tags = null;
        Boolean isCompleted = null;
        Calendar firstDate = null;
        Calendar secondDate = null;


        // test trivial case
        Update command = new Update();
        Task updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(updated, toUpdate));

        // test updating name
        title = "short";
        correct.setName(title);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(correct, updated));
        title = "super duper long title";
        correct.setName(title);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(correct, updated));
        title = "t1t1es w1th numb3r5";
        correct.setName(title);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(correct, updated));

        // test updating description
        description = "short";
        correct.setDescription(description);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(correct, updated));
        description = "super duper long description";
        correct.setDescription(description);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(correct, updated));
        description = "d3scr1pt10n w1th numb3r5";
        correct.setDescription(description);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(correct, updated));

        // test updating tags
        tags = new ArrayList<>();
        tags.add("Test 1 tag");
        correct.setTags(tags);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(correct, updated));
        tags.add("Test 2 tags");
        correct.setTags(tags);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(correct, updated));
        tags.clear(); // test no tags
        correct.setTags(tags);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(correct, updated));

        // test updating isCompleted
        isCompleted = true;
        correct.setIsCompleted(isCompleted);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(correct, updated));
        isCompleted = false;
        correct.setIsCompleted(isCompleted);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(correct, updated));

        // test firstDate
        firstDate = Calendar.getInstance(); // test today
        DeadlineTask dlCorrect = new DeadlineTask();
        dlCorrect.setId(1);
        dlCorrect.setName(title);
        dlCorrect.setDescription(description);
        dlCorrect.setTags(tags);
        dlCorrect.setIsCompleted(isCompleted);
        dlCorrect.setEnd(firstDate);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(dlCorrect, updated));
        firstDate = Calendar.getInstance(); // test past date
        firstDate.set(1876,1,1,1,1,1);
        dlCorrect.setEnd(firstDate);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(dlCorrect, updated));
        firstDate = Calendar.getInstance(); // test future date
        firstDate.set(5000,12,30,23,59,59);
        dlCorrect.setEnd(firstDate);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(dlCorrect, updated));

        // test second date
        secondDate = Calendar.getInstance(); // test today
        Event eventCorrect = new Event();
        eventCorrect.setId(1);
        eventCorrect.setName(title);
        eventCorrect.setDescription(description);
        eventCorrect.setTags(tags);
        eventCorrect.setIsCompleted(isCompleted);
        eventCorrect.setStart(firstDate);
        eventCorrect.setEnd(secondDate);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(eventCorrect, updated));
        firstDate = Calendar.getInstance(); // test past date
        firstDate.set(1876,1,1,1,1,1);
        eventCorrect.setEnd(firstDate);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(eventCorrect, updated));
        firstDate = Calendar.getInstance(); // test future date
        firstDate.set(5000,12,30,23,59,59);
        eventCorrect.setEnd(firstDate);
        updated = command.updateTask(title,description,tags,isCompleted,firstDate,secondDate,toUpdate);
        assertEquals(true, taskChecker(eventCorrect, updated));
    }

    public boolean taskChecker(Task task1, Task task2) {
        if(task1.getId() != task2.getId()) {
            System.out.println("id not same");
            return false;
        } else {
            if (task1.getName() != null && task2.getName() !=null) {
                if (!task1.getName().equals(task2.getName())) {
                    System.out.println("name not same");
                    return false;
                }
            } else if (task1.getName() == null && task2.getName() == null) {
                // do nothing
            } else {
                System.out.println("name not same");
                return false;
            }
            if (task1.getDescription() != null && task2.getDescription() != null) {
                if (!task1.getDescription().equals(task2.getDescription())) {
                    System.out.println("description not same");
                    return false;
                }
            } else if (task1.getDescription() == null && task2.getDescription() == null) {
                // do nothing
            } else {
                System.out.println("description not same");
                return false;
            }
            if (task1.getIsCompleted() != task2.getIsCompleted()) {
                System.out.println("isCompleted not same");
                return false;
            }
            if (task1.getTags() != null || task2.getTags() != null) {
                if (task1.getTags() == null & task2.getTags() == null) {
                    // if both == null, ignore
                } else if (task1.getTags() != null && task2.getTags() != null) {
                    if (task1.getTags().size() != task2.getTags().size()) {
                        System.out.println("tags not same");
                        return false;
                    } else {
                        int i = 0;
                        for (String tag : task1.getTags()) {
                            if (!tag.equals(task2.getTags().get(i++))) {
                                System.out.println("tags not same");
                                return false;
                            }
                        }
                    }
                } else {
                    // Either task1 or task2 is null while the other isn't
                    System.out.println("tags not same");
                    return false;
                }
            }
            if (task1.getTaskType() == null && task2.getTaskType() == null) {
                // do nothing
            } else if (task1.getTaskType() != null && task2.getTaskType() != null) {
                System.out.println("taskType not same");
                return task1.getTaskType().equals(task2.getTaskType());
            }
        }
        return true;
    }

    @Test
    public void testCheckValid() throws Exception {
        // test invalid id
        ParsedCommand command = MyParser.parseCommand("Edit 500 new name");
        assertEquals(false, Update.checkValid(command, new Model()));
        // test valid id
        command = MyParser.parseCommand("Edit 1 new name");
        assertEquals(true,Update.checkValid(command,new Model()));
    }

    @After
    public void reset() {
        File dataFile = new File("Data.txt");
        dataFile.renameTo(new File("Test_Data.txt"));
        File testFile = new File("temp.txt");
        testFile.renameTo(new File("Data.txt"));
    }
}