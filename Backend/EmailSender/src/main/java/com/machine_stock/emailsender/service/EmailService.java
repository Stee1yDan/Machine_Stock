package com.machine_stock.emailsender.service;

public interface EmailService
{
    void sendBasicEmailMessage(String receiverEmail);
    void sendMimeMessageWithAttachment(String receiverEmail);
    void sendHtmlPage(String receiverEmail);
}
