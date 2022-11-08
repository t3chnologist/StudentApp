package C196PA.BTerbish.StudentApp.UIController;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Term;
import C196PA.BTerbish.StudentApp.R;

public class TermEditActivity extends AppCompatActivity {

    public static final String EXTRA_TERM_ID = "C196PA.BTerbish.StudentApp.Entity.term";
    private StudentDatabase mStudentDb;
    private EditText mTermTitle;
    private EditText mStartDate;
    private EditText mEndDate;
    private long mTermId;
    private Term mTerm;
    private final int REQUEST_CODE_UPDATE_TERM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_edit);
        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        mTermTitle = findViewById(R.id.termTitleText);
        mStartDate = findViewById(R.id.startDateText);
        mEndDate = findViewById(R.id.endDateText);

        Intent intent = getIntent();
        mTermId = intent.getLongExtra(EXTRA_TERM_ID, -1);

        ActionBar actionBar = getSupportActionBar();

        if (mTermId == -1) {
            mTerm = new Term();
            if (actionBar != null) {
                setTitle("Add New Term");
            }
        }
        else {
            mTerm = mStudentDb.termDao().getTerm(mTermId);
            mTermTitle.setText(mTerm.getTermTitle());

            mStartDate.setText(mTerm.getStartDate());
            mEndDate.setText(mTerm.getEndDate());

            if(actionBar != null) {
                setTitle("Edit term");
            }
        }
    }

    public void onSaveButtonClick(View view) {
        mTerm.setTermTitle(mTermTitle.getText().toString());
        mTerm.setStartDate(mStartDate.getText().toString());
        mTerm.setEndDate(mEndDate.getText().toString());
        Intent intent = new Intent();

        if (mTermId == -1) {
            long termId = mStudentDb.termDao().insertTerm(mTerm);
            intent.putExtra(EXTRA_TERM_ID, termId);
        }
        else {
            mStudentDb.termDao().updateTerm(mTerm);
            intent = new Intent(this, TermListAdapter.class);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}