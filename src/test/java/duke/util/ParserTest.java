package duke.util;

import duke.exceptions.DukeException;
import duke.exceptions.UnknownCommandException;
import duke.task.TaskList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    @Test
    public void parseTest_invalidCommand_sameOutput() throws DukeException {
        String command = "Random Command";
        TaskList tasks = new TaskList();

        try {
            Parser.parse(command).execute(tasks);
        } catch (UnknownCommandException e) {
            assertEquals("☹ OOPS!!! I'm sorry, but I don't know what that means.",
                    e.getMessage());
        }
    }

    @Test
    public void parseTest_validCommand_sameOutput() throws DukeException {
        String userInputCommand = "todo Finish Week 3 IP";
        TaskList taskList = new TaskList();
        TaskList resultTaskList = new TaskList();
        Command command = tasks -> tasks.addTodo(userInputCommand);

        assertEquals(command.execute(resultTaskList),
                Parser.parse(userInputCommand).execute(taskList));
    }
}