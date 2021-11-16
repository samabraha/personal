import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class Utilities {

    private Utilities() {

    }

    /** Calls toString() the Object parameter and sends the result to clipboard
     * @param object from which a string is to be extracted  */
    public static void copyToClipboard(Object object) {
        var selection = new StringSelection(object.toString());

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(selection, null);
    }
}
