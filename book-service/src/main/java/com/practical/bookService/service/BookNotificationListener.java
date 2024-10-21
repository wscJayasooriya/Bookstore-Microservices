package com.practical.bookService.service;

import org.springframework.stereotype.Service;

@Service
public class BookNotificationListener {

    public void receiveMessage(String message) {
        System.out.println("Received notification: " + message);
    }
}
