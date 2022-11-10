package C196PA.BTerbish.StudentApp.UIController;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import C196PA.BTerbish.StudentApp.R;

public class CourseDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_COURSE_ID = "C196PA.BTerbish.StudentApp.Entity.Course";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
    }
}