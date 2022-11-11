package C196PA.BTerbish.StudentApp.UIController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.R;

public class CourseDetailsActivity extends AppCompatActivity {

    private final int REQUEST_CODE_UPDATE_COURSE = 1;
    private long mCourseId;
    private long mTermId;
    StudentDatabase mStudentDb;
    private TextView mCourseTitle;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mStatus;
    private TextView mInstructorName;
    private TextView mInstructorPhone;
    private TextView mInstructorEmail;
    private TextView mOptionalNote;
    private Course mCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        mCourseId = bundle.getLong("courseId");
        mTermId = bundle.getLong("termId");
        mStudentDb = StudentDatabase.getInstance(getApplicationContext());

        mCourseTitle = findViewById(R.id.courseTitleView);
        mStartDate = findViewById(R.id.courseStartView);
        mEndDate = findViewById(R.id.courseEndView);
        mStatus = findViewById(R.id.statusView);
        mInstructorName = findViewById(R.id.instructorNameView);
        mInstructorPhone = findViewById(R.id.instructorPhoneView);
        mInstructorEmail = findViewById(R.id.instructorEmailView);
        mOptionalNote = findViewById(R.id.courseNoteView);

        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {

        mCourse = mStudentDb.courseDao().getCourseById(mCourseId);
        String courseTitle =mCourse.getCourseTitle();
        setTitle("\"" + courseTitle + "\" details");
        mCourseTitle.setText(courseTitle);
        mStartDate.setText(mCourse.getCourseStartDate());
        mEndDate.setText(mCourse.getCourseEndDate());
        mStatus.setText(mCourse.getCourseStatus());
        mInstructorName.setText(mCourse.getInstructorName());
        mInstructorPhone.setText(mCourse.getInstructorPhone());
        mInstructorEmail.setText(mCourse.getInstructorEmail());
        mOptionalNote.setText(mCourse.getCourseNote());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.edit:
                Bundle bundle = new Bundle();
                bundle.putLong("courseId", mCourseId);
                bundle.putLong("termId", mTermId);
                Intent intent = new Intent(this, CourseEditActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE_UPDATE_COURSE);
                return true;
            case R.id.delete:
                mStudentDb.courseDao().deleteCourse(mCourse);
                bundle = new Bundle();
                bundle.putLong("termId", mTermId);
                intent = new Intent(CourseDetailsActivity.this, CourseListAdapter.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_COURSE) {
            Toast.makeText(this, "Term updated", Toast.LENGTH_SHORT).show();
        }
    }
}