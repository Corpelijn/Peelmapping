package design4natureserver;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Bas
 */
public class Message implements Serializable {

    private String data;
    private int sender;
    private Date time;

    public Message(Date time, int sender, String data) {
        this.data = data;
        this.sender = sender;
        this.time = time;
    }

    public int getSender() {
        return this.sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String value) {
        this.data = value;
    }

    public Date getTime() {
        return time;
    }

    public static Message getMessage(String data) throws ParseException {
        String[] strings = data.split(":");
        SimpleDateFormat formatter = new SimpleDateFormat("HH/mm/ss");
        return new Message(formatter.parse(strings[1]), Integer.parseInt(strings[2]), strings[3]);
    }
}
