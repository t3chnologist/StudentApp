package C196PA.BTerbish.StudentApp.UIController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Calendar;
import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Term;
import C196PA.BTerbish.StudentApp.R;

public class TermEditActivity extends AppCompatActivity{

    private StudentDatabase mStudentDb;
    private EditText mTermTitle;
    private TextView mStartDate;
    private TextView mEndDate;
    private long mTermId;
    private Term mTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_edit);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        mTermTitle = findViewById(R.id.termTitleText);
        mStartDate = findViewById(R.id.startDateText);
        mEndDate = findViewById(R.id.endDateText);

        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TermEditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mStartDate.setText(monthOfYear + "/" + dayOfMonth + "/" + year);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TermEditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mEndDate.setText(monthOfYear + "/" + dayOfMonth + "/" + year);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        Bundle bundle = getIntent().getExtras();
        mTermId = bundle.getLong("termId");

        actionBar = getSupportActionBar();

        if (mTermId == -1) {
            mTerm = new Term();
            if (actionBar != null) {
                setTitle("Add New Term");
            }
        }
        else {
            mTerm = mStudentDb.termDao().getTermById(mTermId);
            mTermTitle.setText(mTerm.getTermTitle());

            mStartDate.setText(mTerm.getStartDate());
            mEndDate.setText(mTerm.getEndDate());

            if(actionBar != null) {
                setTitle("Editing: " + mTermTitle.getText());
            }
        }

        mTermTitle.requestFocus();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_new_menu, menu);
        return true;
    }
    public void onSaveButtonClick(View view) {
        saveTerm();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.save:
                saveTerm();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveTerm() {
        mTerm.setTermTitle(mTermTitle.getText().toString());
        mTerm.setStartDate(mStartDate.getText().toString());
        mTerm.setEndDate(mEndDate.getText().toString());
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        if (mTermId == -1) {
            long newTermId = mStudentDb.termDao().insertTerm(mTerm);
            bundle.putLong("termId", newTermId);
            intent.putExtras(bundle);
        }
        else {
            mStudentDb.termDao().updateTerm(mTerm);
            bundle.putLong("termId", mTerm.getId());
            intent = new Intent(this, TermListAdapter.class);
            intent.putExtras(bundle);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}