package C196PA.BTerbish.StudentApp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import C196PA.BTerbish.StudentApp.Entity.Course;

@Dao
public interface CourseDao {
    @Query("SELECT * FROM coursesTable ORDER BY courseTitle")
    List<Course> getCourses();

    @Query("SELECT * FROM coursesTable WHERE term = :termId")
    List<Course> getCoursesByTermId(long termId);

    @Query("SELECT courseTitle FROM coursesTable")
    List<String> getAllCourseTitles();

    @Query("SELECT * FROM coursesTable WHERE id = :courseId")
    Course getCourseById(long courseId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCourse(Course course);

    @Update
    void updateCourse(Course course);

    @Delete
    void deleteCourse(Course course);
}
