package org.javafbp.android.examples.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jpmorrsn.fbp.engine.*;
import com.jpmorrsn.fbp.engine.Runtime;

import org.javafbp.android.examples.helloworld.R;
import org.javafbp.android.examples.helloworld.R.drawable;

public class HelloWorldActivity extends Activity {

    Thread thread = null;

    public class NetworkRunnable implements Runnable {
        private Runtime.RuntimeNetwork mNetwork;

        NetworkRunnable(Runtime.RuntimeNetwork net) {
            mNetwork = net;
        }
        public void run() {
            try {
                mNetwork.go();
            } catch (Exception e) {
                // FIXME: give exception out as onError callback
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        Runtime.ComponentLibrary lib = null;
        try {
            String fbpJsonContent = AssetUtils.readAsset(getBaseContext(), "fbp.json");
            lib = new Runtime.ComponentLibrary();
            lib.loadFromJson(fbpJsonContent);
        } catch (Exception e) {
            System.err.print("Failed to load component library");
            e.printStackTrace();
        }

        // Create intent. TODO: create component(s) for this
        String url = "http://github.com/jonnor/javafbp-android";
        Intent t = new Intent(this, HelloWorldActivity.class);
        if (url != null && !url.isEmpty()) {
            t = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }
        final Intent targetIntent = t;

        Runtime.Definition def = new Runtime.Definition();
        try {
            def.loadFromJson(AssetUtils.readAsset(getBaseContext(), "showNotification.json"));
            final String button = "getButton";
            def.addInitial(button, "activity", this);
            final String notify = "notify";
            def.addInitial(notify, "context", this);
            def.addInitial(notify, "target", targetIntent);
            def.addInitial(notify, "id", 1); // FIXME: should be optional
            // FIXME: use a Split + exported inports to avoid passing Context/this many times
            def.addInitial("buttonId", "context", this);
            def.addInitial("iconId", "context", this);
            // TEMP: network should be long running and fire triggered by click
            //def.addInitial(notify, "fire", true);
            Runtime.RuntimeNetwork net = new Runtime.RuntimeNetwork(lib, def);
            NetworkRunnable runnable = new NetworkRunnable(net);
            thread = new Thread(runnable);
            thread.start();
        } catch (Exception e) {
            System.err.print("Network execution failed");
            e.printStackTrace();
        }
        System.err.print("OnCreate FINISHED");
    }
}
