package com.example.ticketopenaggregator;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CrawlingTest {

    @Test
    public void testInterParkTicketCrawling() throws IOException {
        String url = "https://ticket.interpark.com/webzine/paper/TPNoticeList_iFrame.asp?bbsno=34&pageno=1&KindOfGoods=TICKET&Genre=&sort=opendate&stext=";
        Document doc = Jsoup.connect(url).get();

        doc.select("tbody .count").forEach(element -> {
            if (Integer.parseInt(element.text()) > 400) {
                String date = element.parent().select(".date").text();
                Elements subject = element.parent().select(".subject a");
                String detailUrl = "https://ticket.interpark.com/webzine/paper/" + subject.attr("href");

                System.out.println("element = " + element.text());
                System.out.println("date = " + date);
                System.out.println("subject = " + detailUrl);
            }
        });
    }

}
