package C196PA.BTerbish.StudentApp.Database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import C196PA.BTerbish.StudentApp.DAO.AssessmentDao;
import C196PA.BTerbish.StudentApp.DAO.CourseDao;
import C196PA.BTerbish.StudentApp.DAO.TermDao;
import C196PA.BTerbish.StudentApp.Entity.Assessment;
import C196PA.BTerbish.StudentApp.Entity.Course;
import C196PA.BTerbish.StudentApp.Entity.Term;

@Database(entities = {Term.class, Course.class, Assessment.class}, version = 1, exportSchema = false)
public abstract class StudentDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "StudentApp.db";

    private static StudentDatabase mStudentDatabase;

    public static StudentDatabase getInstance(Context context) {
        //If no DB exists, build.
        if (mStudentDatabase == null) {
            mStudentDatabase = Room.databaseBuilder(context, StudentDatabase.class,
                    DATABASE_NAME).allowMainThreadQueries().build();
        }
        return mStudentDatabase;
    }

    public abstract TermDao termDao();
    public abstract CourseDao courseDao();
    public abstract AssessmentDao assessmentDao();
}
