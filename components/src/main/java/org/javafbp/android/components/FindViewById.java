package org.javafbp.android.components;

import android.app.*;
import android.os.*;
import android.view.*;
import com.jpmorrsn.fbp.engine.*;

/**
 * Created by jon on 8/27/14.
 */
@OutPort(value="OUT", type=View.class, optional = true)
@InPorts({
        @InPort(value="ACTIVITY", type=Activity.class),
        @InPort(value="ID", type=Integer.class),
})
public class FindViewById extends Component {

    private OutputPort outputPort;
    private InputPort idPort;
    private InputPort activityPort;
    private Activity activity;

    @Override
    protected void openPorts() {
        idPort = openInput("ID");
        activityPort = openInput("ACTIVITY");
        outputPort = openOutput("OUT");
    }

    @Override
    protected void execute() {

        // Activity
        {
            Packet p = activityPort.receive();
            if (p != null) {
                activity = (Activity)p.getContent();
                drop(p);
            }
        }

        // ID
        {
            final Packet p = idPort.receive();
            if (p != null) {
                final Integer viewId = (Integer)p.getContent();
                drop(p);
                final Component self = this;
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        View view = activity.findViewById(viewId);
                        if (outputPort.isConnected()) {
                             outputPort.send(create(view));
                        }
                    }
                };
                try {
                    ComponentUtils.runInMainThreadWaiting(myRunnable);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

