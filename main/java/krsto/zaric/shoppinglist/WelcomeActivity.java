package krsto.zaric.shoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tekst;
    private TextView smltv;
    private Button btnNewList;
    private Button btnSeeMyLists;
    private Button btnSeeSharedLists;
    private String username;

    private ListView list;
    private TextView emptyView;

    private CharacterAdapter adapter;
    private DbHelper dbHelper;

//    private Button btnShowUsers;

    private Button btnHome;

    private HttpHelper httpHelper;
    public static String LISTS_URL = "http://172.20.10.14:3000/lists";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tekst = findViewById(R.id.tvUsername);
        username = getIntent().getStringExtra("username");
        tekst.setText(username);

        btnNewList = findViewById(R.id.btnNewList);
        btnNewList.setOnClickListener(this);

        btnSeeMyLists = findViewById(R.id.btnSeeMyLists);
        btnSeeMyLists.setOnClickListener(this);

        btnSeeSharedLists = findViewById(R.id.btnSeeSharedLists);
        btnSeeSharedLists.setOnClickListener(this);

//        btnShowUsers = findViewById(R.id.btnShowUsers);
//        btnShowUsers.setOnClickListener(this);

        httpHelper = new HttpHelper();

        // =========== LISTA ===========
        list = findViewById(R.id.lista);
        emptyView = findViewById(R.id.emptyView);
        list.setEmptyView(emptyView);

        adapter = new CharacterAdapter(this);
        list.setAdapter(adapter); // povezivanje Adaptera i ListView

        dbHelper = new DbHelper(this);

        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                CharacterModel elem = (CharacterModel) adapter.getItem(i);

                String creator = dbHelper.readListCreator(elem.getText());


                if(creator.equals(username)) {
                    dbHelper.deleteList(elem.getText());
                    adapter.removeElement(elem);
                } else {
                    Toast.makeText(WelcomeActivity.this, "Not your list!", Toast.LENGTH_SHORT).show();
                }

                String name = elem.getText();
                if(elem.isBul()) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                httpHelper.httpDelete(LISTS_URL + "/" + username + "/" + name);
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

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CharacterModel elem = (CharacterModel) adapter.getItem(i);
                Intent intent = new Intent(WelcomeActivity.this, ShowListActivity.class);
                intent.putExtra("naslov", elem.getText());
                intent.putExtra("shared", String.valueOf(elem.isBul()));
                startActivity(intent);
            }
        });

        // =============================
    }

    @Override
    protected void onResume() {
        super.onResume();

        CharacterModel[] lists = dbHelper.readLists(username);
        adapter.update(lists);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnNewList) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("New List Dialog")
                    .setMessage("Are you sure you want to create new list?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what would happen when positive button is clicked
                            Intent intent = new Intent(WelcomeActivity.this, NewListActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .show();
        }
        if(view.getId() == R.id.btnSeeMyLists) {
            CharacterModel[] lists = dbHelper.readMyLists(username);
            adapter.update(lists);
        }
        if(view.getId() == R.id.btnSeeSharedLists) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        JSONArray jsonarray = httpHelper.getJSONArrayFromURL(LISTS_URL);

                        CharacterModel[] lists = new CharacterModel[jsonarray.length()];

                        for(int i = 0; i < jsonarray.length(); i++){
                            JSONObject jsonobject = jsonarray.getJSONObject(i);

                            String name = jsonobject.getString("name");
                            Boolean shared = jsonobject.getBoolean("shared");
                            String id = jsonobject.getString("_id");

                            CharacterModel cm = new CharacterModel(name, shared, id);
                            lists[i] = cm;
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.update(lists);
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
//        if(view.getId() == R.id.btnShowUsers) {
//            dbHelper.readUserFilip();
//        }
    }
}