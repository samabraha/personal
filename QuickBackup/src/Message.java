public class Message {
    private StringBuilder message;

    public Message() {
        this.message = new StringBuilder();
    }

    public Message(String message) {
        this.message = new StringBuilder(message);
    }

    public Message(String messageFormat, Object... items) {
        this.message = new StringBuilder(String.format(messageFormat, items));
    }

    public static Message create() {
        return new Message();
    }

    public static Message create(String message) {
        return new Message(message);
    }

    public void print() {
        System.out.print(message);
    }

    public void printOnSameLine() {
        printOnSameLine(999);
    }

    /** Prints message by erasing previousLineLength number of characters using \b. */
    public void printOnSameLine(int previousLineLength) {
        System.out.print("\b".repeat(previousLineLength) + message);
    }
}
