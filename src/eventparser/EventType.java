package eventparser;

public enum EventType {
    INGEST("ingest"),
    INGEST_WITH_PROXY("ingest-with-proxy");

    private String text;

    EventType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static EventType fromString(String text) {
        for (EventType eventType : EventType.values()) {
            if (eventType.text.equalsIgnoreCase(text)) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("text not valid");
    }
}
