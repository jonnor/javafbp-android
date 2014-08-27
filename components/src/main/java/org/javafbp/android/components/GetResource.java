package org.javafbp.android.components;

import android.app.*;
import android.content.Context;
import android.content.res.Resources;
import android.os.*;
import android.view.*;
import com.jpmorrsn.fbp.engine.*;

import java.lang.reflect.Field;

/**
 * Created by jon on 8/27/14.
 */
@OutPort(value="OUT", type=Integer.class)
@InPorts({
        @InPort(value="CONTEXT", type=Context.class),
        @InPort(value="NAME", type=String.class),
})
public class GetResource extends Component {

    private OutputPort outputPort;
    private InputPort namePort;
    private InputPort contextPort;

    @Override
    protected void openPorts() {
        contextPort = openInput("CONTEXT");
        namePort = openInput("NAME");
        outputPort = openOutput("OUT");
    }

    @Override
    protected void execute() {

        // Context
        Context context = null;
        {
            Packet p = contextPort.receive();
            if (p != null) {
                context = (Context)p.getContent();
                drop(p);
            }
        }

        // Name
        {
            Packet p = namePort.receive();
            if (p != null) {
                String fullName = (String) p.getContent();
                drop(p);
                int lastDot = fullName.lastIndexOf(".");
                final String type = fullName.substring(0, lastDot);
                final String name = fullName.substring(lastDot + 1);
                try {
                    Resources r = context.getResources();
                    Integer id = r.getIdentifier(name, type, context.getPackageName());
                    outputPort.send(create(id));
                } catch (Exception e) {
                    // FIXME: send on error port
                    e.printStackTrace();
                }

            }
        }
    }

    private static Object getFieldValue(String path) throws Exception {
        int lastDot = path.lastIndexOf(".");
        String className = path.substring(0, lastDot);
        String fieldName = path.substring(lastDot + 1);
        Class myClass = Class.forName(className);
        Field myField = myClass.getDeclaredField(fieldName);
        return myField.get(null);
    }
}
