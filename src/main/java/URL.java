import java.util.concurrent.CopyOnWriteArrayList;

public class URL {

    private String url;
    private CopyOnWriteArrayList<URL> subUrlList;
    private int urlLevel;

    public URL(String url, int urlLevel) {
        this.url = url;
        this.urlLevel = urlLevel;
    }

    public URL(String url, CopyOnWriteArrayList<URL> subUrlList) {
        this.url = url;
        this.subUrlList = subUrlList;
    }

    public int getUrlLevel() {
        return urlLevel;
    }

    public void setUrlLevel(int urlLevel) {
        this.urlLevel = urlLevel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CopyOnWriteArrayList<URL> getSubUrlList() {
        if (subUrlList == null) {
            return null;
        }
        return subUrlList;
    }

    public void setSubUrlList(CopyOnWriteArrayList<URL> subUrlList) {
        this.subUrlList = subUrlList;
    }

    public void addSubUrl(URL page) {
        if (this.subUrlList == null) {
            this.subUrlList = new CopyOnWriteArrayList<>();
        }
        this.subUrlList.add(page);
    }
}