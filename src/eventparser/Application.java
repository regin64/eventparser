package eventparser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws ParserException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Scanner input = new Scanner(System.in);
        System.out.println("Enter start time in format yyyy-MM-dd HH:mm:ss: ");
        String startTimeInput = input.nextLine();
        System.out.println("Enter start time in format yyyy-MM-dd HH:mm:ss: " );
        String endTimeInput = input.nextLine();

        validateInput(startTimeInput, endTimeInput, formatter);
        LocalDateTime start = LocalDateTime.parse(startTimeInput, formatter);
        LocalDateTime end = LocalDateTime.parse(endTimeInput, formatter);

        EventParser eventParser = new EventParser();
        Map<LocalDateTime, List<Event>> map = eventParser.parseEventsFromCsv("resources/events.csv");

        for (String s : eventParser.getEventCountByTime(map, start, end)) {
            System.out.println(s);
        }
    }

    private static void validateInput(String startTimeInput, String endTimeInput, DateTimeFormatter formatter) throws ParserException {
        try {
            LocalDateTime start = LocalDateTime.parse(startTimeInput, formatter);
            LocalDateTime end = LocalDateTime.parse(endTimeInput, formatter);
            if (start.isAfter(end)) {
                throw new ParserException("start time input is later than end time input");
            }
        } catch (DateTimeParseException dtpe) {
            throw new ParserException("input is invalid: " + dtpe.getMessage());
        }
    }
}