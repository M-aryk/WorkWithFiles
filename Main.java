import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) {
        StringBuilder result = new StringBuilder();

        File dirSrc = new File("D:\\Games\\src");
        File dirRes = new File("D:\\Games\\res");
        File dirSavegames = new File("D:\\Games\\savegames");
        File dirTemp = new File("D:\\Games\\temp");
        File dirMain = new File(dirSrc + "\\main");
        File dirTest = new File(dirSrc + "\\test");
        File dirDrawables = new File(dirRes + "\\drawables");
        File dirVectors = new File(dirRes + "\\vectors");
        File dirIcons = new File(dirRes + "\\icons");

        List<File> dirs = new ArrayList<>(Arrays.asList(dirSrc, dirRes, dirSavegames, dirTemp, dirMain, dirTest, dirDrawables, dirVectors, dirIcons));
        createDir(result, dirs);

        File fileMain = new File(dirMain, "Main.java");
        File fileUtils = new File(dirMain, "Utils.java");
        File fileTemp = new File(dirTemp, "temp.txt");

        List<File> files = new ArrayList<>(Arrays.asList(fileMain, fileTemp, fileUtils));
        creatableFile(result, files);

        try (FileWriter fileWriter = new FileWriter(fileTemp, true)) {
            fileWriter.write(result.toString());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        GameProgress progress1 = new GameProgress(100, 20, 30, 25.0);
        String filePath1 = dirSavegames.getAbsolutePath() + "\\save1.dat";
        saveGame(filePath1, progress1);
        GameProgress progress2 = new GameProgress(50, 12, 10, 7.8);
        String filePath2 = dirSavegames.getAbsolutePath() + "\\save2.dat";
        saveGame(filePath2, progress2);
        GameProgress progress3 = new GameProgress(120, 55, 48, 36.5);
        String filePath3 = dirSavegames.getAbsolutePath() + "\\save3.dat";
        saveGame(filePath3, progress3);

        List<String> filePaths = new ArrayList<>(Arrays.asList(filePath1, filePath2, filePath3));
        String saveGamePath = dirSavegames + "\\zip.zip";
        zipFiles(saveGamePath, filePaths);
        deleteSaveNotZip(dirSavegames);
        openZip(saveGamePath, dirSavegames.getAbsolutePath());

        System.out.println(openProgress(filePath2));
    }

    private static GameProgress openProgress(String filePath) {
        GameProgress gameProgress = null;

        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }

    private static void openZip(String saveGamePath, String dirSavegames) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(saveGamePath))) {
            ZipEntry entry;
            String name;
            while ((entry = zis.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fos = new FileOutputStream(name);
                for (int i = zis.read(); i != -1; i = zis.read()) {
                    fos.write(i);
                }
                fos.flush();
                zis.closeEntry();
                fos.close();
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void deleteSaveNotZip(File dirSavegames) {
        for (File f : dirSavegames.listFiles()) {
            if (f != null && f.getName().endsWith(".dat")) {
                f.delete();
            }
        }
    }

    private static void zipFiles(String string, List<String> filePaths) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(string))) {
            for (String filePath : filePaths) {
                try (FileInputStream fis = new FileInputStream(filePath)) {
                    ZipEntry zipEntry = new ZipEntry(filePath);
                    zos.putNextEntry(zipEntry);
                    while (fis.available() > 0) {
                        zos.write(fis.read());
                    }
                    zos.closeEntry();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void creatableFile(StringBuilder result, List<File> files) {
        try {
            for (File file : files) {
                if (file.createNewFile()) {
                    result.append("Файл ").append(file.getName()).append(" создан. \n");
                }
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void createDir(StringBuilder result, List<File> dirs) {
        for (File dir : dirs) {
            if (dir.mkdir()) result.append("Каталог ").append(dir.getName()).append(" создан. \n");
        }
    }

    private static void saveGame(String filePath, GameProgress progress) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(progress);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
