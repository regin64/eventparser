package eventparser;

import java.time.LocalDateTime;

public class Event {

    String customerId;
    String txId;
    EventType eventType;
    LocalDateTime timestamp;

    public Event(String customerId, String txId, EventType eventType, LocalDateTime timestamp) {
        this.customerId = customerId;
        this.txId = txId;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }
}
