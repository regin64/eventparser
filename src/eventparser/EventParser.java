package eventparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EventParser {

    private static final String COMMA_DELIMITER = ",";
    private static final int PARALLELIZATION_FACTOR = 10;

    private final ExecutorService executorService;

    public EventParser() {
        this.executorService = Executors.newFixedThreadPool(PARALLELIZATION_FACTOR);
    }

    public Map<LocalDateTime, List<Event>> parseEventsFromCsv(String filePath) {

        Map<LocalDateTime, List<Event>> map = new HashMap<>();

        try (Scanner scanner = new Scanner(new File(filePath));) {
            while (scanner.hasNextLine()) {
                // TODO: validate the event input
                // TODO: run parallel tasks for generating event objects, need to deal with map.put(window, new LinkedList<>()) part to avoid duplicate lists generated
                Event event = generateEventFromLine(scanner.nextLine());
                LocalDateTime window = event.timestamp.truncatedTo(ChronoUnit.HOURS);
                if (!map.containsKey(window)) {
                    map.put(window, new LinkedList<>());
                }
                map.get(window).add(event);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    private Event generateEventFromLine(String line) {
        List<String> arguments = new LinkedList<>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            while (rowScanner.hasNext()) {
                arguments.add(rowScanner.next());
            }
        }

        // e.g. 2021-03-03 20:14:43.34
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new Event(arguments.get(0), arguments.get(2), EventType.fromString(arguments.get(1)),
                LocalDateTime.parse(arguments.get(3).substring(0, 19), formatter));
    }

    public List<String> getEventCountByTime(Map<LocalDateTime, List<Event>> map, LocalDateTime startTime, LocalDateTime endTime) {

        List<String> result = null;

        LocalDateTime firstWindow = startTime.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime lastWindow = endTime.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime currentWindow = firstWindow;

        List<LocalDateTime> windows = new LinkedList<>();
        while (currentWindow.isBefore(endTime)) {
            windows.add(currentWindow);
            // LocalDateTime objects are immutable
            currentWindow = currentWindow.plusHours(1);
        }

        try {
            result = executorService.submit(() -> windows.parallelStream()
                    .map(window -> getCountByWindow(map, window, firstWindow, lastWindow, startTime, endTime))
                    .toList()).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        // TODO: upgrade to Java 19 to implement try-with-resources on ExecutorService as it implements AutoCloseable
        executorService.shutdownNow();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return result;
    }

    private String getCountByWindow(Map<LocalDateTime, List<Event>> map, LocalDateTime currentWindow, LocalDateTime firstWindow,
                                    LocalDateTime lastWindow, LocalDateTime startTime, LocalDateTime endTime) {
        int count = 0;
        if (currentWindow.isEqual(firstWindow)) {
            count = (int) map.get(currentWindow).stream().filter(event -> event.timestamp.isAfter(startTime)).count();
            return currentWindow + " bucket (after " + startTime + ") -> " + count;
        }
        if (currentWindow.isEqual(lastWindow)) {
            count = (int) map.get(currentWindow).stream().filter(event -> event.timestamp.isBefore(endTime)).count();
            return currentWindow + " bucket (before " + endTime + ") -> " + count;
        }
        return currentWindow + " bucket -> " + map.get(currentWindow).size();
    }
}
