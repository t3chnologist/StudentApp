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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.R;

public class CourseEditActivity extends AppCompatActivity {

    private StudentDatabase mStudentDb;
    private EditText mCourseTitle;
    private TextView mStartDate;
    private TextView mEndDate;
    private Spinner mStatus;
    private EditText mInstructorName;
    private EditText mInstructorPhone;
    private EditText mInstructorEmail;
    private EditText mOptionalNote;
    private long mTermId;
    private long mCourseId;
    private Course mCourse;
    private boolean addNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        mCourseTitle = findViewById(R.id.courseTitleText);
        mStartDate = findViewById(R.id.courseStartText);
        mEndDate = findViewById(R.id.courseEndText);
        mStatus = findViewById(R.id.statusSpinner);

        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CourseEditActivity.this,
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(CourseEditActivity.this,
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


        Spinner spinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.course_status_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        mInstructorName = findViewById(R.id.instructorNameText);
        mInstructorPhone = findViewById(R.id.instructorPhoneText);
        mInstructorEmail = findViewById(R.id.instructorEmailText);
        mOptionalNote = findViewById(R.id.courseNoteOptionalText);

        Bundle bundle = getIntent().getExtras();
        mCourseId = bundle.getLong("courseId");
        mTermId = bundle.getLong("termId");
        addNote = bundle.getBoolean("addNote");

        StudentDatabase mStudentDb = StudentDatabase.getInstance(getApplicationContext());

        if (mCourseId == -1) {
            setTitle(mStudentDb.termDao().getTermById(mTermId).getTermTitle() + ": New Course");
            mCourse = new Course();
        }
        else {
            setTitle("Editing: " + mStudentDb.courseDao().getCourseById(mCourseId).getCourseTitle());
            mCourse = mStudentDb.courseDao().getCourseById(mCourseId);
            mCourseTitle.setText(mCourse.getCourseTitle());
            mStartDate.setText(mCourse.getCourseStartDate());
            mEndDate.setText(mCourse.getCourseEndDate());
            mInstructorName.setText(mCourse.getInstructorName());
            mInstructorPhone.setText(mCourse.getInstructorPhone());
            mInstructorEmail.setText(mCourse.getInstructorEmail());
            mOptionalNote.setText(mCourse.getCourseNote());

            if (addNote) {
                getIntent().removeExtra("addNote");
                mOptionalNote.requestFocus();
            }
            else {
                mCourseTitle.requestFocus();
            }
        }
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
                saveCourse();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveCourseButtonClick(View view) {
        saveCourse();
    }

    public void saveCourse() {
        mCourse.setCourseTitle(mCourseTitle.getText().toString());
        mCourse.setCourseStartDate(mStartDate.getText().toString());
        mCourse.setCourseEndDate(mEndDate.getText().toString());
        mCourse.setCourseStatus(mStatus.getSelectedItem().toString());
        mCourse.setInstructorName(mInstructorName.getText().toString());
        mCourse.setInstructorPhone(mInstructorPhone.getText().toString());
        mCourse.setInstructorEmail(mInstructorEmail.getText().toString());
        mCourse.setCourseNote(mOptionalNote.getText().toString());
        mCourse.setTerm(mTermId);

        Intent intent;

        if (mCourseId == -1) {
            mStudentDb.courseDao().insertCourse(mCourse);
            intent = new Intent(this, CourseListAdapter.class);

        }
        else {
            mStudentDb.courseDao().updateCourse(mCourse);
            intent = new Intent(this, CourseListAdapter.class);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}