package ink.moming.htmlanalysislib;




import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlAnalysis {

    public String htmlTest(String url){
        String title="";
        try {
            Document document = Jsoup.connect(url).get();
            title = document .title();

        }catch (IOException e){
            e.printStackTrace();
        }
        return title;
    }

    public JSONArray getCityList(){
        String url = "https://lvyou.baidu.com/scene/";
        JSONArray areaArray = new JSONArray();
        try {
            Document document = Jsoup.connect(url).get();
            Element body = document.getElementById("body");
            Elements citys = body.getElementsByClass("china-visit");
            for (Element city:citys){
                JSONObject jsonArea=new JSONObject();

                String firstAreaName = city.getElementsByTag("span").first().text();
                Elements secondAreaEles = city.getElementsByTag("ul").first()
                        .getElementsByTag("li");
                jsonArea.put("area",firstAreaName);

                JSONArray secondAreaArray = new JSONArray();
                for (int i=0;i<secondAreaEles.size();i++){
                    String secondAreaName = secondAreaEles.get(i)
                            .getElementsByClass("china-visit-type").first().text();
                    Elements thirdAreaEles = secondAreaEles.get(i)
                            .getElementsByTag("a");
                    JSONObject secondAreaJson = new JSONObject();
                    JSONArray thirdAreaArray = new JSONArray();

                    secondAreaJson.put("region",secondAreaName);

                    for (int j =0 ;j<thirdAreaEles.size();j++){
                        JSONObject thirdAreaJson = new JSONObject();
                        thirdAreaJson.put("city",thirdAreaEles.get(j).text());
                        thirdAreaJson.put("url",thirdAreaEles.get(j).attr("href"));
                        thirdAreaArray.put(thirdAreaJson);
                    }
                    secondAreaJson.put("citys",thirdAreaArray);
                    secondAreaArray.put(secondAreaJson);

                }
                jsonArea.put("regions",secondAreaArray);
                areaArray.put(jsonArea);
            }




        }catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return areaArray;
    }

    public JSONObject getCityGuide(String url){
        //https://lvyou.baidu.com/search/ajax/search?format=ajax&word=%E4%B8%89%E4%BA%9A
        JSONObject cityGuide = new JSONObject();
        try {
            Document document = Jsoup.connect(url).get();
            String cityName = document.getElementById("dest-body")
                    .getElementsByClass("dest-name").first()
                    .getElementsByClass("main-name").first()
                    .getElementsByTag("a").first().text();
            cityGuide.put("name",cityName);
            String cityInfo = document.getElementsByClass("main-desc-p").first().text();
            cityGuide.put("info",cityInfo);

            Elements picEles = document.getElementById("J_pic-slider")
                    .getElementsByTag("li");
            int count =0;
            String link = picEles.get(count).getElementsByTag("img").first()
                    .attr("src");
            String src = getMatchFromLink(link);

            cityGuide.put("image",src);

            JSONArray articleArray;

            String articleJsonStr = getArticleFromAjax(cityName);
            JSONObject articleJsonObj = new JSONObject(articleJsonStr);

            if (articleJsonObj.get("errno").equals(0)){
                articleArray = articleJsonObj.getJSONObject("data")
                        .getJSONObject("search_res")
                        .getJSONArray("notes_list");

            }else {
                articleArray = null;
            }

            cityGuide.put("articles",articleArray);


        }catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityGuide;
    }

    private String getMatchFromLink(String link) {
        Pattern pattern = Pattern.compile("item/(\\S*)jpg");
        Matcher matcher =  pattern.matcher(link);
        if (matcher.find()){
            return matcher.group().substring(5);
        }else {
            return "";
        }

    }

    public String getArticleFromAjax(String city) throws IOException {
        String url = "https://lvyou.baidu.com/search/ajax/search?format=ajax&word="+city;

        String json = Jsoup.connect(url).ignoreContentType(true).execute().body();

        return json;

    }


}
