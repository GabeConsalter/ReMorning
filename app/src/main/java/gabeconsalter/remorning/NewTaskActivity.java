package gabeconsalter.remorning;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NewTaskActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private TextView txtNewWhat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initViews();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        disableFab();

    }

    public boolean onKeyUp(int keyCode, KeyEvent event){
        Toast.makeText(this, "digitou", Toast.LENGTH_SHORT).show();

        return false;
    }

    public void enableFab(){
        fab.setEnabled(true);
        fab.getBackground().setAlpha(100);
    }

    public void disableFab(){
        fab.setEnabled(false);
        fab.getBackground().setAlpha(80);
    }

    public void initViews(){
        fab = (FloatingActionButton) findViewById(R.id.fab);
        txtNewWhat = (TextView) findViewById(R.id.txtNewWhat);
    }

}
