package C196PA.BTerbish.StudentApp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import C196PA.BTerbish.StudentApp.Entity.Term;

@Dao
public interface TermDao {
    @Query("SELECT * FROM termsTable ORDER BY termTitle")
    List<Term> getTerms();

    @Query("SELECT * FROM termsTable WHERE id = :termId")
    Term getTermById(long termId);

    @Query("SELECT termTitle FROM termsTable")
    List<String> getAllTermTitles();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTerm(Term term);

    @Update
    void updateTerm(Term term);

    @Delete
    void deleteTerm(Term term);
}
