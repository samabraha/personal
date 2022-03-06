import java.io.PrintStream;

public class Message {
    private static final PrintStream printStream = System.out;
    private final StringBuilder messageString;

    public Message() {
        this.messageString = new StringBuilder();
    }

    public Message(String message) {
        this.messageString = new StringBuilder(message);
    }

    public Message(String messageFormat, Object... items) {
        this.messageString = new StringBuilder(String.format(messageFormat, items));
    }

    public static Message create() {
        return new Message();
    }

    public static Message create(String message) {
        return new Message(message);
    }

    /** Prints a message without then adding a newline character */
    public void print() {
        printStream.println(messageString);
    }

    public Message newLine() {
        messageString.append("%n");
        return this;
    }

    public Message newLine(String string) {
        messageString.append("\n").append(string);
        return this;
    }

    public void printOnSameLine() {
        printOnSameLine(999);
    }

    /** Prints message by erasing previousLineLength number of characters using \b. */
    public void printOnSameLine(int previousLineLength) {
        printStream.print("\b".repeat(previousLineLength) + messageString);
    }


}
