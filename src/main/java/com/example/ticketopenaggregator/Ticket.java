package com.example.ticketopenaggregator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TICKET")
@Data
@NoArgsConstructor
public class Ticket {

    @Id
    private String id;
    @Column
    private String title;
    @Column
    private String url;
    @Column
    private LocalDateTime date;
    @Column
    private Integer count;

    public Ticket(String id, String text, String detailUrl, LocalDateTime dateTime, int count) {
        this.id = id;
        this.title = text;
        this.url = detailUrl;
        this.date = dateTime;
        this.count = count;
    }
}
