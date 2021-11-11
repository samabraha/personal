
import javax.swing.*;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.Instant;
import java.time.LocalDate;

import java.util.Map;

import java.util.TimeZone;

public class QuickCopy {
    public static final String APP_NAME = "Develogica QuickCopy";
    public static final Path destDirRoot = Path.of("E:/EACG-0032");
    static final Map<String, String> filesMap = Map.of(
            "EA", "Albino-Quiosa.qpb",
            "MB", "Muxitos-Benfica.qpb",
            "FS", "Muxitos-Rocha.qpb"
    );

    static String getFileNameFromAlias(String alias) {
        if (filesMap.containsKey(alias)) {
            return filesMap.get(alias);
        }

        new Message("The name \"%s\" could not found in files dictionary.%n", alias).print();
        return null;
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {

        new Message("Welcome to %s%n", APP_NAME).print();

        if (args.length > 0) {
            runCommand(args);
        } else {
            // Launches GUI
            AppLauncher.main(args);
        }
    }

    /** Parses command and options from arg
     * @param arg String[] containing command options to be parsed */
    private static void runCommand(String[] arg) {
        String command = arg[0].toLowerCase();

        switch (command) {
            case "cp", "copy" -> runCopyCommand(arg);
            case "gp", "getpath" -> getLatestBackupPath(arg);
            default -> System.err.println("Unknown command: " + command);
        }
    }

    private static void runCopyCommand(String... arg) {
        if (arg.length < 2) {
            new Message("Missing file name or alias.").print();
            return;
        }
        String alias = arg[1].toUpperCase();

        if (alias.equalsIgnoreCase("all")) {
            for (var element : filesMap.entrySet()) {
                backup(element.getValue(), destDirRoot);
            }
            return;
        }
        String fileName = getFileNameFromAlias(alias);

        backup(fileName, destDirRoot);
    }

    public static void backup(String fileName, Path destRootDir) {
        File sourceFile = getSource("", fileName);

        if (sourceFile == null) {
            return;
        }
        long modifiedLong = sourceFile.lastModified();

        /* Compose destPath */
        Path destPath = Path.of(
                destRootDir.toString(),
                fileName.substring(0, fileName.length() - 4));

        if (!destPath.toFile().exists()) {
            System.err.println("Wrong backup location " + destPath);
            return;
        }

        destPath = Path.of(destPath.toString(), getYearMonthPathSegment(modifiedLong).toString());


        if (!destPath.toFile().exists()) {
            try {
                Message.create("Creating: " + destPath + "\n").print();
                Files.createDirectories(destPath);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        String destFileName = getFilenamePrefix(modifiedLong) + " - " + fileName;

        destPath = Path.of(destPath.toString(), destFileName);

        if (destPath.toFile().exists()) {
            System.err.printf("\"%s\" already exists at %n\t\"%s\".%n",
                    fileName, destPath);
        } else {
            copy(sourceFile.toPath(), destPath);
        }

        copyToClipboard(destPath);
    }

    /** Copies a file using Files.copy() from Path a to Path b */
    private static void copy(Path from, Path to) {

        try {
            String lastLine = "Copying in progress...";
            new Message("Copying%n\tfrom: %s%n\tto: %s%n", from, to).print();
            Message.create(lastLine).print();
            Files.copy(from, to);
            Message.create("Copying successfully completed.\n").printOnSameLine(lastLine.length());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    /** Returns year-month-day String from longEpochOfMillis */
    private static String getFilenamePrefix(long fileDate) {
        var instant = Instant.ofEpochMilli(fileDate);
        LocalDate date = LocalDate.ofInstant(instant, TimeZone.getDefault().toZoneId());

        return String.format("%04d-%02d-%02d",
                date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /** Returns year/year-month Path segment from longEpochOfMillis
     * @return Path segment containing year and year-month directories */
    private static Path getYearMonthPathSegment(long fileDate) {
        var instant = Instant.ofEpochMilli(fileDate);

        LocalDate date = LocalDate.ofInstant(instant, TimeZone.getDefault().toZoneId());

        String year = String.format("%04d", date.getYear());
        String month =  String.format("%02d", date.getMonthValue());

        return Path.of(year, year + "-" + month);
    }

    /**
     * Returns the Path of source by iterating through possible roots
     * and checking if the file exists in a given root*/
    private static File getSource(String parentPath, String fileName) {

        fileName = Path.of(parentPath, fileName).toString();

        Iterable<Path> roots = Path.of("").getFileSystem().getRootDirectories();

        for (Path root : roots) {
            Path potentialSource = Path.of(root.toString()).resolve(fileName);
            if (potentialSource.toFile().exists()) {
                new Message("\"%s\" found\n", potentialSource).print();
                return potentialSource.toFile();
            }
        }

        new Message("\"%s\" not found in any drive on this computer.%n", fileName).print();
        return null;
    }

    /** Finds latest backup by iterating by date backwards and checks if a matching
     * file exists in backup folder.
     * @param arg*/
    private static Path getLatestBackupPath(String... arg) {
        /* TODO */
        Message.create("SORRY, THIS IS A STUB METHOD.")
                .print();
        return null;
    }

    /** Calls toString() the Object parameter and sends the result to clipboard
     * @param object from which a string is to be extracted  */
    private static void copyToClipboard(Object object) {
        var selection = new StringSelection(object.toString());

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(selection, null);
    }
}
