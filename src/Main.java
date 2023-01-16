import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // ЗАДАЧА 1. УСТАНОВКА.
        // Предполагается, что папка Games уже существует
        // Начальный путь:
        Path initPath = Path.of("C:\\Users\\Adios\\Documents\\Java\\netology\\15_java_core\\Files\\Task1_Ustanovka\\Games");

        // Лог работы с файлами и папками:
        String log = "";
        StringBuilder sb = new StringBuilder(log);

        // Временный путь для создания папок
        Path tempPath;

        // Работа с каталогом Games/src/
        tempPath = Path.of(initPath + File.separator + "src" + File.separator + "main" + File.separator);
        if (createFolder(tempPath.toString(), sb)) {
            createFile(tempPath + File.separator + "Main.java", sb);
            createFile(tempPath + File.separator + "Utils.java", sb);
        }
        tempPath = Path.of(initPath + File.separator + "src" + File.separator + "test" + File.separator);
        createFolder(tempPath.toString(), sb);

        // Работа с каталогом Games/res/
        tempPath = Path.of(initPath + File.separator + "res" + File.separator + "drawables" + File.separator);
        createFolder(tempPath.toString(), sb);
        tempPath = Path.of(initPath + File.separator + "res" + File.separator + "vectors" + File.separator);
        createFolder(tempPath.toString(), sb);
        tempPath = Path.of(initPath + File.separator + "res" + File.separator + "icons" + File.separator);
        createFolder(tempPath.toString(), sb);

        // Работа с каталогом Games/
        tempPath = Path.of(initPath + File.separator + "savegames" + File.separator);
        createFolder(tempPath.toString(), sb);
        tempPath = Path.of(initPath + File.separator + "temp" + File.separator);
        if (createFolder(tempPath.toString(), sb)) {
            createFile(tempPath + File.separator + "temp.txt", sb);
        }//end if

        // ЗАДАЧА 2. СОХРАНЕНИЕ
        List<GameProgress> gameSaves = new ArrayList<>();
        gameSaves.add(new GameProgress(100, 20, 1, 258.36));
        Thread.sleep(1000);     // это чтобы время создания файлов отличалось
        gameSaves.add(new GameProgress(80, 15, 2, 369.97));
        Thread.sleep(1000);
        gameSaves.add(new GameProgress(50, 5, 4, 789.58));
        Thread.sleep(1000);

        //Сохраним все экземпляры игр
        for (GameProgress game : gameSaves) {
            saveGame(initPath + File.separator + "savegames" + File.separator, game, sb);
        }

        // Архивируем и удалим экземпляры игр
        zipFiles(initPath + File.separator + "savegames" + File.separator, gameSaves, sb);

        // Запись лога в файл
        try (FileWriter writer = new FileWriter(tempPath + File.separator + "temp.txt", false)) {
            writer.write(sb.toString());
            writer.flush();     // дозаписываем и очищаем буфер
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        // ЗАДАЧА 2. РАЗАРХИВАЦИЯ
        openZip(initPath + File.separator + "savegames" + File.separator, initPath + File.separator + "savegames" + File.separator);
        for (GameProgress game : gameSaves) {
            System.out.println(openProgress(initPath + File.separator + "savegames" + File.separator, game.getName()).toString());
        }
        // System.out.println(gameProgress);

    }//end main

    // Создание файла
    public static void createFile(String path, StringBuilder sb) {
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                sb.append(new Date()).append(". Успешно создан файл ").append(file.getPath()).append("\n");
            }
        } catch (IOException ex) {
            sb.append(new Date()).append(". Не удалось создать файл ").append(file.getPath()).append(": ").append(ex.getMessage()).append("\n");
        }
    }//end method

    // Создание папки
    public static boolean createFolder(String path, StringBuilder sb) {
        File folder = new File(path);
        if (folder.mkdirs()) {
            sb.append(new Date()).append(". Успешно создана папка ").append(folder.getPath()).append("\n");
            return true;
        } else {
            sb.append(new Date()).append(". Не удалось создать папку ").append(folder.getPath()).append("\n");
            return false;
        }
    }//end method

    // Сохранение игры
    public static void saveGame(String path, GameProgress gameProgress, StringBuilder sb) {
        // откроем выходной поток для записи в файл
        try (FileOutputStream fos = new FileOutputStream(path + gameProgress.getName() + ".dat");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // запишем экземпляр класса в файл
            oos.writeObject(gameProgress);
            sb.append(new Date()).append(". Успешное сохранение файла: ").append(gameProgress.getName()).append(".dat").append("\n");
        } catch (Exception ex) {
            sb.append(new Date()).append(". Не удалось сохранить файл: ").append(gameProgress.getName()).append(".dat: ").append(ex.getMessage()).append("\n");
        }
    }//end method

    // Архивация игр
    public static void zipFiles(String zipPath, List<GameProgress> files, StringBuilder sb) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipPath + "savegames.zip"))) {
            for (GameProgress gp : files) {
                FileInputStream fis = new FileInputStream(zipPath + gp.getName() + ".dat");
                ZipEntry entry = new ZipEntry(gp.getName() + ".dat");
                zout.putNextEntry(entry);
                // считываем содержимое файла в массив byte
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                // добавляем содержимое к архиву
                zout.write(buffer);
                // закрываем текущую запись для новой записи
                zout.closeEntry();
                fis.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        // Удалим файлы после архивации
        for (GameProgress gp : files) {
            File file = new File(zipPath + gp.getName() + ".dat");
            if (file.delete()) {
                sb.append(new Date()).append(". Успешное удаление файла: ").append(file.getName()).append("\n");
            } else {
                sb.append(new Date()).append(". Не удалось удалить файл: ").append(file.getName()).append("\n");
            }
        }
    }// end method

    // Разархивация игр
    public static void openZip(String zipPath, String unzipTo) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipPath + "savegames.zip"))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName(); // получим название файла
                // распаковка
                FileOutputStream fout = new FileOutputStream(unzipTo + name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }//end method

    // Чтение разархивированных игр
    public static GameProgress openProgress(String path, String saveName) {
        GameProgress gameProgress = null;
        // откроем входной поток для чтения файла
        try (FileInputStream fis = new FileInputStream(path + saveName + ".dat");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // десериализуем объект и скастим его в класс
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }

}//end class