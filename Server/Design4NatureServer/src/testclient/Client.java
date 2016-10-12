/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testclient;

import shared.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bas
 */
public class Client {

    private Socket socket;
    private ObjectOutputStream sender;
    private ObjectInputStream reader;

    private int clientID = -1;

    public Client() {
    }

    public void start() throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.print("Client: Enter IP address of server: ");
        String serverAddress = input.nextLine();

        socket = new Socket(serverAddress, 11000);
        sender = new ObjectOutputStream(this.socket.getOutputStream());
        reader = new ObjectInputStream(this.socket.getInputStream());

        startMessageReader();
        startMessageWriter();
    }

    private void startMessageWriter() {
        Thread t = new Thread(() -> {
//            try {
//                sender.writeObject("51.45178566,5.48224093");
//                sender.writeObject("51.45178557,5.48223745");
//                sender.writeObject("51.45178559,5.48223926");
//                sender.writeObject("51.45178584,5.48223923");
//                sender.writeObject("51.45178607,5.48223919");
//                sender.writeObject("51.45178621,5.48223915");
//                sender.writeObject("51.45178115,5.48222395");
//                sender.writeObject("51.45178126,5.48222365");
//                sender.writeObject("51.45178135,5.48222361");
//                sender.writeObject("51.45178142,5.4822236");
//                sender.writeObject("51.45178254,5.48222344");
//                sender.writeObject("51.45178256,5.48222335");
//                sender.writeObject("51.45178256,5.48222335");
//                sender.writeObject("51.45178256,5.48222335");
//                sender.writeObject("51.45178256,5.48222335");
//                sender.writeObject("51.45178256,5.48222335");
//                sender.writeObject("51.45170754,5.48220177");
//                sender.writeObject("51.45178355,5.48222302");
//                sender.writeObject("51.45178242,5.48222308");
//                sender.writeObject("51.45178252,5.48222307");
//                sender.writeObject("51.45178642,5.48229771");
//                sender.writeObject("51.45180196,5.4824605");
//                sender.writeObject("51.45186371,5.48238954");
//                sender.writeObject("51.4518146,5.48235519");
//                sender.writeObject("51.45174246,5.48231181");
//                sender.writeObject("51.45175307,5.482311");
//                sender.writeObject("51.4518102,5.48221445");
//                sender.writeObject("51.45178576,5.48218178");
//                sender.writeObject("51.45181512,5.48217352");
//                sender.writeObject("51.45183676,5.4821691");
//                sender.writeObject("51.45185781,5.48216479");
//                sender.writeObject("51.45187039,5.48215798");
//                sender.writeObject("51.45188628,5.48215237");
//                sender.writeObject("51.45190531,5.48215028");
//                sender.writeObject("51.45193448,5.48215546");
//                sender.writeObject("51.45195376,5.48215283");
//                sender.writeObject("51.45194868,5.48216526");
//                sender.writeObject("51.45195325,5.48215042");
//                sender.writeObject("51.45197497,5.48214298");
//                sender.writeObject("51.45196924,5.48214716");
//                sender.writeObject("51.4519727,5.48215154");
//                sender.writeObject("51.45196558,5.48217033");
//                sender.writeObject("51.45195305,5.48215957");
//                sender.writeObject("51.4519531,5.48215952");
//                sender.writeObject("51.45195313,5.48215949");
//                sender.writeObject("51.45173232,5.48186497");
//                sender.writeObject("51.4517612,5.48185235");
//                sender.writeObject("51.45177044,5.48184685");
//                sender.writeObject("51.45176349,5.48182918");
//                sender.writeObject("51.45175844,5.48178993");
//                sender.writeObject("51.45175643,5.48176419");
//                sender.writeObject("51.45176076,5.48174178");
//                sender.writeObject("51.45177867,5.48175774");
//                sender.writeObject("51.45178787,5.48175304");
//                sender.writeObject("51.45175937,5.48173453");
//                sender.writeObject("51.45174248,5.48173169");
//                sender.writeObject("51.45171207,5.4817192");
//                sender.writeObject("51.45167769,5.48167636");
//                sender.writeObject("51.45149679,5.48130211");
//                sender.writeObject("51.45145916,5.48124514");
//                sender.writeObject("51.45143908,5.4811983");
//                sender.writeObject("51.45141615,5.48114356");
//                sender.writeObject("51.45140743,5.48111358");
//                sender.writeObject("51.45144154,5.48113782");
//                sender.writeObject("51.45146989,5.48115657");
//                sender.writeObject("51.45149141,5.48116656");
//                sender.writeObject("51.45151047,5.48118308");
//                sender.writeObject("51.45151973,5.48120769");
//                sender.writeObject("51.45151759,5.48126158");
//                sender.writeObject("51.45151408,5.4813184");
//                sender.writeObject("51.45151239,5.48136087");
//                sender.writeObject("51.45150554,5.48139092");
//                sender.writeObject("51.45149801,5.48141601");
//                sender.writeObject("51.45149029,5.48143123");
//                sender.writeObject("51.45148227,5.48142585");
//                sender.writeObject("51.45147621,5.48142404");
//                sender.writeObject("51.45146667,5.48143106");
//                sender.writeObject("51.45145343,5.48142241");
//                sender.writeObject("51.45144153,5.48142074");
//                sender.writeObject("51.45143349,5.48144071");
//                sender.writeObject("51.45142594,5.48147372");
//                sender.writeObject("51.4514223,5.4814966");
//                sender.writeObject("51.45142539,5.48152169");
//                sender.writeObject("51.45143181,5.48154135");
//                sender.writeObject("51.45143941,5.48156066");
//                sender.writeObject("51.45144761,5.48158203");
//                sender.writeObject("51.45145727,5.48160373");
//                sender.writeObject("51.45146597,5.48161879");
//                sender.writeObject("51.4514749,5.48163601");
//                sender.writeObject("51.45147976,5.48165835");
//                sender.writeObject("51.45148636,5.48168251");
//                sender.writeObject("51.45149205,5.4817107");
//                sender.writeObject("51.45149755,5.48174125");
//                sender.writeObject("51.4515036,5.48176585");
//                sender.writeObject("51.45149757,5.48179635");
//                sender.writeObject("51.45148984,5.48182213");
//                sender.writeObject("51.45148057,5.4818433");
//                sender.writeObject("51.45147363,5.48186532");
//                sender.writeObject("51.45147103,5.48188409");
//                sender.writeObject("51.45147656,5.48190754");
//                sender.writeObject("51.45148634,5.48193103");
//                sender.writeObject("51.45149432,5.48195019");
//                sender.writeObject("51.45150233,5.4819677");
//                sender.writeObject("51.45150342,5.48197054");
//                sender.writeObject("51.451507,5.48197596");
//                sender.writeObject("51.45150728,5.4819869");
//                sender.writeObject("51.45149963,5.4820134");
//                sender.writeObject("51.45150015,5.48204195");
//                sender.writeObject("51.4515003,5.48206695");
//                sender.writeObject("51.45149005,5.48209536");
//                sender.writeObject("51.45148465,5.48212373");
//                sender.writeObject("51.45147618,5.48214789");
//                sender.writeObject("51.45146068,5.48216501");
//                sender.writeObject("51.45143868,5.48217155");
//                sender.writeObject("51.45141742,5.48216799");
//                sender.writeObject("51.45139405,5.48216562");
//                sender.writeObject("51.45137762,5.48216647");
//                sender.writeObject("51.45136726,5.48217906");
//                sender.writeObject("51.45136062,5.48220368");
//                sender.writeObject("51.45136161,5.48223264");
//                sender.writeObject("51.4513624,5.48225902");
//                sender.writeObject("51.45136417,5.48228372");
//                sender.writeObject("51.4513666,5.48230364");
//                sender.writeObject("51.45137066,5.48232194");
//                sender.writeObject("51.45137804,5.48234175");
//                sender.writeObject("51.45138613,5.4823592");
//                sender.writeObject("51.45139045,5.48237655");
//                sender.writeObject("51.45139097,5.48239542");
//                sender.writeObject("51.45139307,5.48241612");
//                sender.writeObject("51.45139126,5.48243645");
//                sender.writeObject("51.45139038,5.48245691");
//                sender.writeObject("51.45138671,5.48247606");
//                sender.writeObject("51.4513863,5.48249517");
//                sender.writeObject("51.45138693,5.48251519");
//                sender.writeObject("51.45138619,5.48252935");
//                sender.writeObject("51.45138661,5.48254442");
//                sender.writeObject("51.4513877,5.4825584");
//                sender.writeObject("51.45139357,5.48257546");
//                sender.writeObject("51.45139704,5.48259373");
//                sender.writeObject("51.4514001,5.48261111");
//                sender.writeObject("51.45140184,5.48262777");
//                sender.writeObject("51.45140506,5.48264");
//                sender.writeObject("51.45140865,5.48265863");
//                sender.writeObject("51.45140989,5.4826791");
//                sender.writeObject("51.45141017,5.48269513");
//                sender.writeObject("51.45141571,5.48271353");
//                sender.writeObject("51.45142088,5.48273621");
//                sender.writeObject("51.45142403,5.48275533");
//                sender.writeObject("51.45142427,5.48278014");
//                sender.writeObject("51.45142313,5.48280325");
//                sender.writeObject("51.45142208,5.48282453");
//                sender.writeObject("51.45141777,5.48284816");
//                sender.writeObject("51.45141357,5.48286914");
//                sender.writeObject("51.45141029,5.48289171");
//                sender.writeObject("51.45141041,5.48291296");
//                sender.writeObject("51.45141195,5.48293141");
//                sender.writeObject("51.45141425,5.48295054");
//                sender.writeObject("51.45141683,5.4829689");

//            } catch (IOException ex) {
//            }
            while (true) {
                try {
                    Scanner input = new Scanner(System.in);
                    String newMessage = input.nextLine();

                    sender.writeObject(newMessage);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        t.start();
    }

    private void startMessageReader() {
        Thread t = new Thread(() -> {
            while (true) {
                Message object = null;
                try {
                    object = (Message) reader.readObject();
                } catch (IOException | ClassNotFoundException ex) {
                    continue;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
    }

    public void sendMessage(Message message) {
        try {
            sender.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getClientId() {
        return this.clientID;
    }
}
