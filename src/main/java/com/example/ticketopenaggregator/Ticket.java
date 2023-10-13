package com.example.ticketopenaggregator;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class Ticket {

    private String id;
    private String title;
    private String url;
    private LocalDateTime date;
    private Integer count;

    public Ticket(String id, String text, String detailUrl, LocalDateTime dateTime, int count) {
        this.id = id;
        this.title = text;
        this.url = detailUrl;
        this.date = dateTime;
        this.count = count;
    }
}
