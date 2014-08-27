package org.javafbp.android.components;

import java.util.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.NotificationCompat;

import com.jpmorrsn.fbp.engine.*;

// Put the message into a notification and post it.

@OutPort(value="OUT", type=Boolean.class, optional = true)
@InPorts({
        @InPort(value="CONTEXT", type=Context.class),
        @InPort(value="TITLE", type=String.class),
        @InPort(value="TEXT", type=String.class),  // TODO: make optional
        @InPort(value="TARGET", type=Intent.class), // TODO: make optional
        @InPort(value="ID", type=Integer.class), // TODO: make optional
        @InPort(value="ICON", type=Integer.class), // TODO: make optional
        @InPort(value="FIRE", type=Object.class)
})
public class ShowNotification extends Component {

    private OutputPort outputPort;
    private Map<String, InputPort> inputPorts = new HashMap<String, InputPort>();
    private String[] portNames = {"CONTEXT", "TITLE", "TEXT", "TARGET", "ID", "ICON", "FIRE"};
    private Map<String, Object> lastValue = new HashMap<String, Object>();

    @Override
    protected void openPorts() {
        for (String name : portNames) {
            inputPorts.put(name, openInput(name));
        }
        outputPort = openOutput("OUT");
    }

    @Override
    protected void execute() {
        for (String name : portNames) {
            InputPort port = inputPorts.get(name);
            final Packet packet = port.receive();
            Object o = packet.getContent();
            drop(packet);
            if (o != null && name != "FIRE") {
                lastValue.put(name, o);
            } else if (name == "FIRE") {
                // Fire notification
                final Context context = (Context)lastValue.get("CONTEXT");
                final String text = (String)lastValue.get("TEXT");
                final String title = (String)lastValue.get("TITLE");
                final Intent target = (Intent)lastValue.get("TARGET");
                final Integer id = (Integer)lastValue.get("ID");
                final Integer icon = (Integer)lastValue.get("ICON");
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        sendNotification(context, id, title, text, icon, target);
                        // Send confirmation
                        if (outputPort.isConnected()) {
                            outputPort.send(create(true));
                        }
                    }
                };
                mainHandler.post(myRunnable);
            } else {

            }
        }
    }

    // Not threadsafe!
    private static void sendNotification(Context context, int notificationId,
                                         String title, String text, int iconId, Intent target) {
        if (context == null || title == null || text == null || target == null) {
            System.err.print("NULL data in SendNotification");
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(iconId)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentText(text);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, target, 0);
        mBuilder.setContentIntent(contentIntent);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificationId, notification);
    }

}
