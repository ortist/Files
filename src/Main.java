import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
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
            createFile(tempPath.toString() + File.separator + "Main.java", sb);
            createFile(tempPath.toString() + File.separator + "Utils.java", sb);
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
            createFile(tempPath.toString() + File.separator + "temp.txt", sb);
            // Запись лога в файл
            try (FileWriter writer = new FileWriter(tempPath.toString() + File.separator + "temp.txt", false)) {
                writer.write(sb.toString());
                writer.flush();     // дозаписываем и очищаем буфер
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
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
            saveGame(initPath + File.separator + "savegames" + File.separator, game);
        }

        // Архивируем и удалим экземпляры игр
        zipFiles(initPath + File.separator + "savegames" + File.separator, gameSaves);


    }//end main

    // Создание файла
    public static void createFile(String path, StringBuilder sb) {
        Date date = new Date();
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                sb.append(date.toString()).append(". Успешно создан файл ").append(file.getPath()).append("\n");
            }
        } catch (IOException ex) {
            sb.append(date.toString()).append(". Не удалось создать файл ").append(file.getPath()).append(": ").append(ex.getMessage()).append("\n");
        }
    }//end method

    // Создание папки
    public static boolean createFolder(String path, StringBuilder sb) {
        Date date = new Date();
        File folder = new File(path);
        if (folder.mkdirs()) {
            sb.append(date.toString()).append(". Успешно создана папка ").append(folder.getPath()).append("\n");
            return true;
        } else {
            sb.append(date.toString()).append(". Не удалось создать папку ").append(folder.getPath()).append("\n");
            return false;
        }
    }//end method

    // Удаление файла
    public static void deleteFile(String path, StringBuilder sb) {
        Date date = new Date();
        File file = new File(path);

        if (file.delete()) {
            sb.append(date.toString()).append(". Успешно удален файл ").append(file.getPath()).append("\n");
        } else {
            sb.append(date.toString()).append(". Не удалось удалить файл ").append(file.getPath()).append("\n");
        }
    }//end method

    // Сохранение игры
    public static void saveGame(String path, GameProgress gameProgress) {
        // откроем выходной поток для записи в файл
        String str = path.toString() + gameProgress.getName() + ".dat";
        try (FileOutputStream fos = new FileOutputStream(str);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // запишем экземпляр класса в файл
            oos.writeObject(gameProgress);
            oos.close();
            oos.flush();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }//end method

    // Архивация игр
    public static void zipFiles(String zipPath, List<GameProgress> files) {
        String str = zipPath + files.get(0).getName() + ".dat";
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
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        for (GameProgress gp : files) {
            File file = new File(zipPath + gp.getName() + ".dat");
            file.delete();
        }
    }

}//end class