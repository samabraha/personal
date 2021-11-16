import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class Utilities {
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    private Utilities() {

    }

    /** Calls toString() on the Object parameter and sends the result to clipboard
     * @param object from which a string is to be extracted  */
    public static void copyToClipboard(Object object) {
        var selection = new StringSelection(object.toString());
        clipboard.setContents(selection, null);
    }
}
