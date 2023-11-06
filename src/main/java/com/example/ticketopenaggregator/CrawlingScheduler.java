package com.example.ticketopenaggregator;

import com.example.ticketopenaggregator.crawler.InterParkTicketCrawler;
import com.example.ticketopenaggregator.crawler.MelonTicketCrawler;
import com.example.ticketopenaggregator.crawler.Yes24TicketCrawler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CrawlingScheduler {

    private final InterParkTicketCrawler interParkTicketCrawler;
    private final MelonTicketCrawler melonTicketCrawler;
    private final Yes24TicketCrawler yes24TicketCrawler;
    private final SmsSender smsSender;

    public CrawlingScheduler(InterParkTicketCrawler interParkTicketCrawler, MelonTicketCrawler melonTicketCrawler, Yes24TicketCrawler yes24TicketCrawler, SmsSender smsSender) {
        this.interParkTicketCrawler = interParkTicketCrawler;
        this.melonTicketCrawler = melonTicketCrawler;
        this.yes24TicketCrawler = yes24TicketCrawler;
        this.smsSender = smsSender;
    }

    @PostConstruct
    public void init() throws IOException {
        log.info("CrawlingScheduler init");
        interParkTicketCrawler.interParkTicketCrawling();
        melonTicketCrawler.melonTicketCrawling();
        yes24TicketCrawler.yes24TicketCrawling();

//        smsSender.send();
    }
}
