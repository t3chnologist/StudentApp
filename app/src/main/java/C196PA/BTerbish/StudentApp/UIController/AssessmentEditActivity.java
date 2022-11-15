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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.stream.Stream;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Assessment;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.R;

public class AssessmentEditActivity extends AppCompatActivity {

    private StudentDatabase mStudentDb;
    private EditText mAssessmentTitle;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView courseDateRange;
    private RadioButton mPerformanceAssessmentRB;
    private RadioButton mObjectiveAssessmentRB;
    private long mAssessmentId;
    private long mCourseId;
    private Assessment mAssessment;
    private Calendar calendar;
    private int year;
    private int month;
    private int day;
    private DatePickerDialog datePickerDialog;
    private Course mCourse;
    private String courseName;
    private String courseRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_edit);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        courseDateRange = findViewById(R.id.courseDateRange);
        mAssessmentTitle = findViewById(R.id.assessmentTitleEdit);
        mStartDate = findViewById(R.id.assessmentStartDateEdit);
        mEndDate = findViewById(R.id.assessmentEndDateEdit);
        mPerformanceAssessmentRB = findViewById(R.id.performanceAssessmentEditRB);
        mObjectiveAssessmentRB = findViewById(R.id.objectiveAssessmentEditRB);

        Bundle bundle = getIntent().getExtras();
        mAssessmentId = bundle.getLong("assessmentId");
        mCourseId = bundle.getLong("courseId");


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        mCourse = mStudentDb.courseDao().getCourseById(mCourseId);
        courseName = mCourse.getCourseTitle();
        courseRange = courseName + " (" + mCourse.getCourseStartDate() + " - "
                                        + mCourse.getCourseEndDate() + ")";
        courseDateRange.setText(courseRange);

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
            if (mAssessment.getAssessmentType().equals("Performance assessment")) {
                mPerformanceAssessmentRB.setChecked(true);
            }
            else {
                mObjectiveAssessmentRB.setChecked(true);
            }
        }

        mAssessmentTitle.requestFocus();
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
                try {
                    saveAssessment();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveAssessmentButtonClick(View view) throws ParseException {
        minimizeKeyboard(view);
        saveAssessment();
    }

    public void saveAssessment() throws ParseException {

        if (inputValidated()) {
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
            Bundle bundle = new Bundle();
            bundle.putLong("courseId", mCourseId);
            intent.putExtras(bundle);
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

        boolean noMissingInput = Stream.of(mAssessmentTitle.getText().toString(),
                                    mStartDate.getText().toString(), mEndDate.getText().toString())
                                .noneMatch(String::isEmpty);
        if (noMissingInput) {
            mAssessmentTitle.setError(null);
            mStartDate.setError(null);
            mEndDate.setError(null);

            //check for duplicate course title
            String assessmentTitle = mAssessmentTitle.getText().toString();
            for (Assessment assessment : mStudentDb.assessmentDao().getAssessments()) {
                String otherAssessmentTitle = assessment.getAssessmentTitle();

                if (mAssessmentId != assessment.getId() &&
                        Objects.equals(assessmentTitle, otherAssessmentTitle)) {
                    mAssessmentTitle.setError("Duplicate assessment title");
                    Toast.makeText(this, mAssessmentTitle.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            //check start & end dates
            Course tempCourse = mStudentDb.courseDao().getCourseById(mCourseId);
            Calendar courseStart = converter(tempCourse.getCourseStartDate());
            Calendar courseEnd = converter(tempCourse.getCourseEndDate());
            Calendar assessmentStart = converter(mStartDate.getText().toString());
            Calendar assessmentEnd = converter(mEndDate.getText().toString());


            if (assessmentEnd.before(assessmentStart)) {
                mStartDate.setError("Invalid start/end");
                mEndDate.setError("Invalid start/end");
                Toast.makeText(this, "Invalid start/end date", Toast.LENGTH_SHORT).show();
            }

            if (assessmentStart.before(courseStart) || assessmentStart.after(courseEnd)) {
                mStartDate.setError("Invalid start date");

                Toast.makeText(this, "Start date is outside of course "
                                        + courseName + " dates", Toast.LENGTH_SHORT).show();
            }

            if (assessmentEnd.before(courseStart) || assessmentEnd.after(courseEnd)) {
                mEndDate.setError("Invalid start/end");
                Toast.makeText(this, "End date is outside of course "
                                        + courseName + " dates", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (mAssessmentTitle.getText().toString().isEmpty()) {
                mAssessmentTitle.setError("Title is required");
            }
            if (mStartDate.getText().toString().isEmpty()) {
                mStartDate.setError("Missing start date");
            }
            if (mEndDate.getText().toString().isEmpty()) {
                mEndDate.setError("Missing end date");
            }
            Toast.makeText(this, "Missing field!", Toast.LENGTH_SHORT).show();
        }

        //returns true if no error
        return Stream.of(mAssessmentTitle.getError(), mStartDate.getError(), mEndDate.getError())
                .allMatch(Objects::isNull);
    }

}