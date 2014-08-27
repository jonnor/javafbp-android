package org.javafbp.android.examples.helloworld;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jon on 8/25/14.
 */
public class AssetUtils {
    static public String readAsset(Context context, String path) throws IOException {
        StringBuilder buf=new StringBuilder();
        InputStream json=context.getAssets().open(path);
        BufferedReader in= new BufferedReader(new InputStreamReader(json, "UTF-8"));
        String str;
        while ((str=in.readLine()) != null) {
            buf.append(str);
        }
        in.close();
        return buf.toString();
    }
}