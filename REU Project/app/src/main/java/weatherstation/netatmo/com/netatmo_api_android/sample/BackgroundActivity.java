package weatherstation.netatmo.com.netatmo_api_android.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import android.util.Log;


import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weatherstation.netatmo.com.netatmo_api_android.api.NetatmoUtils;
import weatherstation.netatmo.com.netatmo_api_android.api.model.Measures;
import weatherstation.netatmo.com.netatmo_api_android.api.model.Module;
import weatherstation.netatmo.com.netatmo_api_android.api.model.Params;
import weatherstation.netatmo.com.netatmo_api_android.api.model.Station;
import android.widget.Button;
import java.io.File;
import android.app.Service;


public class BackgroundActivity extends Service {

    public static final String TAG = "MainActivity";
    CustomAdapter mAdapter;
    List<Module> mListItems = new ArrayList<>();
    Station station;
    List<Module> modules = new ArrayList<>();
    List<Station> mDevices;
    int mCompletedRequest;
    String logger;


    SampleHttpClient sampleHttpClient;

    Handler handler = new Handler();
    final Thread t=new Thread(new Runnable() {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            Log.d(Thread.currentThread().toString(), "Here");
            while(true) {
                try {
                    Thread.sleep(600000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                RunTime();
                System.out.println(readFromSDCard());

            }
        }
    });

    protected void onCreate(Bundle savedInstanceState) {

        logger="";
        Log.d(Thread.currentThread().toString(), "Here");
        System.out.println(Thread.currentThread().toString());
        System.out.println("Main");

        sampleHttpClient = new SampleHttpClient(this);
        if(sampleHttpClient.getAccessToken() != null){
            //if the user is already logged
        }else {
            //else, stats the LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
        }

    }
    //Recurring update
    public void RunTime(){
        handler.post(new Runnable() {
            @Override
            public void run() {
            }
        });
        mCompletedRequest = modules.size();

        if (!mListItems.isEmpty()) {
            mListItems.clear();
            mAdapter.notifyDataSetChanged();
        }

        final String[] types = new String[]{
                Params.TYPE_NOISE,
                Params.TYPE_CO2,
                Params.TYPE_PRESSURE,
                Params.TYPE_HUMIDITY,
                Params.TYPE_TEMPERATURE,
                Params.TYPE_RAIN,
                Params.TYPE_RAIN_SUM_1,
                Params.TYPE_RAIN_SUM_24,
                Params.TYPE_WIND_ANGLE,
                Params.TYPE_WIND_STRENGTH,
                Params.TYPE_GUST_ANGLE,
                Params.TYPE_GUST_STRENGTH
        };
        sampleHttpClient.getDevicesList(
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject res = null;
                        try {
                            res = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (res != null) {
                            HashMap<String, Measures> measuresHashMap = NetatmoUtils.parseMeasures(res, types);
                            Module module=modules.get(0);
                            if (measuresHashMap.containsKey(module.getId())) {
                                module.setMeasures(measuresHashMap.get(module.getId()));
                                Measures m = module.getMeasures();
                                logger = m.getBeginTime() + "," + m.getTemperature() + "," + m.getHumidity() + "," + m.getPressure() + ",";
                                writeToSDFile(logger);
                                mListItems.add(module);
                            }
                            mAdapter.notifyDataSetChanged();

                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }
                });


    }


    private void writeToFile(String data) {
        String write=readFromFile().concat(data);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("REUCSV.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(write);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private void clearFile(){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("REUCSV.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("REUCSV.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }






    /**
     * Method to check whether external media available and writable. This is adapted from
     * http://developer.android.com/guide/topics/data/data-storage.html#filesExternal
     */



    /**
     * Method to write ascii text characters to file on SD card. Note that you must add a
     * WRITE_EXTERNAL_STORAGE permission to the manifest file or this method will throw
     * a FileNotFound Exception because you won't have write permission.
     */

    private void writeToSDFile(String logger) {

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();
        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File(root.getAbsolutePath() + "/Download");
        dir.mkdirs();
        File file = new File(dir, "REUCSV.txt");
        String write=readFromSDCard().concat(logger);
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.print(write);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String readFromSDCard(){
        File root = android.os.Environment.getExternalStorageDirectory();

        final File file = new File(root.getAbsolutePath() + "/Download/REUCSV.txt");
        System.out.println(file.getAbsolutePath());
        String ret="";
        try {
            InputStream inputStream = new FileInputStream(file);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("hi","Service");
        return null;
    }
}

