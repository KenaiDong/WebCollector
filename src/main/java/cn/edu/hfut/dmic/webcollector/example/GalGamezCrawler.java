/*
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cn.edu.hfut.dmic.webcollector.example;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.ExceptionUtils;

import com.google.common.collect.Lists;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;


/**
 * 本教程演示了如何自定义http请求
 *
 * 有些爬取任务中，可能只有部分URL需要使用POST请求，我们可以利用2.20版本中添 加的MetaData功能，来完成POST请求的定制。
 *
 * 使用MetaData除了可以标记URL是否需要使用POST，还可以存储POST所需的参数信息
 *
 * 教程中还演示了如何定制Cookie、User-Agent等http请求头信息
 *
 * WebCollector中已经包含了org.json的jar包
 *
 * @author hu
 */
public class GalGamezCrawler extends BreadthCrawler {

    /**
     *
     * 假设我们要爬取三个链接 1)http://www.A.com/index.php 需要POST，并需要POST表单数据username:John
     * 2)http://www.B.com/index.php?age=10 需要POST，数据直接在URL中 ，不需要附带数据 3)http://www.C.com/
     * 需要GET
     */
    public GalGamezCrawler(final String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);


        for (int i = start; i <= end; i++) {
            addSeed(new CrawlDatum(http + "&page=" + i).meta("method", "POST"));
        }

        setRequester(new OkHttpRequester(){
            @Override
            public Request.Builder createRequestBuilder(CrawlDatum crawlDatum) {
                Request.Builder requestBuilder = super.createRequestBuilder(crawlDatum);
                String method = crawlDatum.meta("method");

                // 默认就是GET方式，直接返回原来的即可
                if(method.equals("GET")){
                    return requestBuilder;
                }

                if(method.equals("POST")){
                    RequestBody requestBody;
                    String username = crawlDatum.meta("username");
                    // 如果没有表单数据username，POST的数据直接在URL中
                    if(username == null){
                        requestBody = RequestBody.create(null, new byte[]{});
                    }else{
                        // 根据meta构建POST表单数据
                        requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("username", username)
                                .build();
                    }
                    return requestBuilder.post(requestBody);
                }

                //执行这句会抛出异常
                ExceptionUtils.fail("wrong method: " + method);
                return null;
            }
        });


    }


    @Override
    public void visit(Page page, CrawlDatums next) {
        Elements elements = page.doc().getElementsByTag("a");
        for (Element a : elements) {
            boolean b = false;
            for (String k : keys) {
                b = a.text().contains(k);
            }
            if (a.text().contains(key) || b){
                String href = galgamez + a.attributes().get("href");
                Pair<String, String > pair = new Pair<String ,String>(a.text(), href);
                src.add(pair);
            }
        }
    }

    static String galgamez = "http://www.galgamezz.org/bbs/";
    static String http = "http://www.galgamezz.org/bbs/forumdisplay.php?fid=8";
    static Integer page = 10;
    static Integer start = 1;
    static Integer end = 50;
    static String key = "亿万僵尸";
    static String[] keys = new String[]{"亿万僵尸"};
    static List<Pair<String , String>> src = new ArrayList<Pair<String, String>>();

    /**
     *
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception {

        GalGamezCrawler crawler = new GalGamezCrawler("json_crawler", true);
        crawler.start(1);
        for (Pair<String, String> pair : src) {
            System.out.println(pair.getKey() + "    ");
            System.out.println(pair.getValue());
            System.out.println();
        }
    }

}
