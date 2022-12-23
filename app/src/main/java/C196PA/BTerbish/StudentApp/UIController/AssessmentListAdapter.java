package C196PA.BTerbish.StudentApp.UIController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Assessment;
import C196PA.BTerbish.StudentApp.R;

public class AssessmentListAdapter extends AppCompatActivity {

    private final int REQUEST_CODE_NEW_ASSESSMENT = 0;
    private final int REQUEST_CODE_UPDATE_ASSESSMENT = 1;
    private final int REQUEST_CODE_DELETE_ASSESSMENT = 2;
    StudentDatabase mStudentDb;
    private AssessmentAdapter mAssessmentAdapter;
    private RecyclerView mRecyclerView;
    private int[] mAssessmentColors;
    private Assessment mSelectedAssessment;
    private int mSelectedAssessmentPosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;
    private ViewGroup mShowAssessmentsLayout;
    private ViewGroup mNoAssessmentLayout;
    private List<Assessment> mAssessmentList;
    private long mCourseId;
    private String mCourseTitle;
    private long assessmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list_adapter);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        mCourseId = bundle.getLong("courseId");

        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        mNoAssessmentLayout = findViewById(R.id.noAssessmentLayout);
        mShowAssessmentsLayout = findViewById(R.id.showAssessmentLayout);
        mAssessmentList = mStudentDb.assessmentDao().getAssessmentsByCourseId(mCourseId);
        mCourseTitle = mStudentDb.courseDao().getCourseById(mCourseId).getCourseTitle();
        mAssessmentColors = getResources().getIntArray(R.array.assessmentColors);
        mRecyclerView = findViewById(R.id.assessmentRecyclerView);
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        setTitle(mCourseTitle + ": Assessment list");


    }

    @Override
    public void onResume() {
        super.onResume();
        mAssessmentList = mStudentDb.assessmentDao().getAssessmentsByCourseId(mCourseId);
        if (mAssessmentList.size() == 0) {
            displayAssessment(false);
        }
        else {
            displayAssessment(true);
        }
        mAssessmentAdapter = new AssessmentListAdapter.AssessmentAdapter(mStudentDb.assessmentDao()
                                                        .getAssessmentsByCourseId(mCourseId));
        mRecyclerView.setAdapter(mAssessmentAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean("deleteAssessment")) {
            getIntent().removeExtra("deleteAssessment");
            assessmentId = bundle.getLong("assessmentId");
            getIntent().removeExtra("assessmentId");
            deleteAssessment(mStudentDb.assessmentDao().getAssessmentById(assessmentId));
        }
    }

    private void displayAssessment(boolean display) {
        if (display) {
            mShowAssessmentsLayout.setVisibility(View.VISIBLE);
            mNoAssessmentLayout.setVisibility(View.GONE);
        } else {
            mShowAssessmentsLayout.setVisibility(View.GONE);
            mNoAssessmentLayout.setVisibility(View.VISIBLE);
        }
    }

    private class AssessmentAdapter extends RecyclerView.Adapter<AssessmentListAdapter.AssessmentHolder>{

        private final List<Assessment> mAssessments;

        public AssessmentAdapter(List<Assessment> assessments) {
            mAssessments = assessments;
        }

        @Override
        public AssessmentListAdapter.AssessmentHolder onCreateViewHolder(ViewGroup parent, int viewType){

            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new AssessmentListAdapter.AssessmentHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(AssessmentListAdapter.AssessmentHolder holder, int position){
            holder.bind(mAssessments.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mAssessments.size();
        }

        public void removeAssessment(Assessment assessment) {
            int index = mAssessments.indexOf(assessment);
            if (index >= 0) {
                mAssessments.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    private class AssessmentHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private Assessment mAssessment;
        private TextView mTextView;

        public AssessmentHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mTextView = itemView.findViewById(R.id.recyclerTextView);
        }

        public void bind(Assessment assessment, int position) {
            mAssessment = assessment;
            assessmentId = mAssessment.getId();
            mTextView.setText(assessment.getAssessmentTitle());

            if (mSelectedAssessmentPosition == position) {
                mTextView.setBackgroundColor(Color.BLUE);
            } else {
                int colorIndex = assessment.getAssessmentTitle().length() % mAssessmentColors.length;
                mTextView.setBackgroundColor(mAssessmentColors[colorIndex]);
            }
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putLong("assessmentId", mAssessment.getId());
            bundle.putLong("courseId", mCourseId);

            Intent intent = new Intent(AssessmentListAdapter.this, AssessmentDetailsActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            if (mActionMode != null) {
                return false;
            }

            mActionMode = AssessmentListAdapter.this.startActionMode(mActionModeCallback);
            mSelectedAssessment = mAssessment;
            mSelectedAssessmentPosition = getAdapterPosition();
            mAssessmentAdapter.notifyItemChanged(mSelectedAssessmentPosition);
            mActionMode = AssessmentListAdapter.this.startActionMode(mActionModeCallback);

            return true;
        }
    }
    public void onAssessmentAddClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("assessmentId", -1);
        bundle.putLong("courseId", mCourseId);
        Intent intent = new Intent(AssessmentListAdapter.this, AssessmentEditActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_NEW_ASSESSMENT);
    }

    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    mode.finish();
                    deleteAssessment(mSelectedAssessment);
                    return true;
                case R.id.edit:
                    mode.finish();
                    Bundle bundle = new Bundle();
                    bundle.putLong("assessmentId", mSelectedAssessment.getId());
                    bundle.putLong("courseId", mCourseId);
                    Intent intent = new Intent(AssessmentListAdapter.this,
                                                AssessmentEditActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE_UPDATE_ASSESSMENT);
                default:
                    return false;
            }
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mAssessmentAdapter.notifyItemChanged(mSelectedAssessmentPosition);
            mSelectedAssessmentPosition = RecyclerView.NO_POSITION;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NEW_ASSESSMENT) {
            Toast.makeText(this, "Assessment added", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_ASSESSMENT) {
            Toast.makeText(this, "Assessment updated", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_DELETE_ASSESSMENT) {
            Toast.makeText(this, "Assessment deleted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, CourseDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("courseId", mCourseId);
                bundle.putLong("termId", mStudentDb.courseDao().getCourseById(mCourseId).getTerm());
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteAssessment(Assessment selectedAssessment) {
        Assessment deletedAssessment = selectedAssessment;
        mStudentDb.assessmentDao().deleteAssessment(selectedAssessment);
        mAssessmentAdapter.removeAssessment(selectedAssessment);
        onResume();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.assessmentCoordinatorLayout),
                "Assessment deleted", Snackbar.LENGTH_SHORT);
        snackbar.setAction("Undo", (v) -> {
            mStudentDb.assessmentDao().insertAssessment(deletedAssessment);
            mAssessmentList.add(deletedAssessment);
            onResume();
        });
        snackbar.setDuration(6000);
        snackbar.show();
    }
}