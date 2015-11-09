package test.LogicTests;

import logic.*;
import org.junit.Before;
import org.junit.Test;
import parser.MyParser;
import storage.Storage;
import parser.ParsedCommand;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static org.junit.Assert.*;

//@@author A012477W
public class SearchTest {


    @Test
    public void testMultiSearch() throws Exception {
        Search search = new Search();
        Storage storage = new Storage();
        ParsedCommand command = MyParser.parseCommand("search 9");
        List<Task> searchList = search.multiSearch(storage.getAllTasks(), command);

        ArrayList<Task> correctList = new ArrayList<Task>();
        correctList.add(new Task("Professor Oak Assignement 4",null,9,false,new ArrayList<String>(), ParsedCommand.TaskType.FLOATING_TASK));
        boolean result = listChecker(searchList, correctList);

        assertEquals(true,result);
    }

    public boolean listChecker(List<Task> list1, List<Task> list2) {
        int index = 0;
        if (list1.size() != list2.size()) {
            System.out.println("list size different");
            System.out.println(list1.size());
            System.out.println(list2.size());
            return false;
        }
        for (Task task : list1) {
            if (list2.get(index).getId() != task.getId()) {
                return false;
            }
            index++;
        }
        return true;
    }

    @Test
    public void testSearch() throws Exception {

        Storage storage = new Storage();
        List<Task> searchList = Search.search(storage.getAllTasks(), "pikachu");

        ArrayList<Task> correctList = new ArrayList<Task>();
        correctList.add(new Task("Feed Pikachu",null,3,false,new ArrayList<String>(), ParsedCommand.TaskType.FLOATING_TASK));
        boolean result = listChecker(searchList, correctList);

        assertEquals(true,result);
    }


    @Test
    public void testSearchDate() throws Exception {

        Storage storage = new Storage();
        ParsedCommand command = MyParser.parseCommand("search 03/11/15 07/11/15");
        List<Task> searchList = Search.searchDate(storage.getAllTasks(), command.getFirstDate(), command.getSecondDate());

        ArrayList<Task> correctList = new ArrayList<Task>();
        correctList.add(new Task("important meeting with client 2",null,24,false,new ArrayList<String>(), ParsedCommand.TaskType.FLOATING_TASK));
        correctList.add(new Task("important meeting with client 3",null,25,false,new ArrayList<String>(), ParsedCommand.TaskType.FLOATING_TASK));
        correctList.add(new Task("important meeting with client 4",null,26,false,new ArrayList<String>(), ParsedCommand.TaskType.FLOATING_TASK));
        correctList.add(new Task("important meeting with client 5",null,27,false,new ArrayList<String>(), ParsedCommand.TaskType.FLOATING_TASK));


        boolean result = listChecker( searchList, correctList);

        assertEquals(true, result);
    }
}