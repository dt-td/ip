import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Encapsulates a TaskList object.
 *
 * @author Dickson
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructors for TaskList.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    private void checkCommandFormat(String command, String regex, String correctFormat) throws IllegalFormatException{
        if (!command.matches(regex)) {
            throw new IllegalFormatException(correctFormat);
        }
    }

    public String addTodo(String command) throws IllegalFormatException {
        String regexToMatch = "^todo .*";
        String correctFormat = "todo <todo description>";
        checkCommandFormat(command, regexToMatch, correctFormat);

        Task task = new ToDo(command.substring(5).trim());
        tasks.add(task);

        return addMessage(task, tasks.size());
    }

    public String addEvent(String command) throws IllegalFormatException {
        String regexToMatch = "^event .* /at \\d{2}/\\d{2}/\\d{2} \\d{4}-\\d{4}";
        String correctFormat = "event <event description> /at <dd/MM/yy> <HHmm>-<HHmm>";
        checkCommandFormat(command, regexToMatch, correctFormat);

        String[] eventInfo = command.substring(6).split("/at ");
        String eventDescription = eventInfo[0];
        String dateString = eventInfo[1].substring(0,8);
        String startTimeString = eventInfo[1].substring(9,13);
        String endTimeString = eventInfo[1].substring(14,18);

        LocalDate date;
        LocalTime startTime, endTime;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

        try {
            date = LocalDate.parse(dateString, dateFormatter);
            startTime = LocalTime.parse(startTimeString, timeFormatter);
            endTime = LocalTime.parse(endTimeString, timeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalFormatException(correctFormat);
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalFormatException(correctFormat);
        }

        Task task = new Event(eventDescription, date, startTime, endTime);
        tasks.add(task);

        return addMessage(task, tasks.size());
    }

    public String addDeadline(String command) throws IllegalFormatException {
        String regexToMatch = "^deadline .* /by \\d{2}/\\d{2}/\\d{2} \\d{4}";
        String correctFormat = "deadline <deadline description> /by <dd/MM/yy> <HHmm>";
        checkCommandFormat(command, regexToMatch, correctFormat);

        String[] eventInfo = command.substring(9).split("/by ");
        String eventDescription = eventInfo[0];
        String dateTimeString = eventInfo[1].trim();

        LocalDateTime dateTime;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HHmm");

        try {
            dateTime = LocalDateTime.parse(dateTimeString, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalFormatException(correctFormat);
        }

        Task task = new Deadline(eventDescription, dateTime);
        tasks.add(task);

        return addMessage(task, tasks.size());
    }

    public String markTaskDone(String command) throws TaskNotFoundException, IllegalFormatException {
        String regexToMatch = "^done [0-9].*";
        String correctFormat = "done <task index>";
        checkCommandFormat(command, regexToMatch, correctFormat);

        int taskIndex;

        try {
            taskIndex = Integer.parseInt(command.substring(5)) - 1;
        } catch (NumberFormatException e) {
            throw new TaskNotFoundException();
        }

        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new TaskNotFoundException();
        }

        Task task = tasks.get(taskIndex);
        task.markDone();

        return doneMessage(task);
    }

    public String deleteTask(String command) throws TaskNotFoundException, IllegalFormatException {
        String regexToMatch = "^delete [0-9].*";
        String correctFormat = "delete <task index>";
        checkCommandFormat(command, regexToMatch, correctFormat);

        int taskIndex;

        try {
            taskIndex = Integer.parseInt(command.substring(7)) - 1;
        } catch (NumberFormatException e) {
            throw new TaskNotFoundException();
        }

        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new TaskNotFoundException();
        }

        Task task = tasks.remove(taskIndex);

        int size = tasks.size();

        return deleteMessage(task, size);
    }

    private String addMessage(Task task, int size) {
        return "Got it. I've added this task:\n" + task +
                "\nNow you have " + size +
                (size <= 1
                        ? " task "
                        : " tasks " + "in your list.");
    }

    private String deleteMessage(Task task, int size) {
        return "Noted. I've removed the following task:\n" + task +
                "\nNow you have " + size +
                (size <= 1
                        ? " task "
                        : " tasks " + "in your list.");
    }

    private String doneMessage(Task task) {
        return "Nice! I've marked this task as done:\n" + task;
    }

    public String printTaskList() throws EmptyListException {
        String str = "";
        int size = tasks.size();

        if (size == 0) {
            throw new EmptyListException();
        }

        for (int i = 0; i < size; i++) {
            str = str.concat(tasks.get(i).toString());
            if (i < tasks.size() - 1) {
                str = str.concat("\n");
            }
        }
        return str;
    }
}
