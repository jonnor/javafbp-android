package org.javafbp.android.components;

import android.app.*;
import android.os.*;
import android.view.*;
import com.jpmorrsn.fbp.engine.*;

/**
 * Created by jon on 8/27/14.
 */
@InPorts({
    @InPort(value="VIEW", type=View.class)
})
@OutPort(value="OUT", type=View.class, optional = true)
public class ClickListener extends Component {

    private OutputPort outputPort;
    private InputPort viewPort;
    private View view;

    @Override
    protected void openPorts() {
        viewPort = openInput("VIEW");
        outputPort = openOutput("OUT");
    }

    @Override
    protected void execute() {
        final Packet p = viewPort.receive();
        if (p != null) {
            final View newView = (View)p.getContent();
            drop(p);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    // Reset old click handler
                    view.setOnClickListener(null);
                    // Set new
                    view = newView;
                    view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (outputPort.isConnected()) {
                                outputPort.send(create(view));
                            }
                        }
                    });
                }
            };
            mainHandler.post(myRunnable);
        }
    }
}

