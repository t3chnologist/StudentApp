package C196PA.BTerbish.StudentApp.UIController;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
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
        }
        else {
            showCourseButton.setVisibility(View.GONE);
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
                    bundle = new Bundle();
                    bundle.putLong("termId", mTermId);
                    bundle.putBoolean("deleteTerm", true);
                    intent = new Intent(TermDetailsActivity.this,
                            TermListAdapter.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

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

            Snackbar snackbar = Snackbar.make(findViewById(R.id.termDetailsCoordinatorLayout),
                    "Term updated", Snackbar.LENGTH_LONG);
            snackbar.setAction("Update course", (v) -> {
                showAssociatedCourses();
            });
            snackbar.show();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_TERM) {

            Snackbar snackbar = Snackbar.make(findViewById(R.id.termDetailsCoordinatorLayout),
                    "Term updated", Snackbar.LENGTH_LONG);
            snackbar.setAction("Update course", (v) -> {
                showAssociatedCourses();
            });
            snackbar.setDuration(6000);
            snackbar.show();
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