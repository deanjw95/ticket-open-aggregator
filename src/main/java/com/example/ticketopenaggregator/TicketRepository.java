package com.example.ticketopenaggregator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    List<Ticket> findAllByDateBetweenOrderByDate(LocalDateTime today, LocalDateTime tomorrow);
}
