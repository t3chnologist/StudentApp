package C196PA.BTerbish.StudentApp.UIController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
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
    private Calendar calendar;
    private int year;
    private int month;
    private int day;
    private DatePickerDialog datePickerDialog;

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
        Bundle bundle = getIntent().getExtras();
        mTermId = bundle.getLong("termId");

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

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
    public void onSaveButtonClick(View view) throws ParseException {
        minimizeKeyboard(view);
        saveTerm();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.save:
                try {
                    saveTerm();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTerm() throws ParseException {

        if (inputValidated()) {
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

    public void onClickStartDate(View view) {
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mStartDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                mStartDate.setError(null);
                mEndDate.setError(null);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void onClickEndDate(View view) {
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mEndDate.setText((monthOfYear + 1) +  "/" + dayOfMonth + "/" + year);
                mStartDate.setError(null);
                mEndDate.setError(null);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private Calendar converter(String date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        cal.setTime(sdf.parse(date));
        return cal;
    }

    public void minimizeKeyboard(View view) {
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean inputValidated() throws ParseException {
        //check for missing input
        boolean noMissingInput = Stream.of(mTermTitle.getText().toString(),
                                            mStartDate.getText().toString(),
                                            mEndDate.getText().toString())
                                    .noneMatch(String::isEmpty);

        if (noMissingInput) {

            mTermTitle.setError(null);
            mStartDate.setError(null);
            mEndDate.setError(null);

            String termTitle = mTermTitle.getText().toString();
            Calendar tempStartDate = converter(mStartDate.getText().toString());
            Calendar tempEndDate = converter(mEndDate.getText().toString());

            for (Term term : mStudentDb.termDao().getTerms()) {

                //check for duplicate term title
                String otherTitle = term.getTermTitle();
                if ((mTermId != term.getId()) && (Objects.equals(termTitle, otherTitle))) {
                    mTermTitle.setError("Duplicate term title");
                    Toast.makeText(this, mTermTitle.getError(), Toast.LENGTH_SHORT).show();
                }

                //check for start & end date overlap
                Calendar otherStartDate = converter(term.getStartDate().toString());
                Calendar otherEndDate = converter(term.getEndDate().toString());
                if ((tempStartDate.after(otherStartDate) && tempStartDate.before(otherEndDate)) ||
                        (tempEndDate.after(otherStartDate) && tempEndDate.before(otherEndDate))) {
                    mStartDate.setError("Invalid start/end");
                    mEndDate.setError("Invalid start/end");
                    Toast.makeText(this, "Dates overlap with " + term.getTermTitle(),
                                    Toast.LENGTH_SHORT).show();
                }
            }

            //check start & end dates
            if (tempEndDate.before(tempStartDate)) {
                mStartDate.setError("Invalid start/end");
                mEndDate.setError("Invalid start/end");
                Toast.makeText(this, mStartDate.getError(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (mTermTitle.getText().toString().isEmpty()) {mTermTitle.setError("Title is required");}
            if (mStartDate.getText().toString().isEmpty()) {mStartDate.setError("Missing start");}
            if (mEndDate.getText().toString().isEmpty()) {mEndDate.setError("Missing end");}
            Toast.makeText(this, "Missing field", Toast.LENGTH_SHORT).show();
        }


        //returns true if no error
        return Stream.of(mTermTitle.getError(), mStartDate.getError(), mEndDate.getError())
                    .allMatch(Objects::isNull);
    }
}