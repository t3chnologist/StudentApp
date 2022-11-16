package C196PA.BTerbish.StudentApp.UIController;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Assessment;
import C196PA.BTerbish.StudentApp.R;

public class AssessmentDetailsActivity extends AppCompatActivity {

    private final int REQUEST_CODE_UPDATE_ASSESSMENT = 1;
    private long mAssessmentId;
    private long mCourseId;
    StudentDatabase mStudentDb;
    private TextView mAssessmentTitle;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mAssessmentType;
    private Assessment mAssessment;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        Bundle extras = getIntent().getExtras();
        mAssessmentId = extras.getLong("assessmentId");
        mCourseId = extras.getLong("courseId");

        mStudentDb = StudentDatabase.getInstance(getApplicationContext());

        mAssessmentTitle = findViewById(R.id.assessmentTitleDetails);
        mStartDate = findViewById(R.id.assessmentStartDateDetails);
        mEndDate = findViewById(R.id.assessmentEndDateDetails);
        mAssessmentType = findViewById(R.id.typeAssessmentDetails);

    }

    @Override
    public void onResume() {
        super.onResume();
        mAssessment = mStudentDb.assessmentDao().getAssessmentById(mAssessmentId);

        String assessmentTitle = mAssessment.getAssessmentTitle();
        setTitle("\"" + assessmentTitle + "\" details");

        mAssessmentTitle.setText(assessmentTitle);
        mStartDate.setText(mAssessment.getAssessmentStart());
        mEndDate.setText(mAssessment.getAssessmentEnd());
        mAssessmentType.setText(mAssessment.getAssessmentType());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.assessment_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;

            case R.id.edit:
                editAssessment(mAssessmentId, mCourseId);
                return true;

            case R.id.delete:
                deleteAssessment();
                return true;
            case R.id.setAlert:
                String title = mAssessment.getAssessmentTitle();
                String type = mAssessment.getAssessmentType();
                String start = mAssessment.getAssessmentStart();
                String end = mAssessment.getAssessmentEnd();

                setNotification(start,title + " starts today!", type);
                setNotification(end, title + " ends today!", type);

                Toast.makeText(this, "Added notification", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_ASSESSMENT) {
            Toast.makeText(this, "Assessment updated", Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteAssessment() {
        Bundle bundle = new Bundle();
        bundle.putLong("courseId", mCourseId);
        bundle.putLong("assessmentId", mAssessmentId);
        bundle.putBoolean("deleteAssessment", true);
        Intent intent = new Intent(this, AssessmentListAdapter.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void editAssessment(long assessmentId, long courseId) {
        Bundle bundle = new Bundle();
        bundle.putLong("assessmentId", assessmentId);
        bundle.putLong("courseId", courseId);

        Intent intent = new Intent(this, AssessmentEditActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_ASSESSMENT);
    }

    public void setNotification (String date, String notificationText, String assessmentType) {



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
        bundleStart.putString("title", assessmentType + " reminder");
        intent.putExtras(bundleStart);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                HomeScreenActivity.numAlert++, intent, FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerStartDate, pendingIntent);
    }
}