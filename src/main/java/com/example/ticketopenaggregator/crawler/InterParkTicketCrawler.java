package com.example.ticketopenaggregator.crawler;

import com.example.ticketopenaggregator.Ticket;
import com.example.ticketopenaggregator.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class InterParkTicketCrawler {

    private final TicketRepository ticketRepository;

    public InterParkTicketCrawler(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void interParkTicketCrawling() throws IOException {
        for (int i = 1; i < 5; i++) {
            String url = "https://ticket.interpark.com/webzine/paper/TPNoticeList_iFrame.asp?bbsno=34&pageno=" + i + "&KindOfGoods=TICKET&Genre=&sort=opendate&stext=";
            Document ticketsDoc = Jsoup.connect(url).get();

            for (Element countElement : ticketsDoc.select("tbody .count")) {
                int count = Integer.parseInt(countElement.text());
                if (count > 1000) {
                    Elements subject = countElement.parent().select(".subject a");
                    String detailUrl = "https://ticket.interpark.com/webzine/paper/" + subject.attr("href");
                    Document ticketDetailDoc = Jsoup.connect(detailUrl).get();

                    if (hasBookingBtn(ticketDetailDoc)) {
                        String title = ticketDetailDoc.select(".info h3").text();
                        String bookingUrl = ticketDetailDoc.select(".btn_book").attr("href");
                        String id = bookingUrl.substring(bookingUrl.indexOf("GoodsCode=") + 10);
                        LocalDateTime dateTime = getLocalDateTime(countElement);

                        Ticket ticket = new Ticket(id, title, "interpark", bookingUrl, dateTime, count);
                        ticketRepository.save(ticket);
                        log.info("ticket = {}", ticket);
                    }
                }
            }
        }
    }

    private static boolean hasBookingBtn(Document ticketDetailDoc) {
        return !ticketDetailDoc.select(".btn_book").isEmpty();
    }

    private LocalDateTime getLocalDateTime(Element count) {
        String date = count.parent().select(".date").text();
        date = date.substring(0, date.indexOf("(")) + date.substring(date.indexOf(")") + 1);
        return stringToLocalDateTime(date);
    }

    private LocalDateTime stringToLocalDateTime(String dateString) {
        String[] split = dateString.split(" ");
        String[] date = split[0].split("\\.");
        String[] time = split[1].split(":");
        return LocalDateTime.of(Integer.parseInt("20" + date[0]),
                Integer.parseInt(date[1]),
                Integer.parseInt(date[2]),
                Integer.parseInt(time[0]),
                Integer.parseInt(time[1]));
    }
}
