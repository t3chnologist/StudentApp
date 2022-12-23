package C196PA.BTerbish.StudentApp.UIController;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.R;

public class CourseDetailsActivity extends AppCompatActivity {

    private final int REQUEST_CODE_UPDATE_COURSE = 1;
    private final int REQUEST_CODE_NEW_ASSESSMENT = 2;
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
    private Button showAssessmentsButton;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

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
        showAssessmentsButton = findViewById(R.id.showAssessmentsButtonCourseDetails);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCourse = mStudentDb.courseDao().getCourseById(mCourseId);


        String courseTitle = mCourse.getCourseTitle();
        setTitle("\"" + courseTitle + "\" details");
        mCourseTitle.setText(courseTitle);
        mStartDate.setText(mCourse.getCourseStartDate());
        mEndDate.setText(mCourse.getCourseEndDate());
        mStatus.setText(mCourse.getCourseStatus());
        mInstructorName.setText(mCourse.getInstructorName());
        mInstructorPhone.setText(mCourse.getInstructorPhone());
        mInstructorEmail.setText(mCourse.getInstructorEmail());
        mOptionalNote.setText(mCourse.getCourseNote());
        mOptionalNote.setError(null);

        if (mStudentDb.assessmentDao().getAssessmentsByCourseId(mCourseId).size() > 0) {
            showAssessmentsButton.setVisibility(View.VISIBLE);
        }
        else {
            showAssessmentsButton.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.course_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                Intent intent = new Intent(this, CourseListAdapter.class);
                Bundle bundle = new Bundle();
                bundle.putLong("termId", mTermId);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;

            case R.id.edit:
                editCourse(mCourseId, mTermId, false);
                return true;

            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setTitle("Course: " + mCourseTitle.getText())
                        .setMessage("Are you sure you want to delete this course?" +
                                "\n\nAny associated assessments will be removed automatically.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                deleteCourse();

                            }})
                        .setNegativeButton("No", null).show();
                return true;

            case R.id.shareNote:

                String noteString = mOptionalNote.getText().toString();

                if (noteString.isEmpty()) {
                    mOptionalNote.setError("Empty note");
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.courseDetailsCoordinatorLayout),
                            "Can't share empty note.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Add note", (v) -> {
                        editCourse(mCourseId, mTermId, true);
                    });
                    snackbar.setDuration(6000);
                    snackbar.show();
                }
                else {
                    String textBody = mCourseTitle.getText() + " course note:\n\n" + noteString;
                    Intent intentShare = new Intent(Intent.ACTION_SEND);
                    intentShare.setType("text/plain");
                    intentShare.putExtra(Intent.EXTRA_TEXT, textBody);
                    intentShare.createChooser(intentShare, null);
                    startActivity(intentShare);
                }
                return true;
            case R.id.setAlert:
                String courseTitle = mCourseTitle.getText().toString();
                String start = mStartDate.getText().toString();
                String end = mEndDate.getText().toString();

                setNotification(start,courseTitle + " starts today");
                setNotification(end, courseTitle + " ends today");

                Toast.makeText(this, "Notification added", Toast.LENGTH_SHORT).show();
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
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NEW_ASSESSMENT) {
            Toast.makeText(this, "Assessment added", Toast.LENGTH_SHORT).show();
        }
    }

    public void editCourse(long courseId, long termId, boolean addNote) {
        Bundle bundle = new Bundle();
        bundle.putLong("courseId", courseId);
        bundle.putLong("termId", termId);
        bundle.putBoolean("addNote", addNote);
        Intent intent = new Intent(this, CourseEditActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_COURSE);
    }

    public void onAddAssessmentButtonClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("assessmentId", -1);
        bundle.putLong("courseId", mCourseId);
        Intent intent = new Intent(this, AssessmentEditActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_NEW_ASSESSMENT);
    }

    public void onShowAssessmentButtonClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("courseId", mCourseId);
        Intent intent = new Intent(this, AssessmentListAdapter.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void deleteCourse() {
        Bundle bundle = new Bundle();
        bundle.putLong("termId", mTermId);
        bundle.putLong("courseId", mCourseId);
        bundle.putBoolean("deleteCourse", true);
        Intent intent = new Intent(this, CourseListAdapter.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void setNotification (String date, String notificationText) {

        Date notificationDate = null;

        try {
            notificationDate = simpleDateFormat.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert notificationDate != null;
        long triggerStartDate = notificationDate.getTime();

        Intent intent = new Intent(this, MyReceiver.class);
        Bundle bundleStart = new Bundle();
        bundleStart.putString("text", notificationText);
        bundleStart.putString("title", "Course reminder");
        intent.putExtras(bundleStart);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                HomeScreenActivity.numAlert++, intent, FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerStartDate, pendingIntent);
    }

    private Calendar converter(String date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        cal.setTime(sdf.parse(date));
        return cal;
    }
}