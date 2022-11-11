package C196PA.BTerbish.StudentApp.UIController;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Term;
import C196PA.BTerbish.StudentApp.R;

public class TermDetailsActivity extends AppCompatActivity {

    private final int REQUEST_CODE_UPDATE_TERM = 1;
    long mTermId;
    Term mTerm;
    StudentDatabase mStudentDb;
    private TextView mTermTitleDetail;
    private TextView mStartDateDetail;
    private TextView mEndDateDetail;
    private Button showCourseButton;
    private Button addCourseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        mTermTitleDetail = findViewById(R.id.termTitleDetails);
        mStartDateDetail = findViewById(R.id.startDateDetails);
        mEndDateDetail = findViewById(R.id.endDateDetails);

        Bundle bundle = getIntent().getExtras();
        mTermId = bundle.getLong("termId");

        showCourseButton = findViewById(R.id.showCourseButtonId);
        addCourseButton = findViewById(R.id.addCourseButtonId);

        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        mTerm = mStudentDb.termDao().getTermById(mTermId);
        String termTitle = mTerm.getTermTitle();
        setTitle("\"" + termTitle + "\" details");
        mTermTitleDetail.setText(termTitle);
        mStartDateDetail.setText(mTerm.getStartDate());
        mEndDateDetail.setText(mTerm.getEndDate());

        if (mStudentDb.courseDao().getCoursesByTermId(mTermId).size() > 0) {
            showCourseButton.setVisibility(View.VISIBLE);
            addCourseButton.setVisibility(View.GONE);
        }
        else {
            showCourseButton.setVisibility(View.GONE);
            addCourseButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.edit:
                Bundle bundle = new Bundle();
                bundle.putLong("termId", mTermId);
                Intent intent = new Intent(this, TermEditActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE_UPDATE_TERM);
                return true;
            case R.id.delete:
                //check if term has course associated
                if (mStudentDb.courseDao().getCoursesByTermId(mTermId).size() > 0) {
                    new AlertDialog.Builder(this)
                            .setTitle("Warning")
                            .setMessage("This term cannot be deleted.\n" +
                                        "Remove associated course(s) first.")
                            .setPositiveButton("OK", null)
                            .setNeutralButton("Show course(s)",
                                                        new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    showAssociatedCourses();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(this)
                            .setTitle("Term: " + mTermTitleDetail.getText())
                            .setMessage("Are you sure you want to delete this term?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mStudentDb.termDao().deleteTerm(mTerm);
                                    Intent intent = new Intent(TermDetailsActivity.this,
                                                                    TermListAdapter.class);
                                    startActivity(intent);
                                }})
                            .setNegativeButton("No", null).show();
                    return true;
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_TERM) {
            Toast.makeText(this, "Term updated", Toast.LENGTH_SHORT).show();

            Bundle bundle = data.getExtras();
            long termId = bundle.getLong("termId");
            mTermId = termId;
            String termTitle = mStudentDb.termDao().getTermById(mTermId).getTermTitle();

            new AlertDialog.Builder(this)
                    .setTitle("Successfully updated \"" + termTitle + "\"")
                    .setMessage("Would you like to make updates to course(s) in this term?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            showAssociatedCourses();
                        }})
                    .setNegativeButton("No", null).show();
        }
    }

    public void onAddCourseButtonClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("courseId", -1);
        bundle.putLong("termId", mTermId);
        Intent intent = new Intent(this, CourseEditActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onShowCourseButtonClick(View view) {
        showAssociatedCourses();
    }

    public void showAssociatedCourses() {
        Bundle bundle = new Bundle();
        bundle.putLong("termId", mTermId);
        Intent intent = new Intent(TermDetailsActivity.this, CourseListAdapter.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}