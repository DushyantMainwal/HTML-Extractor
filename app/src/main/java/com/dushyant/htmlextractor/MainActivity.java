package com.dushyant.htmlextractor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button getBtn;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.result);
        getBtn = (Button) findViewById(R.id.getBtn);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWebsite();
            }
        });
    }

    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup
                            .connect("https://impelfeed.com/article/greatest-motivational-quotes-by-sportspersons-on-struggle-and-success")
                            .get();
                    String title = doc.title();
                    Elements links = doc.select("a[href]");

                    //Favicon
//                    Element e=doc.head().select("link[href~=.*\\.ico]").first();
//                    String url=e.attr("href");

                    builder.append(title).append("\n");

                    for (Element link : links) {
                        builder.append("\n").append("Link : ").append(link.attr("href"))
                                .append("\n").append("Text : ").append(link.text());
                    }

                    //For MetaData
//                    Document doc = Jsoup.connect("http://example.com/").get()
//                    for(Element meta : doc.select("meta")) {
//                        System.out.println("Name: " + meta.attr("name") + " - Content: " + meta.attr("content"));
//                    }

//                    //Get description from document object.
//                    String description =
//                            doc.select("meta[name=description]").get(0)
//                                    .attr("content");
//                    System.out.println("Meta Description: " + description);
//
//                    //Get keywords from document object.
//                    String keywords =
//                            doc.select("meta[name=keywords]").first()
//                                    .attr("content");
//                    System.out.println("Meta Keyword : " + keywords);

                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        result.setText(builder.toString());
                    }
                });
            }
        }).start();
    }

    public boolean checkFavicon(Document doc) {
        Elements e = doc.head().select("link[rel=shortcut icon]");
        if (e.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    //For Extracting From Graph API
    public static String parsePageHeaderInfo(String urlStr) throws Exception {

        StringBuilder sb = new StringBuilder();
        Connection con = Jsoup.connect(urlStr);

        /* this browseragant thing is important to trick servers into sending us the LARGEST versions of the images */
        con.userAgent("BROWSER_USER_AGENT");
        Document doc = con.get();

        String text = null;
        Elements metaOgTitle = doc.select("meta[property=og:title]");
        if (metaOgTitle!=null) {
            text = metaOgTitle.attr("content");
        }
        else {
            text = doc.title();
        }

        String imageUrl = null;
        Elements metaOgImage = doc.select("meta[property=og:image]");
        if (metaOgImage!=null) {
            imageUrl = metaOgImage.attr("content");
        }

        if (imageUrl!=null) {
            sb.append("<img src='");
            sb.append(imageUrl);
            sb.append("' align='left' hspace='12' vspace='12' width='150px'>");
        }

        if (text!=null) {
            sb.append(text);
        }

        return sb.toString();
    }

}
