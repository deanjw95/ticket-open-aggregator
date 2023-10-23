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

    @Test
    public void testMelonTicketCrawling() throws IOException {
        String url = "https://ticket.melon.com/csoon/ajax/listTicketOpen.htm";
        int orderType = 2;
        int pageIndex = 1;
        String schGcode = "GENRE_ALL";

        Document doc = Jsoup.connect(url)
                .data("orderType", String.valueOf(orderType))
                .data("pageIndex", String.valueOf(pageIndex))
                .data("schGcode", schGcode)
                .post();

        doc.select(".txt_review").forEach(element -> {
            // , 없애기
            if (Integer.parseInt(element.text().replace(",", "")) > 1000) {
                String date = element.parent().parent().parent().select(".date").text();
                Elements subject = element.parent().parent().select(".link_consert a");
                String detailUrl = "https://ticket.melon.com/csoon/" + subject.attr("href").substring(2);

                System.out.println("element = " + element.text());
                System.out.println("date = " + date);
                System.out.println("subject = " + detailUrl);
            }
        });
    }

    @Test
    public void testYes24TicketCrawling() throws IOException {
        String url = "http://ticket.yes24.com/New/Notice/Ajax/axList.aspx";
        int page = 1;
        int size = 20;
        int order = 2;
        String searchType = "All";

        Document doc = Jsoup.connect(url)
                .data("page", String.valueOf(page))
                .data("size", String.valueOf(size))
                .data("order", String.valueOf(order))
                .data("searchType", searchType)
                .post();

        doc.select("tbody tr").forEach(element -> {
            try {
                if (Integer.parseInt(element.child(3).text().replace(",", "")) > 1000) {
                    String date = element.child(2).text();
                    Elements subject = element.child(1).select("a");
                    String detailUrl = "http://ticket.yes24.com" + subject.attr("href");

                    System.out.println("element = " + element.child(2).text());
                    System.out.println("date = " + date);
                    System.out.println("subject = " + detailUrl);
                }
            } catch (NumberFormatException e) {
                System.out.println("element.child(3).text() = " + element.child(3).text());
            }
        });
    }


}
