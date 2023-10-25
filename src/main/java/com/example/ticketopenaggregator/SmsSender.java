package com.example.ticketopenaggregator;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class SmsSender {

    private final String API_KEY = "NCSHYMGIQNZQQGTW";
    private final String API_SECRET_KEY = "UBK83WR87ZDMB4G55RG5HHB1S1F54080";
    private final String PHONE_NUMBER = "01045490007";

    private final DefaultMessageService messageService;
    private final TicketRepository ticketRepository;

    public SmsSender(TicketRepository ticketRepository) {
        this.messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, "https://api.coolsms.co.kr");
        this.ticketRepository = ticketRepository;
    }

    public void send() {
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime today = LocalDate.now().atStartOfDay();
        List<Ticket> todayTickets = ticketRepository.findAllByDateBetweenOrderByDate(today, tomorrow);

        if (todayTickets.isEmpty()) {
            log.info("금일 티켓팅은 없습니다");
            return;
        }

        sendFirstMessage(todayTickets);

        todayTickets.forEach(ticket -> {
            String textMessage = createTextMessage(ticket);
            log.info("textMessage.length() = {}", textMessage.length());
            log.info("textMessage = {}", textMessage);

            log.info("ticket = {}", ticket);

            sendMessage(textMessage);
        });


    }

    private void sendFirstMessage(List<Ticket> todayTickets) {
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDate.now())
                .append(" 오늘의 공연 정보입니다.\n")
                .append("금일 티켓팅은 ").append(todayTickets.size()).append("건 입니다.\n");

        sendMessage(sb.toString());
    }

    private void sendMessage(String sb) {
        Message message = new Message();
        message.setTo(PHONE_NUMBER);
        message.setFrom(PHONE_NUMBER);
        message.setText(sb);

        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        log.info("response = {}", response);
    }

    private String createTextMessage(Ticket ticket) {
        StringBuilder sb = new StringBuilder();
        LocalDateTime date = ticket.getDate();
        if (ticket.getTitle().length() > 65) {
            ticket.setTitle(ticket.getTitle().substring(0, 65));
        }
        sb.append("[").append(date.getHour()).append(":").append(date.getMinute()).append("] ")
                .append("[").append(ticket.getSite()).append("] ")
                .append(ticket.getTitle())
                .append("\n");
        return sb.toString();
    }

}
