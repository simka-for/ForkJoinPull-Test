import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main {

    private static Scanner scanner;
    private static String url;
    private static long start;
    private static ForkJoinPool forkJoinPool;
    private static Thread workThread;

    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {

        start = System.currentTimeMillis();
        getUrl();
        System.out.println("\nURL address accepted");
        startWorkThread();
    }

    private static void getUrl() {
        System.out.println("Write website url. Example : \"https://lenta.ru\": ");
        System.out.println("The site must be protected by \"https\" protocol");
        while (!isUrlValid()) {
            System.out.print("\nError in site address format, check and re-enter: ");
        }
    }

    private static void startWorkThread() {
        workThread = new Thread(() -> {

            forkJoinPool = new ForkJoinPool(processorCoreCount);

            System.out.println("\nStarted...");
            URL mainPage = forkJoinPool.invoke(new LinkPull(url));

            System.out.println(mainPage.getUrlLevel() + " " + mainPage.getUrl());
            mainPage.getSubUrlList().forEach(i -> System.out.println(i.getUrlLevel() + " " + i.getUrl()));

            System.out.println("Work completed in " + (int) ((System.currentTimeMillis() - start) / 1000) + " c");

            writeStringToFile(buildStringsFromPage(mainPage));

        });
        workThread.start();
    }


    private static boolean isUrlValid() {
        scanner = new Scanner(System.in);
        url = scanner.nextLine().trim();
        if (url.matches("(http(s)*://)*(www\\.)*.+\\..+")) {
            String https = "https://";
            url = url.replaceAll("(http(s)*://)*(www\\.)*", "");
            url = https + url;
            return true;
        }
        return false;
    }

    private static ArrayList<String> buildStringsFromPage(URL url) {
        final String tab = "\t";
        ArrayList<String> resultList = new ArrayList<>();

        String tabString = "";
        for (int j = 0; j < url.getUrlLevel(); j++) {
            tabString += tab;
        }
        resultList.add(tabString + url.getUrl() + "\n");
        if (url.getSubUrlList() != null) {
            for (URL outUrl : url.getSubUrlList()) {
                ArrayList<String> tempStringList = buildStringsFromPage(outUrl);
                resultList.addAll(tempStringList);
            }
        }
        return resultList;
    }


    private static void writeStringToFile(List<String> stringList) {
        try {
            String filePath = "src/main/map.txt";
            Files.deleteIfExists(Paths.get(filePath));
            Files.createFile(Path.of(filePath));
            File file = new File(filePath);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : stringList) {
                writer.write(s);
            }
            writer.close();
            System.out.println("File recorded to << " + filePath + " >>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

