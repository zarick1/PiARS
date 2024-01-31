package krsto.zaric.shoppinglist;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    private Looper serviceLooper;
    private boolean mRun = true;

    private NotificationHelper notificationHelper;

    private CharacterAdapter adapter;
    private DbHelper dbHelper;

    private HttpHelper httpHelper;
    public static String LISTS_URL = "http://172.20.10.14:3000/lists";
    public static String USERS_URL = "http://172.20.10.14:3000/users";
    public static String TASKS_URL = "http://172.20.10.14:3000/tasks";


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationHelper = new NotificationHelper();
        httpHelper = new HttpHelper();
        adapter = new CharacterAdapter(this);
        dbHelper = new DbHelper(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRun) {
                    // =====================
                    // SINHRONIZOVANJE LOKALNE I BAZE NA SERVERU
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                JSONArray jsonarray = httpHelper.getJSONArrayFromURL(LISTS_URL);
                                JSONArray jsonarray2 = httpHelper.getJSONArrayFromURL(USERS_URL);
                                JSONArray jsonarray3 = httpHelper.getJSONArrayFromURL(TASKS_URL);

                                for(int i = 0; i < jsonarray.length(); i++){
                                    JSONObject jsonobject = jsonarray.getJSONObject(i);

                                    String name = jsonobject.getString("name");
                                    Boolean shared = jsonobject.getBoolean("shared");
                                    String creator = jsonobject.getString("creator");

                                    CharacterModel cm = new CharacterModel(name, shared, creator);

                                    if(!dbHelper.readList(name, creator)) {
                                        dbHelper.insertList(cm, creator);
                                    }
                                }

                                for(int i = 0; i < jsonarray2.length(); i++){
                                    JSONObject jsonobject = jsonarray2.getJSONObject(i);

                                    String username = jsonobject.getString("username");
                                    String password = jsonobject.getString("password");
                                    String email = jsonobject.getString("email");

                                    if(!dbHelper.readUser(username, password)) {
                                        dbHelper.insertUser(username, email, password);

                                    }
                                }

                                for(int i = 0; i < jsonarray3.length(); i++){
                                    JSONObject jsonobject = jsonarray3.getJSONObject(i);

                                    String namee = jsonobject.getString("name");
                                    String listName = jsonobject.getString("list");
                                    Boolean done = jsonobject.getBoolean("done");
                                    String taskId = jsonobject.getString("taskId");

                                    CharacterModel cm = new CharacterModel(namee, done, taskId);

                                    if(!dbHelper.readTask(taskId)) {
                                        dbHelper.insertTask(cm, listName);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    notificationHelper.createNotification(getApplicationContext(), "Synchronizing...", "The local and base on the server are synchronized");

                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service is starting", Toast.LENGTH_LONG).show();

        /*
        START_STICKY - ako sistem unisti sertvis kreiraj ga ponovo
        START_NOT_STICKY - ako sistem unisti servis nece ga ponovo kreirati
        */
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service is done", Toast.LENGTH_LONG).show();
    }
}