package cn.edu.hfut.dmic.webcollector.example;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.Proxies;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;
import okhttp3.OkHttpClient;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 教程：使用WebCollector自定义Http请求
 * 可以自定义User-Agent和Cookie
 *
 * @author hu
 */
public class t66yCrawler extends BreadthCrawler {

    // 自定义的请求插件
    // 可以自定义User-Agent和Cookie
    public static class MyRequester extends OkHttpRequester {

        Proxies proxies;

        public MyRequester() {
            proxies = new Proxies();
            // add a socks proxy
            proxies.addSocksProxy("127.0.0.1", 7890);
            // null means direct connection without proxy
//            proxies.add(null);
        }

        @Override
        public OkHttpClient.Builder createOkHttpClientBuilder() {
            return super.createOkHttpClientBuilder()
                    // 设置一个代理选择器
                    .proxySelector(new ProxySelector() {
                        @Override
                        public List<Proxy> select(URI uri) {
                            // 随机选择1个代理
                            Proxy randomProxy = proxies.get(0);
                            // 返回值类型需要是List
                            List<Proxy> randomProxies = new ArrayList<Proxy>();
                            //如果随机到null，即不需要代理，返回空的List即可
                            if(randomProxy != null) {
                                randomProxies.add(randomProxy);
                            }
                            return randomProxies;
                        }

                        @Override
                        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

                        }
                    });
        }

    }

    public t66yCrawler(String crawlPath) {
        super(crawlPath, true);

        // 设置请求插件
        setRequester(new MyRequester());


        for (int i = start; i <= end; i++) {
            addSeed(http + "thread0806.php?fid=" + type + "&search=&page=" +  i );
        }
//        addSeed("https://www.google.com/");
    }

    public void visit(Page page, CrawlDatums crawlDatums) {
        Elements s_xst = page.doc().getElementsByClass("tal");
        for (Element element : s_xst) {
            if (element.text().contains(target)){
                System.out.println("找到了： " + element.text());
                System.out.println(http + element.getAllElements().get(2).attributes().get("href"));
                sb.append(element.text())
                        .append("\n")
                        .append(http)
                        .append(element.getAllElements().get(2).attributes().get("href"))
                        .append("\n");
            }
        }
    }

    public static String http = "https://www.t66y.com/";
//    public static String http = "https://www.weixeus.info/";
    public static String youma = "15";
    public static String zimu = "26";
    public static String wuma = "2";
    public static String oumei = "4";
    public static String guochan = "25";

    public static String type = oumei;

//    public static String target = "SDDE";
//    public static String target = "RCT";
    public static String target = "VR";

    public static int start = 1100;
    public static int end = 1400;

    public static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws Exception {

        t66yCrawler crawler = new t66yCrawler("crawl");

        crawler.start(1);
        System.out.println(sb.toString());
    }
}
