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
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class MelonTicketCrawler {

    private final TicketRepository ticketRepository;

    public MelonTicketCrawler(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void melonTicketCrawling() throws IOException {
        for (int i = 0; i < 4; i++) {
            String url = "https://ticket.melon.com/csoon/ajax/listTicketOpen.htm";
            int orderType = 2;
            int pageIndex = i * 10 + 1;
            String schGcode = "GENRE_ALL";
            Document ticketsDoc = Jsoup.connect(url)
                    .data("orderType", String.valueOf(orderType))
                    .data("pageIndex", String.valueOf(pageIndex))
                    .data("schGcode", schGcode)
                    .post();

            for (Element countElement : ticketsDoc.select(".txt_review")) {
                int count = Integer.parseInt(countElement.text().replace(",", ""));
                if (count > 1000) {
                    Elements subject = countElement.parent().parent().select(".link_consert a");
                    String detailUrl = "https://ticket.melon.com/csoon/" + subject.attr("href").substring(2);
                    Document ticketDetailDoc = Jsoup.connect(detailUrl).get();

                    if (hasBookingBtn(ticketDetailDoc)) {
                        String title = ticketDetailDoc.select(".tit_consert").text();
                        String bookingUrl = ticketDetailDoc.select(".box_link a").attr("onclick");
                        Pattern pattern = Pattern.compile("'(\\d+)'");

                        Matcher matcher = pattern.matcher(bookingUrl);
                        if (matcher.find()) {
                            String id = matcher.group(1);
                            bookingUrl = "https://ticket.melon.com/performance/index.htm?prodId=" + id;
                            Elements dates = ticketDetailDoc.select(".txt_date");
                            LocalDateTime dateTime;
                            if (dates.size() == 3)
                                dateTime = getLocalDateTime(dates.get(2));
                            else dateTime = getLocalDateTime(dates.get(1));

                            Ticket ticket = new Ticket(id, title, "melon", bookingUrl, dateTime, count);
                            ticketRepository.save(ticket);
                            log.info("ticket = {}", ticket);
                        }
                    }
                }
            }
        }
    }

    private static boolean hasBookingBtn(Document ticketDetailDoc) {
        return !ticketDetailDoc.select(".box_link").isEmpty();
    }

    private LocalDateTime getLocalDateTime(Element dateText) {
        String dateStr = dateText.text().substring(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E) HH:mm", Locale.KOREA);
        return LocalDateTime.parse(dateStr, formatter);
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
