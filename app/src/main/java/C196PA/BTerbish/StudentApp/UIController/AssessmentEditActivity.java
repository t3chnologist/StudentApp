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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Assessment;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.R;

public class AssessmentEditActivity extends AppCompatActivity {

    private StudentDatabase mStudentDb;
    private EditText mAssessmentTitle;
    private TextView mStartDate;
    private TextView mEndDate;
    private RadioButton mPerformanceAssessmentRB;
    private RadioButton mObjectiveAssessmentRB;
    private long mAssessmentId;
    private long mCourseId;
    private Assessment mAssessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_edit);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        mAssessmentTitle = findViewById(R.id.assessmentTitleEdit);
        mStartDate = findViewById(R.id.assessmentStartDateEdit);
        mEndDate = findViewById(R.id.assessmentEndDateEdit);
        mPerformanceAssessmentRB = findViewById(R.id.performanceAssessmentEditRB);
        mObjectiveAssessmentRB = findViewById(R.id.objectiveAssessmentEditRB);

        Bundle bundle = getIntent().getExtras();
        mAssessmentId = bundle.getLong("assessmentId");
        mCourseId = bundle.getLong("courseId");

        if(mAssessmentId == -1) {
            setTitle(mStudentDb.courseDao().getCourseById(mCourseId).getCourseTitle()
                                                    + ": New Assessment");
            mAssessment = new Assessment();
        }
        else {
            mAssessment = mStudentDb.assessmentDao().getAssessmentById(mAssessmentId);
            setTitle("Editing: " + mAssessment.getAssessmentTitle());
            mAssessmentTitle.setText(mAssessment.getAssessmentTitle());
            mStartDate.setText(mAssessment.getAssessmentStart());
            mEndDate.setText(mAssessment.getAssessmentEnd());
            if (mAssessment.getAssessmentType() == mPerformanceAssessmentRB.getText()) {
                mPerformanceAssessmentRB.setChecked(true);
            }
            else {
                mObjectiveAssessmentRB.setChecked(true);
            }
        }

        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AssessmentEditActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AssessmentEditActivity.this, new DatePickerDialog.OnDateSetListener() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_new_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.save:
                saveAssessment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveAssessmentButtonClick(View view) {
        saveAssessment();
    }

    public void saveAssessment() {
        mAssessment.setAssessmentTitle(mAssessmentTitle.getText().toString());
        mAssessment.setAssessmentStart(mStartDate.getText().toString());
        mAssessment.setAssessmentEnd(mEndDate.getText().toString());


        if (mPerformanceAssessmentRB.isChecked()) {
            mAssessment.setAssessmentType(mPerformanceAssessmentRB.getText().toString());
        }
        else {
            mAssessment.setAssessmentType(mObjectiveAssessmentRB.getText().toString());
        }

        if (mAssessmentId == -1) {
            mAssessment.setCourse(mCourseId);
            mAssessmentId = mStudentDb.assessmentDao().insertAssessment(mAssessment);
        }
        else {
            mStudentDb.assessmentDao().updateAssessment(mAssessment);
        }

        Intent intent = new Intent(this, AssessmentListAdapter.class);
        setResult(RESULT_OK, intent);
        finish();
    }
}