import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class MainFrame extends JFrame {
    private final JComboBox<String> filesComboBox = new JComboBox<>();
    private final JTextField statusText = new JTextField(25);

    public MainFrame(String title) {
        super(title);

        buildMenuBar();

        setLayout(new BorderLayout());

        var logoLabel = new JLabel("DEVELOGICA - QuickCopy 1.0");
        var topPanel = new JPanel();
        topPanel.add(logoLabel);

        addFiles(filesComboBox);

        var centerPanel = new JPanel();
        centerPanel.add(filesComboBox);

        var buttonPanel = new JPanel(new FlowLayout());

        var backupButton = new JButton("Backup");
        var pathButton = new JButton("Copy Path");
        var openButton = new JButton("Open Folder");
        var exitButton = new JButton("Exit");

        backupButton.addActionListener(this::backup);
        pathButton.addActionListener(this::getPath);
        openButton.addActionListener(this::openDir);
        exitButton.addActionListener(event -> exit());

        buttonPanel.add(backupButton);
        buttonPanel.add(pathButton);
        buttonPanel.add(openButton);
        centerPanel.add(buttonPanel);

        var statusPanel = new JPanel();
        statusText.setEditable(false);
        statusPanel.add(statusText);

        var bottomPanel = new JPanel();

        bottomPanel.add(statusPanel);
        bottomPanel.add(exitButton);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exit();
            }
        });

        pack();
    }
    private void buildMenuBar() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("File");
        buildFileMenu(fileMenu);
        menuBar.add(fileMenu);

        var editMenu = new JMenu("Edit");
        buildEditMenu(editMenu);
        menuBar.add(editMenu);

        var helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void buildFileMenu(JMenu fileMenu) {
        var exitMItem = new JMenuItem("Exit");
        exitMItem.addActionListener(actionEvent -> exit());
        fileMenu.add(exitMItem);
    }

    private void buildEditMenu(JMenu editMenu) {
        var copyPathMItem = new JMenuItem("Copy Path");
        copyPathMItem.addActionListener(this::getPath);
        editMenu.add(copyPathMItem);
    }

    private void openDir(ActionEvent event) {
        String selected = (String) filesComboBox.getSelectedItem();
        ProcessBuilder builder = new ProcessBuilder();
        String[] pathArray = QuickCopy.destDirRoot.toString().split("/");

        List<String> command = new ArrayList<>();
        command.add("explorer.exe");

        command.addAll(Arrays.asList(pathArray));

        assert selected != null;
        if (!selected.equalsIgnoreCase("all")) {
            String fileName = Objects.requireNonNull(QuickCopy.getFilenameByAlias(selected));
            command.add("\\" + fileName.substring(0, fileName.length() - 4));
        }

        try {
            builder.command(command.toArray(new String[0]));
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getPath(ActionEvent actionEvent) {
        String selected = (String) filesComboBox.getSelectedItem();

        assert selected != null;
        if (!selected.equalsIgnoreCase("all")) {
            Utilities.copyToClipboard(getPathByAlias(selected));
        } else {
            StringBuilder paths = new StringBuilder();
            QuickCopy.getAllAliases().forEach(x -> paths.append(getPathByAlias(x)).append(System.lineSeparator()));
            Utilities.copyToClipboard(paths);
        }
    }

    private String getPathByAlias(String alias) {
        String fileName = Objects.requireNonNull(QuickCopy.getFilenameByAlias(alias));
        fileName = fileName.substring(0, fileName.length() - 4);
        return QuickCopy.destDirRoot.resolve(fileName).toString();
    }


    private void backup(ActionEvent event) {
        String selected = (String) Objects.requireNonNull(filesComboBox.getSelectedItem());
        if (selected.equalsIgnoreCase("all")) {
            for (var item : QuickCopy.getAllAliases()) {
                statusText.setText(QuickCopy.getFilenameByAlias(item));
                QuickCopy.backup(QuickCopy.getFilenameByAlias(item), QuickCopy.destDirRoot);
            }
        } else {
            statusText.setText(QuickCopy.getFilenameByAlias(selected));
            QuickCopy.backup(QuickCopy.getFilenameByAlias(selected), QuickCopy.destDirRoot);
        }
    }


    private void exit() {
        final String question = String.format("Are you sure you want to quit %s?", QuickCopy.APP_NAME);
        int result = JOptionPane.showConfirmDialog(
                this, question, QuickCopy.APP_NAME, JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Adds backup file aliases to comboBox. */
    private void addFiles(JComboBox<String> filesComboBox) {
        filesComboBox.addItem("All");
        for (String key : QuickCopy.getAllAliases()) {
            filesComboBox.addItem(key);
        }
    }
}

