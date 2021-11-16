import java.io.OutputStream;
import java.io.PrintWriter;

public class Message {
    private final PrintWriter printWriter = new PrintWriter(OutputStream.nullOutputStream());
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
        printWriter.print(messageString);
    }


    public void printOnSameLine() {
        printOnSameLine(999);
    }

    /** Prints message by erasing previousLineLength number of characters using \b. */
    public void printOnSameLine(int previousLineLength) {
        printWriter.print("\b".repeat(previousLineLength) + messageString);
    }
}
