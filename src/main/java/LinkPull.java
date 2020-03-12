import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class LinkPull extends RecursiveTask<URL> {

    private URL url;
    private String firstUrl;
    private volatile Set<String> visitedLinks;
    private volatile Queue<URL> queueURL;


    public LinkPull(String firstUrl){
        this.url = new URL(firstUrl, 0);
        this.firstUrl = firstUrl ;
        this.visitedLinks = Collections.synchronizedSet(new HashSet<>());
        this.queueURL = new ConcurrentLinkedQueue<>();
        this.visitedLinks.add(firstUrl);
    }
    public LinkPull(URL url, String firstUrl, Set<String> visitedLinks, Queue<URL> queueURL) {
        this.url = url;
        this.firstUrl = firstUrl;
        this.visitedLinks = visitedLinks;
        this.queueURL = queueURL;
    }

    @Override
    protected URL compute() {
        String currentUrl = url.getUrl();
        HashSet<String> links = getUrl(currentUrl);
        ArrayList<LinkPull> taskList = new ArrayList<>();
        for (String link : links) {
            if (!visitedLinks.contains(link)) {
                URL newPage = new URL(link, (url.getUrlLevel() + 1));
                queueURL.add(newPage);
                visitedLinks.add(link);
            }
        }
        while (queueURL.peek() != null) {
            URL tempPage = queueURL.poll();
            LinkPull task = new LinkPull(tempPage, firstUrl, visitedLinks, queueURL);
            url.addSubUrl(tempPage);
            task.fork();
            taskList.add(task);
        }
        taskList.forEach(ForkJoinTask::join);
        return url;
    }

    public HashSet<String> getUrl(String url){
        HashSet<String> resultList = new HashSet<>();
        System.out.println("Parsing URL with address: " + url);
        try {
            Thread.sleep(100);
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("a");

            for (Element el : elements){
                String attr = el.attr("abs:href");
                if (!attr.isEmpty() && !attr.contains("#") && !visitedLinks.contains(attr) && attr.startsWith(firstUrl)){
                    LinkPull linkPull = new LinkPull(attr);
                    linkPull.fork();
                    resultList.add(attr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
