package ru.immmus;

import java.util.ArrayList;
import java.util.List;

class MessagesTestData {
    private MessagesTestData() {
    }

    final static List<Message> messageList = new ArrayList<Message>() {
        {
            add(new Message("123", "num", "Екатеринбург", "0xcv11cs", "None", "req11"));
            add(new Message("12", "num1", "Екатеринбург", "0xcv11cs", "Nan", "req11"));
            add(new Message("12er", "num12", "321.23", "0xcv11cs", "None", "req11"));
            add(new Message("12sd", "num13", "321.23", "0xcv11cs", "Nan", "req11"));
        }
    };
}
