package C196PA.BTerbish.StudentApp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import C196PA.BTerbish.StudentApp.Entity.Assessment;

@Dao
public interface AssessmentDao {
    @Query("SELECT * FROM assessmentTable ORDER BY assessmentTitle")
    List<Assessment> getAssessments();

    @Query("SELECT * FROM assessmentTable WHERE course = :courseId")
    List<Assessment> getAssessmentsByCourseId(long courseId);

    @Query("SELECT * FROM assessmentTable WHERE id = :assessmentId")
    Assessment getAssessmentById(long assessmentId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAssessment(Assessment assessment);

    @Update
    void updateAssessment(Assessment assessment);

    @Delete
    void deleteAssessment(Assessment assessment);
}
