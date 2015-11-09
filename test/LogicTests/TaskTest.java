package test.LogicTests;

import logic.DeadlineTask;
import logic.Event;
import org.junit.Test;
import org.junit.Before;
import logic.Task;
import parser.MyParser;
import parser.ParsedCommand;

import java.util.ArrayList;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Collections;

//@@author A0124777W
public class TaskTest {

    public static final String TEST_DESCRIPTION_1 = "TestDescription1";
    public static final String TEST_DESCRIPTION_2 = "TestDescription2";
    public static final String TEST_DESCRIPTION_3 = "TestDescription3";

    public static final String TEST_NAME_1 = "TestName1";
    public static final String TEST_NAME_2 = "TestName2";
    public static final String TEST_NAME_3 = "TestName3";

    public static final int TEST_ID_1 = 1;
    public static final int TEST_ID_2 = 2;
    public static final int TEST_ID_3 = 3;

    public ArrayList<Task> list = new ArrayList<Task>();
    public ArrayList<String> tags = new ArrayList<String>();
    public Task task1 ;
    public Task task2 ;
    public Task task3 ;
    public Task task4 ;
    public Task task5 ;


    @Before
    public void initialize() throws Exception {
        tags.add("TestTag1");
        tags.add("TestTag2");
        tags.add("TestTag3");

        Calendar endDate = Calendar.getInstance();
        endDate.set(2000,10,10);
        task1 = new Task(TEST_NAME_1, TEST_DESCRIPTION_1, TEST_ID_1, true, tags, ParsedCommand.TaskType.FLOATING_TASK);
        task2 = new DeadlineTask(TEST_NAME_2, TEST_DESCRIPTION_2, TEST_ID_2, false, tags, ParsedCommand.TaskType.DEADLINE_TASK, endDate);
        endDate.set(1990,10,10);
        task3 = new Event(TEST_NAME_3, TEST_DESCRIPTION_3, TEST_ID_3, true, tags, ParsedCommand.TaskType.EVENT, Calendar.getInstance(), endDate);
        endDate.set(1990,10,10);
        task4 = new Event(TEST_NAME_3, TEST_DESCRIPTION_3, TEST_ID_3, true, tags, ParsedCommand.TaskType.EVENT, Calendar.getInstance(), endDate);
        endDate.set(2005,10,10);
        task5 = new DeadlineTask(TEST_NAME_2, TEST_DESCRIPTION_2, TEST_ID_2, false, tags, ParsedCommand.TaskType.DEADLINE_TASK, endDate);

        list.add(task1);
        list.add(task2);
        list.add(task3);
        list.add(task4);
        list.add(task5);
    }

    @Test
    public void testTaskDetails() throws Exception {

        Task task = new Task("Test Setter Name", "Test Setter description", 50, true, tags, ParsedCommand.TaskType.FLOATING_TASK);
        Task task2 = new Task(MyParser.parseCommand("Add Task"));
        Task task3 = new Task(task);

        int i = 0;
        assertEquals(task.getTaskDetails().get(i++)[0],Task.FIELD_NAME);
        assertEquals(task.getTaskDetails().get(i++)[0], Task.FIELD_ID);
        assertEquals(task.getTaskDetails().get(i++)[0], Task.FIELD_DETAILS);
        assertEquals(task.getTaskDetails().get(i++)[0], Task.FIELD_TAGS);

        i = 0;
        assertEquals(task.getTaskDetails().get(i++)[1],"Test Setter Name");
        assertEquals(task.getTaskDetails().get(i++)[1], "" + 50);
        assertEquals(task.getTaskDetails().get(i++)[1], "Test Setter description");
        assertEquals(task.getTaskDetails().get(i++)[1],String.format("[%1$s][%2$s][%3$s]", tags.get(0), tags.get(1), tags.get(2)));
    }

    @Test
    public void testGetName() throws Exception {
        Task setNameTest1 = task1;
        setNameTest1.setName("Test");
        Task setNameTest2 = task2;
        setNameTest2.setName("Set");
        Task setNameTest3 = task3;
        setNameTest3.setName("Long Name");

        assertEquals(setNameTest1.getName(), "Test");
        assertEquals(setNameTest2.getName(), "Set");
        assertEquals(setNameTest3.getName(), "Long Name");
    }

    @Test
    public void testGetDetails() throws Exception {
        Task setNameTest1 = new Task();
        setNameTest1.setDescription("Test");
        Task setNameTest2 = new Task();
        setNameTest2.setDescription("Set");
        Task setNameTest3 = new Task();
        setNameTest3.setDescription("Description");

        assertEquals(setNameTest1.getDescription(), "Test");
        assertEquals(setNameTest2.getDescription(), "Set");
        assertEquals(setNameTest3.getDescription(), "Description");
    }

    @Test
    public void testGetId() throws Exception {
        Task setNameTest1 = new Task();
        setNameTest1.setId(1);
        Task setNameTest2 = new Task();
        setNameTest2.setId(0);
        Task setNameTest3 = new Task();
        setNameTest3.setId(-1);

        assertEquals(setNameTest1.getId(), 1);
        assertEquals(setNameTest2.getId(), 0);
        assertEquals(setNameTest3.getId(), -1);
    }

    @Test
    public void testGetIsCompleted() throws Exception {
        Task setCompletedTest = new Task();
        setCompletedTest.setIsCompleted(false);

        assertEquals(task1.getIsCompleted(), true);
        assertEquals(task2.getIsCompleted(), false);
        assertEquals(setCompletedTest.getIsCompleted(), false);
    }

    @Test
    public void testGetTags() throws Exception {

        String result = "";
        Task tagTest = task1;

        for(String tag : tagTest.getTags()) {
            result += "[" + tag + "]";
        }


        assertEquals(result, String.format("[%1$s][%2$s][%3$s]", tags.get(0), tags.get(1), tags.get(2)));

    }

    @Test
    public void testGetTaskType() throws Exception {

        assertEquals(task1.getTaskType(), ParsedCommand.TaskType.FLOATING_TASK);
        assertEquals(task2.getTaskType(), ParsedCommand.TaskType.DEADLINE_TASK);
        assertEquals(task3.getTaskType(), ParsedCommand.TaskType.EVENT);
    }

    @Test
    public void testCompareTo() throws Exception {
        Collections.sort(list,Task.compareByDate);

        assertEquals(0, list.get(2).compareTo(task3));
        assertEquals(0, list.get(3).compareTo(task4));
        assertEquals(0, list.get(1).compareTo(task2));
        assertEquals(0, list.get(4).compareTo(task5));
        assertEquals(0, list.get(0).compareTo(task1));
    }
}