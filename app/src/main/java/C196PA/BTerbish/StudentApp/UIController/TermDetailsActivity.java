package C196PA.BTerbish.StudentApp.UIController;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Term;
import C196PA.BTerbish.StudentApp.R;

public class TermDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_TERM_ID = "C196PA.BTerbish.StudentApp.Entity.term";
    private final int REQUEST_CODE_UPDATE_TERM = 1;
    long mTermId;
    Term mTerm;
    StudentDatabase mStudentDb;
    private TextView mTermTitleDetail;
    private TextView mStartDateDetail;
    private TextView mEndDateDetail;

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
        mTermId = getIntent().getLongExtra(EXTRA_TERM_ID, -1);

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
                Intent intent = new Intent(this, TermEditActivity.class);
                intent.putExtra(TermEditActivity.EXTRA_TERM_ID, mTermId);
                startActivityForResult(intent, REQUEST_CODE_UPDATE_TERM);
                return true;
            case R.id.delete:
                try {
                    mStudentDb.termDao().deleteTerm(mTerm);
                    intent = new Intent(this, TermListAdapter.class);
                    startActivity(intent);
                    return true;
                }
                catch (RuntimeException e) {
                    Log.e("", e.toString());
                    AlertDialog.Builder alert = new AlertDialog.Builder(TermDetailsActivity.this);
                    alert.setTitle("Term cannot be deleted!");
                    alert.setMessage("There are course(s) associated with this term.");
                    alert.setPositiveButton("OK",null);
                    alert.show();
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
        }
    }

    public void onShowCourseButtonClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("termId", mTermId);
        Intent intent = new Intent(TermDetailsActivity.this, CourseListAdapter.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}