package C196PA.BTerbish.StudentApp.UIController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.R;

public class CourseEditActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "C196PA.BTerbish.StudentApp.Entity.Course";
    public static final String EXTRA_TERM_ID = "C196PA.BTerbish.StudentApp.Entity.Term";

    private StudentDatabase mStudentDb;
    private EditText mCourseTitle;
    private EditText mStartDate;
    private EditText mEndDate;
    private Spinner mStatus;
    private EditText mInstructorName;
    private EditText mInstructorPhone;
    private EditText mInstructorEmail;
    private long mTermId;
    private long mCourseId;

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

        Spinner spinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.course_status_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        mInstructorName = findViewById(R.id.instructorNameText);
        mInstructorPhone = findViewById(R.id.instructorPhoneText);
        mInstructorEmail = findViewById(R.id.instructorEmailText);

        Intent intent = getIntent();
        mTermId = intent.getLongExtra(EXTRA_TERM_ID, -1);
        mCourseId = intent.getLongExtra(EXTRA_COURSE_ID, -1);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onSaveCourseButtonClick(View view) {
        Course mCourse = new Course();
        mCourse.setCourseTitle(mCourseTitle.getText().toString());
        mCourse.setCourseStartDate(mStartDate.getText().toString());
        mCourse.setCourseEndDate(mEndDate.getText().toString());
        mCourse.setCourseStatus(mStatus.getSelectedItem().toString());
        mCourse.setInstructorName(mInstructorName.getText().toString());
        mCourse.setInstructorPhone(mInstructorPhone.getText().toString());
        mCourse.setInstructorEmail(mInstructorEmail.getText().toString());
        mCourse.setTerm(mTermId);
        //mCourse.setCourseNote(mNote);


        Intent intent = new Intent();


        if (mCourseId == -1) {
            long courseId = mStudentDb.courseDao().insertCourse(mCourse);
            intent.putExtra(EXTRA_COURSE_ID, courseId);
        }
        else {
            mStudentDb.courseDao().updateCourse(mCourse);
            intent = new Intent(this, CourseListAdapter.class);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}