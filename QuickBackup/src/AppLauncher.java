
import com.bulenkov.darcula.DarculaLaf;

import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

public class AppLauncher {
    public static void main(String[] args) throws UnsupportedLookAndFeelException {

//        UIManager.setLookAndFeel(new DarculaLaf());
//        MetalLookAndFeel currentLookAndFeel = (MetalLookAndFeel) UIManager.getLookAndFeel();
        MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());

        EventQueue.invokeLater(() -> {
            var frame = new MainFrame(QuickCopy.APP_NAME);
            System.out.printf("Starting %s...%n", QuickCopy.APP_NAME);
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Point screenCenter = new Point(screen.width / 2, screen.height / 2);
            var center = new Point(
                    screenCenter.x - frame.getWidth() / 2,
                    screenCenter.y - frame.getHeight() / 2
            );
            frame.setLocation(center);
            frame.setVisible(true);
        });
    }
}
