package krsto.zaric.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class ShowListActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView naslov;
    private String naslovString;
    private Button btnAdd;
    private Button btnRefresh;

    private ListView list;
    private TextView emptyView;

    private CharacterTaskAdapter adapter;
    private DbHelper dbHelper;

    private Button btnHome;

    private String shared;

    private HttpHelper httpHelper;
    public static String TASKS_URL = "http://172.20.10.14:3000/tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        naslov = findViewById(R.id.tvNaslov);
        naslovString = getIntent().getStringExtra("naslov");
        naslov.setText(naslovString);

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        list = findViewById(R.id.listaZadataka);
        emptyView = findViewById(R.id.emptyView);
        list.setEmptyView(emptyView);

        adapter = new CharacterTaskAdapter(this);
        list.setAdapter(adapter);

        dbHelper = new DbHelper(this);

        shared = getIntent().getStringExtra("shared");

        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(this);

        if(shared.equals("false")) {
            btnRefresh.setVisibility(View.GONE);
        }

        Log.d("myTag", shared);

        httpHelper = new HttpHelper();

        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowListActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                CharacterModel elem = (CharacterModel) adapter.getItem(i);
                dbHelper.deleteTask(elem.getTaskId());
                adapter.removeElement(elem);

                String name = elem.getText();
                if(shared.equals("true")) {
                    Log.d("myTag", "true");
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                httpHelper.httpDelete(TASKS_URL + "/" + elem.getTaskId());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(shared.equals("false")) {
            CharacterModel[] items = dbHelper.readItems(naslovString);
            adapter.update(items);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnAdd) {
            EditText etNaziv = (EditText) findViewById(R.id.etNaziv);
            String naziv = etNaziv.getText().toString();

            if(!TextUtils.isEmpty(naziv)) {
                String id = UUID.randomUUID().toString();
                CharacterModel task = new CharacterModel(naziv, false, id);
                long rowInserted =  dbHelper.insertTask(task, naslovString);

                if(rowInserted != -1) {
                    if(shared.equals("true")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonobject = new JSONObject();

                                    try {
                                        jsonobject.put("name", naziv);
                                        jsonobject.put("list", naslovString);
                                        jsonobject.put("done", "false");
                                        jsonobject.put("taskId", id);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    httpHelper.postJSONObjectFromURL(TASKS_URL, jsonobject);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    Toast.makeText(this, "Uspesno dodavanje!", Toast.LENGTH_SHORT).show();
                    adapter.addElement(task);
                } else
                    Toast.makeText(this, "Neuspesno dodavanje!", Toast.LENGTH_SHORT).show();

            }
        }
        if(view.getId() == R.id.btnRefresh) {
            btnRefresh.setVisibility(View.GONE);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        JSONArray jsonarray = httpHelper.getJSONArrayFromURL(TASKS_URL + "/" + naslovString);

                        CharacterModel[] tasks = new CharacterModel[jsonarray.length()];

                        for(int i = 0; i < jsonarray.length(); i++){
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            String name = jsonobject.getString("name");
                            Boolean done = jsonobject.getBoolean("done");
                            String id = jsonobject.getString("taskId");

                            CharacterModel cm = new CharacterModel(name, done, id);
                            tasks[i] = cm;
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.update(tasks);
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}