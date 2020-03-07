import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

public class LinkPull extends RecursiveTask<String> {

    private String url;
    private static String firstUrl;
    private static ArrayList<String> allUrl = new ArrayList<>();

    public LinkPull(String url){
        this.url = url.trim();
    }
    public LinkPull(String url, String startUrl) {
        this.url = url.trim();
        LinkPull.firstUrl = startUrl.trim();
    }

    @Override
    protected String compute() {
        StringBuffer stringBuffer = new StringBuffer(url + "\n");
        Set<LinkPull> task = new HashSet<>();
        getUrl(task);
        for (LinkPull link : task) {
            stringBuffer.append(link.join());
        }
        return stringBuffer.toString();
    }



    private void getUrl(Set<LinkPull> task){
        try {
            Thread.sleep(100);
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("a");

            for (Element el : elements){
                String attr = el.attr("abs:href");
                if (!attr.isEmpty() && !attr.contains("#") && !allUrl.contains(attr) && attr.startsWith(firstUrl)){
                    LinkPull linkPull = new LinkPull(attr);
                    linkPull.fork();
                    task.add(linkPull);
                    allUrl.add(attr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
