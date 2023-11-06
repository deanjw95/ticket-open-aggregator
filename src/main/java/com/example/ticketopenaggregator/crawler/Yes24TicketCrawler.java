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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class Yes24TicketCrawler {

    private final TicketRepository ticketRepository;

    public Yes24TicketCrawler(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void yes24TicketCrawling() throws IOException {
        for (int i = 1; i < 3; i++) {
            String url = "http://ticket.yes24.com/New/Notice/Ajax/axList.aspx";
            int size = 20;
            int order = 2;
            String searchType = "All";

            Document ticketsDoc = Jsoup.connect(url)
                    .data("page", String.valueOf(i))
                    .data("size", String.valueOf(size))
                    .data("order", String.valueOf(order))
                    .data("searchType", searchType)
                    .post();


            for (Element element : ticketsDoc.select("tbody tr")) {
                try {
                    int count = Integer.parseInt(element.child(3).text().replace(",", ""));
                    if (count > 1000) {
                        Elements subject = element.child(1).select("a");
                        String detailUrl = "http://ticket.yes24.com/New/Notice/Ajax/axRead.aspx";
                        LocalDateTime dateTime = getLocalDateTime(element);
                        Document ticketDetailDoc = Jsoup.connect(detailUrl)
                                .data("bId", subject.attr("href").substring(4))
                                .post();

                        if (hasBookingBtn(ticketDetailDoc)) {
                            String title = ticketDetailDoc.select(".noti-vt-tit").text();
                            String href = ticketDetailDoc.select(".noti-vt-btns:contains(상세보기) a").attr("href");

                            Pattern pattern = Pattern.compile("(\\d+)");
                            Matcher matcher = pattern.matcher(href);
                            if (matcher.find()) {
                                String id = matcher.group(1);
                                String bookingUrl = "http://ticket.yes24.com/Perf/" + id;

                                Ticket ticket = new Ticket(id, title, "yes24", bookingUrl, dateTime, count);
                                ticketRepository.save(ticket);
                                log.info("ticket = {}", ticket);
                            }
                        }
                    }
                } catch (NumberFormatException e) {

                }
            }
        }
    }

    private static boolean hasBookingBtn(Document ticketDetailDoc) {
        Elements select = ticketDetailDoc.select(".noti-vt-btns:contains(상세보기)");
        return !select.isEmpty();
    }

    private LocalDateTime getLocalDateTime(Element element) {
        String date = element.child(2).text();
        if (date.equals("추후공지")) {
            return null;
        } else {
            date = date.substring(0, date.indexOf("(")) + date.substring(date.indexOf(")") + 1);
            return stringToLocalDateTime(date);
        }
    }

    private LocalDateTime stringToLocalDateTime(String dateString) {
        String[] split = dateString.split(" ");
        String[] date = split[0].split("\\.");
        String[] time = split[1].split(":");
        return LocalDateTime.of(Integer.parseInt(date[0]),
                Integer.parseInt(date[1]),
                Integer.parseInt(date[2]),
                Integer.parseInt(time[0]),
                Integer.parseInt(time[1]));
    }
}
