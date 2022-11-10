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

import java.util.List;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.Entity.Term;
import C196PA.BTerbish.StudentApp.R;

public class CourseListAdapter extends AppCompatActivity {
    public static final String EXTRA_COURSE_ID = "C196PA.BTerbish.StudentApp.Entity.Course";
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list_adapter);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        mCourseColors = getResources().getIntArray(R.array.courseColors);
        mRecyclerView = findViewById(R.id.termRecyclerView);
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        mNoCourseLayout = findViewById(R.id.noCourseLayout);
        mShowCoursesLayout = findViewById(R.id.showCoursesLayout);
        mCourseList = mStudentDb.courseDao().getCourses();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCourseList = mStudentDb.courseDao().getCourses();
        if (mCourseList.size() == 0) {
            displayCourse(false);
        }
        else {
            displayCourse(true);
        }
        mCourseAdapter = new CourseListAdapter.CourseAdapter(mStudentDb.courseDao().getCourses());
        mRecyclerView.setAdapter(mCourseAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mCourseList.size() == 0) {
            displayCourse(false);
        }
        else {
            displayCourse(true);
        }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        public void addCourse(Course course) {
            mCourses.add(0, course);
            notifyItemInserted(0);
            mRecyclerView.scrollToPosition(0);
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
            mTextView = itemView.findViewById(R.id.termTextView);
        }

        public void bind(Course course, int position) {
            mCourse = course;
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
            Intent intent = new Intent(CourseListAdapter.this, CourseDetailsActivity.class);
            intent.putExtra(CourseDetailsActivity.EXTRA_COURSE_ID, mCourse.getId());
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
        Intent intent = new Intent(CourseListAdapter.this, CourseEditActivity.class);
        startActivity(intent);
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
                    Intent intent = new Intent(CourseListAdapter.this, CourseEditActivity.class);
                    intent.putExtra(CourseEditActivity.EXTRA_COURSE_ID, mSelectedCourse.getId());
                    startActivity(intent);
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

}