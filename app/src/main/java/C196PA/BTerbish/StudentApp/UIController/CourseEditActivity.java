package C196PA.BTerbish.StudentApp.UIController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.R;

public class CourseEditActivity extends AppCompatActivity {

    private StudentDatabase mStudentDb;
    private EditText mCourseTitle;
    private EditText mStartDate;
    private EditText mEndDate;
    private Spinner mStatus;
    private EditText mInstructorName;
    private EditText mInstructorPhone;
    private EditText mInstructorEmail;
    private EditText mOptionalNote;
    private long mTermId;
    private long mCourseId;
    private Course mCourse;

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
        mOptionalNote = findViewById(R.id.courseNoteOptionalText);

        Bundle bundle = getIntent().getExtras();
        mCourseId = bundle.getLong("courseId");
        mTermId = bundle.getLong("termId");

        StudentDatabase mStudentDb = StudentDatabase.getInstance(getApplicationContext());

        if (mCourseId == -1) {
            setTitle("Add course");
            mCourse = new Course();
        }
        else {
            setTitle("Edit course");
            mCourse = mStudentDb.courseDao().getCourseById(mCourseId);
            mCourseTitle.setText(mCourse.getCourseTitle());
            mStartDate.setText(mCourse.getCourseStartDate());
            mEndDate.setText(mCourse.getCourseEndDate());
            mInstructorName.setText(mCourse.getInstructorName());
            mInstructorPhone.setText(mCourse.getInstructorPhone());
            mInstructorEmail.setText(mCourse.getInstructorEmail());
            mOptionalNote.setText(mCourse.getCourseNote());
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

        Intent intent = new Intent();

        if (mCourseId == -1) {
            mStudentDb.courseDao().insertCourse(mCourse);
        }
        else {
            mStudentDb.courseDao().updateCourse(mCourse);
            intent = new Intent(this, CourseListAdapter.class);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}