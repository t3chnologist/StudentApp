package C196PA.BTerbish.StudentApp.UIController;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.R;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

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
                new AlertDialog.Builder(this)
                        .setTitle("Course: " + mCourseTitle.getText())
                        .setMessage("Are you sure you want to delete this course?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mStudentDb.courseDao().deleteCourse(mCourse);
                                Bundle bundle = new Bundle();
                                bundle.putLong("termId", mTermId);
                                Intent intent = new Intent(CourseDetailsActivity.this,
                                                            CourseListAdapter.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }})
                        .setNegativeButton("No", null).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_COURSE) {
            Toast.makeText(this, "Course updated", Toast.LENGTH_SHORT).show();


        }
    }
}