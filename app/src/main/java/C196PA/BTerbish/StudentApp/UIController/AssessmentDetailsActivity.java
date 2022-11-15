package C196PA.BTerbish.StudentApp.UIController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.BuddhistCalendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

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
                editAssessment(mAssessmentId, mCourseId);
                return true;

            case R.id.delete:
                deleteAssessment();
                return true;
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
}