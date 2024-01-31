package krsto.zaric.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        LogRegFragment logRegFrag = LogRegFragment.newInstance("abc", "def");
        getSupportFragmentManager().beginTransaction().add(R.id.kontejnerLoginActivity1, logRegFrag).commit();

        Intent intentService = new Intent(LoginActivity.this, MyService.class);
        startService(intentService);
        // stopService(intentService);
    }
}