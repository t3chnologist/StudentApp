package C196PA.BTerbish.StudentApp.UIController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.Entity.Term;
import C196PA.BTerbish.StudentApp.R;

public class CourseEditActivity extends AppCompatActivity {

    private StudentDatabase mStudentDb;
    private TextView termDateRange;
    private EditText mCourseTitle;
    private TextView mStartDate;
    private TextView mEndDate;
    private Spinner mStatus;
    private EditText mInstructorName;
    private EditText mInstructorPhone;
    private EditText mInstructorEmail;
    private EditText mOptionalNote;
    private long mTermId;
    private long mCourseId;
    private Course mCourse;
    private boolean addNote;
    private Calendar calendar;
    private int year;
    private int month;
    private int day;
    private DatePickerDialog datePickerDialog;
    private Term mTerm;
    private String termRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        termDateRange = findViewById(R.id.termDateRange);
        mCourseTitle = findViewById(R.id.courseTitleText);
        mStartDate = findViewById(R.id.courseStartText);
        mEndDate = findViewById(R.id.courseEndText);
        mStatus = findViewById(R.id.statusSpinner);

        Spinner spinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.course_status_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        mInstructorName = findViewById(R.id.instructorNameText);
        mInstructorPhone = findViewById(R.id.instructorPhoneText);
        mInstructorEmail = findViewById(R.id.instructorEmailText);
        mOptionalNote = findViewById(R.id.courseNoteOptionalText);

        Bundle bundle = getIntent().getExtras();
        mCourseId = bundle.getLong("courseId");
        mTermId = bundle.getLong("termId");
        addNote = bundle.getBoolean("addNote");

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        mTerm = mStudentDb.termDao().getTermById(mTermId);
        termRange = mTerm.getTermTitle() + " (" + mTerm.getStartDate() + " - " + mTerm.getEndDate() + ")";
        termDateRange.setText(termRange);

        if (mCourseId == -1) {
            setTitle(mStudentDb.termDao().getTermById(mTermId).getTermTitle() + ": New Course");
            mCourse = new Course();
        }
        else {
            mCourse = mStudentDb.courseDao().getCourseById(mCourseId);
            setTitle("Editing: " + mCourse.getCourseTitle());

            mCourseTitle.setText(mCourse.getCourseTitle());
            mStartDate.setText(mCourse.getCourseStartDate());
            mEndDate.setText(mCourse.getCourseEndDate());
            mInstructorName.setText(mCourse.getInstructorName());
            mInstructorPhone.setText(mCourse.getInstructorPhone());
            mInstructorEmail.setText(mCourse.getInstructorEmail());
            mOptionalNote.setText(mCourse.getCourseNote());

            if (addNote) {
                getIntent().removeExtra("addNote");
                mOptionalNote.requestFocus();
            }
            else {
                mCourseTitle.requestFocus();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_new_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.save:
                try {
                    saveCourse();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveCourseButtonClick(View view) throws ParseException {
        minimizeKeyboard(view);
        saveCourse();
    }

    public void saveCourse() throws ParseException {

        boolean noMissingInput = Stream.of(mCourseTitle.getText().toString(),
                                        mStartDate.getText().toString(),
                                        mEndDate.getText().toString(),
                                        mStatus.getSelectedItem().toString(),
                                        mInstructorName.getText().toString(),
                                        mInstructorPhone.getText().toString(),
                                        mInstructorEmail.getText().toString())
                                .noneMatch(String::isEmpty);

        if (noMissingInput) {

            //check start & end dates
            Calendar termStart = converter(mStudentDb.termDao().getTermById(mTermId).getStartDate());
            Calendar termEnd = converter(mStudentDb.termDao().getTermById(mTermId).getEndDate());
            Calendar courseStart = converter(mStartDate.getText().toString());
            Calendar courseEnd = converter(mEndDate.getText().toString());

            if (courseEnd.before(courseStart) || courseEnd.before(termStart) || courseEnd.after(termEnd) ||
                                    courseStart.before(termStart) || courseStart.after(termEnd)) {

                if (courseEnd.before(termStart) || courseEnd.after(termEnd) ||
                        courseStart.before(termStart) || courseStart.after(termEnd)) {
                    Toast.makeText(this, "Date(s) are out of Term date range",
                                                        Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Invalid start/end date",
                                                        Toast.LENGTH_SHORT).show();
                }
            }
            else {
                mCourse.setCourseTitle(mCourseTitle.getText().toString());
                mCourse.setCourseStartDate(mStartDate.getText().toString());
                mCourse.setCourseEndDate(mEndDate.getText().toString());
                mCourse.setCourseStatus(mStatus.getSelectedItem().toString());
                mCourse.setInstructorName(mInstructorName.getText().toString());
                mCourse.setInstructorPhone(mInstructorPhone.getText().toString());
                mCourse.setInstructorEmail(mInstructorEmail.getText().toString());
                mCourse.setCourseNote(mOptionalNote.getText().toString());

                Intent intent = new Intent(CourseEditActivity.this,
                                                    CourseListAdapter.class);
                Bundle bundle = new Bundle();
                bundle.putLong("termId", mTermId);
                intent.putExtras(bundle);

                if (mCourseId == -1) {
                    mCourse.setTerm(mTermId);
                    mCourseId = mStudentDb.courseDao().insertCourse(mCourse);
                }
                else {
                    mStudentDb.courseDao().updateCourse(mCourse);
                }

                setResult(RESULT_OK, intent);
                finish();
            }
        }
        else {
            Toast.makeText(this, "Missing field!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickStartDate(View view) {
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mStartDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void onClickEndDate(View view) {
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mEndDate.setText((monthOfYear + 1) +  "/" + dayOfMonth + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private Calendar converter(String date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        cal.setTime(sdf.parse(date));
        return cal;
    }

    public void minimizeKeyboard(View view) {
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}