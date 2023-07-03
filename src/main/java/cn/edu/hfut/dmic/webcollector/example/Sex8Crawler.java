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
public class Sex8Crawler extends BreadthCrawler {

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

    public Sex8Crawler(String crawlPath) {
        super(crawlPath, true);

        // 设置请求插件
        setRequester(new MyRequester());


        for (int i = start; i <= end; i++) {
            addSeed(http + "forum-" + type + "-" + i +".html");
        }
//        addSeed("https://www.google.com/");
    }

    public void visit(Page page, CrawlDatums crawlDatums) {
        Elements s_xst = page.doc().getElementsByClass("s xst");
        for (Element element : s_xst) {
            if (element.text().contains(target)){
                System.out.println("找到了： " + element.text());
                System.out.println(http + element.attributes().get("href"));
                sb.append(element.text())
                        .append("\n")
                        .append(http)
                        .append(element.attributes().get("href")).append("\n");
            }
        }
    }

    public static String http = "https://www.sex8.cc/";
//    public static String http = "https://www.weixeus.info/";
    public static String youma = "134";
    public static String zimu = "70";
    public static String wuma = "96";
    public static String xinpian = "232";
    public static String gaoqing = "233";
    public static String oumei = "525";
    public static String oumei2 = "135";
    public static String huaren1 = "280";
    public static String huaren2 = "798";
    // 下面两种没番号
    public static String wumabt = "723";
    public static String youmabt = "713";
    public static String type = wuma;

    public static String target = "电击";
//    public static String target = "RCT";
//    public static String target = "VR";

    public static int start = 1;
    public static int end = 200;

    public static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws Exception {

        Sex8Crawler crawler = new Sex8Crawler("crawl");

        crawler.start(1);
        System.out.println(sb.toString());
    }
}
