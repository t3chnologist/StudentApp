package C196PA.BTerbish.StudentApp.UIController;

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
import java.util.List;
import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.R;

public class CourseListAdapter extends AppCompatActivity {
    private final int REQUEST_CODE_NEW_COURSE = 0;
    private final int REQUEST_CODE_UPDATE_COURSE = 1;
    private final int REQUEST_CODE_DELETE_COURSE = 2;

    StudentDatabase mStudentDb;
    private CourseAdapter mCourseAdapter;
    private RecyclerView mRecyclerView;
    private int[] mCourseColors;
    private Course mSelectedCourse;
    private int mSelectedCoursePosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;
    private ViewGroup mShowCoursesLayout;
    private ViewGroup mNoCourseLayout;
    private List<Course> mCourseList;
    private long mTermId;
    private String mTermTitle;
    private TextView mCustomNoCourseMessage;
    private long courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list_adapter);

        Bundle bundle = getIntent().getExtras();
        mTermId = bundle.getLong("termId");

        mCustomNoCourseMessage = findViewById(R.id.noCourseMessage);
        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        mNoCourseLayout = findViewById(R.id.noCourseLayout);
        mShowCoursesLayout = findViewById(R.id.showCoursesLayout);

        mCourseList = mStudentDb.courseDao().getCoursesByTermId(mTermId);
        mTermTitle = mStudentDb.termDao().getTermById(mTermId).getTermTitle();
        mCourseColors = getResources().getIntArray(R.array.courseColors);
        mRecyclerView = findViewById(R.id.courseRecyclerView);
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        setTitle(mTermTitle + ": Course list");
    }

    @Override
    public void onResume() {
        super.onResume();
        mCourseList = mStudentDb.courseDao().getCoursesByTermId(mTermId);
        if (mCourseList.size() == 0) {
            displayCourse(false);
        }
        else {
            displayCourse(true);
        }
        mCourseAdapter = new CourseListAdapter.CourseAdapter(mStudentDb.courseDao().getCoursesByTermId(mTermId));
        mRecyclerView.setAdapter(mCourseAdapter);
    }

    private void displayCourse(boolean display) {
        if (display) {
            mShowCoursesLayout.setVisibility(View.VISIBLE);
            mNoCourseLayout.setVisibility(View.GONE);
        } else {
            mShowCoursesLayout.setVisibility(View.GONE);
            mNoCourseLayout.setVisibility(View.VISIBLE);
        }
    }

    private class CourseAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseHolder>{

        private final List<Course> mCourses;

        public CourseAdapter(List<Course> courses) {
            mCourses = courses;
        }

        @Override
        public CourseListAdapter.CourseHolder onCreateViewHolder(ViewGroup parent, int viewType){

            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new CourseListAdapter.CourseHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CourseListAdapter.CourseHolder holder, int position){
            holder.bind(mCourses.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mCourses.size();
        }

        public void removeCourse(Course course) {
            int index = mCourses.indexOf(course);
            if (index >= 0) {
                mCourses.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    private class CourseHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private Course mCourse;
        private TextView mTextView;

        public CourseHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mTextView = itemView.findViewById(R.id.recyclerTextView);
        }

        public void bind(Course course, int position) {
            mCourse = course;
            courseId = mCourse.getId();
            mTextView.setText(course.getCourseTitle());

            if (mSelectedCoursePosition == position) {
                mTextView.setBackgroundColor(Color.BLUE);
            } else {
                int colorIndex = course.getCourseTitle().length() % mCourseColors.length;
                mTextView.setBackgroundColor(mCourseColors[colorIndex]);
            }
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putLong("courseId", mCourse.getId());
            bundle.putLong("termId", mTermId);

            Intent intent = new Intent(CourseListAdapter.this, CourseDetailsActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            if (mActionMode != null) {
                return false;
            }

            mActionMode = CourseListAdapter.this.startActionMode(mActionModeCallback);
            mSelectedCourse = mCourse;
            mSelectedCoursePosition = getAdapterPosition();
            mCourseAdapter.notifyItemChanged(mSelectedCoursePosition);
            mActionMode = CourseListAdapter.this.startActionMode(mActionModeCallback);

            return true;
        }
    }
    public void onCourseAddClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("courseId", -1);
        bundle.putLong("termId", mTermId);
        Intent intent = new Intent(CourseListAdapter.this, CourseEditActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_NEW_COURSE);
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
                    mStudentDb.courseDao().deleteCourse(mSelectedCourse);
                    mCourseAdapter.removeCourse(mSelectedCourse);
                    Toast.makeText(CourseListAdapter.this, "Course deleted",
                            Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.edit:
                    mode.finish();
                    Bundle bundle = new Bundle();
                    bundle.putLong("courseId", mSelectedCourse.getId());
                    Intent intent = new Intent(CourseListAdapter.this, CourseEditActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE_DELETE_COURSE);
                default:
                    return false;
            }
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mCourseAdapter.notifyItemChanged(mSelectedCoursePosition);
            mSelectedCoursePosition = RecyclerView.NO_POSITION;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NEW_COURSE) {
            Toast.makeText(this, "Course added", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_COURSE) {
            Toast.makeText(this, "Course updated", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_DELETE_COURSE) {
            Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show();
        }
    }
}