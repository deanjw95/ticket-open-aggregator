package com.example.ticketopenaggregator;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CrawlingScheduler {

    private final InterParkTicketCrawler interParkTicketCrawler;
    private final MelonTicketCrawler melonTicketCrawler;

    public CrawlingScheduler(InterParkTicketCrawler interParkTicketCrawler, MelonTicketCrawler melonTicketCrawler) {
        this.interParkTicketCrawler = interParkTicketCrawler;
        this.melonTicketCrawler = melonTicketCrawler;
    }

    @PostConstruct
    public void init() throws IOException {
        log.info("CrawlingScheduler init");
        interParkTicketCrawler.interParkTicketCrawling();
        melonTicketCrawler.melonTicketCrawling();
    }
}
