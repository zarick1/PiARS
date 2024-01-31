package krsto.zaric.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class NewListActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSave;
    private Button btnOk;
    private String naslov = "NASLOV";

    private CharacterAdapter adapter;
    private DbHelper dbHelper;

    private Button btnHome;

    private HttpHelper httpHelper;
    public static String USERS_URL = "http://172.20.10.14:3000/lists";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        btnOk = findViewById(R.id.btnOK);
        btnOk.setOnClickListener(this);

        adapter = new CharacterAdapter(this);
        dbHelper = new DbHelper(this);

        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewListActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        httpHelper = new HttpHelper();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnSave) {
            Intent intent = new Intent(NewListActivity.this, WelcomeActivity.class);

            String username = getIntent().getStringExtra("username");
            RadioButton rbYes = findViewById(R.id.rbYes);
            CharacterModel list;
            if(rbYes.isChecked()) {
                list = new CharacterModel(naslov, true, null);
            } else {
                list = new CharacterModel(naslov, false, null);
            }
            long rowInserted =  dbHelper.insertList(list, username);

            if(rowInserted != -1) {
                if(list.isBul()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonobject = new JSONObject();

                                try {
                                    jsonobject.put("name", naslov);
                                    jsonobject.put("creator", username);
                                    jsonobject.put("shared", true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                httpHelper.postJSONObjectFromURL(USERS_URL, jsonobject);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                Toast.makeText(this, "Uspesno dodavanje!", Toast.LENGTH_SHORT).show();
                adapter.addElement(list);
            } else
                Toast.makeText(this, "Neuspesno dodavanje!", Toast.LENGTH_SHORT).show();

            intent.putExtra("username", username);
            startActivity(intent);
        }else if(view.getId() == R.id.btnOK) {
            EditText et = (EditText) findViewById(R.id.etNaslovListe);
            naslov = et.getText().toString();
            TextView tv = findViewById(R.id.tvNaslov);
            tv.setText(naslov);
        }
    }
}