package com.machine_stock.service;

public interface EmailService
{
    void sendBasicEmailMessage(String receiverEmail, String token);
    void sendMimeMessageWithAttachment(String receiverEmail, String token, String filePath);
    void sendHtmlPage(String receiverEmail, String token);
}
