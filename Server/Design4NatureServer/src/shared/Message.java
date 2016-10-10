/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import java.io.Serializable;

/**
 *
 * @author Bas
 */
public class Message implements Serializable {

    private String data;
    private int sender;

    public Message(int sender, String data) {
        this.data = data;
        this.sender = sender;
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
}
