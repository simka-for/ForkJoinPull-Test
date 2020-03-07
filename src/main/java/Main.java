import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Main {

    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Write website url. Example : << https://lenta.ru/ >>");

        String url = scanner.nextLine();

        System.out.println("The program will be launched in " + processorCoreCount + " threads");
        System.out.println("Scan started!");

        LinkPull linkPull = new LinkPull(url, url);
        String map = new ForkJoinPool(processorCoreCount).invoke(linkPull);

        System.out.println("Scan completed!\nWrite to file started");

        writeFile(map);

    }
    public static void writeFile(String map){

        String path = "src/main/map.txt";
        File file = new File(path);

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(map);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("File recorded to << " + path + " >>");
    }

}
