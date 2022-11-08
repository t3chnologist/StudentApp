package C196PA.BTerbish.StudentApp.UIController;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import C196PA.BTerbish.StudentApp.R;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
    }

    public void onEnterButtonClick (View view) {
        Intent intent = new Intent(this, TermListAdapter.class);
        startActivity(intent);
    }
}