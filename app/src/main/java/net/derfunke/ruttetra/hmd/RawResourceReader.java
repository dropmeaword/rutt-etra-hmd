package net.derfunke.ruttetra.hmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

public class RawResourceReader {

    /**
     * Load a given resource as a string, it is used to load the shaders from the resource pile.
     *
     * @param ctxt the activity's context
     * @param resId
     * @return
     */
    public static String readRawTextResource(final Context ctxt, int resId)
    {
        InputStream inputStream = ctxt.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n'); // because readLine skips line termination
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }


}
