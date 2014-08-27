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
        // FIXME: add a stop port
        while (true) {
            Packet p = viewPort.receive();
            if (p != null) {
                drop(p);
                changeView((View)p.getContent());
            }
        }
    }

    private void changeView(final View newView) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Reset old click handler
                if (view != null) {
                    view.setOnClickListener(null);
                }
                // Attach new handler
                view = newView;
                if (view != null) {
                    view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (outputPort.isConnected()) {
                                outputPort.send(create(view));
                            }
                        }
                    });
                }
            }
        };
        try {
            ComponentUtils.runInMainThreadWaiting(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

