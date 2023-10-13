package com.example.ticketopenaggregator;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CrawlingScheduler {

    private final TicketCrawler ticketCrawler;

    public CrawlingScheduler(TicketCrawler ticketCrawler) {
        this.ticketCrawler = ticketCrawler;
    }

    @PostConstruct
    public void init() throws IOException {
        System.out.println("CrawlingScheduler.init");
        ticketCrawler.interParkTicketCrawling();
    }
}
