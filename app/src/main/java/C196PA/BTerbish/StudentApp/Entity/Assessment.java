package C196PA.BTerbish.StudentApp.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "assessmentTable",
        foreignKeys = @ForeignKey(entity = Course.class,
        parentColumns = "id", childColumns = "course"))
public class Assessment {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "assessmentType")
    private String mAssessmentType;

    @ColumnInfo(name = "assessmentTitle")
    private String mAssessmentTitle;

    @ColumnInfo(name = "assessmentStart")
    private String mAssessmentStart;

    @ColumnInfo(name = "assessmentEnd")
    private String mAssessmentEnd;

    @ColumnInfo(name = "course")
    private String mCourse;

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getAssessmentType() {
        return mAssessmentType;
    }

    public void setAssessmentType(String mAssessmentType) {
        this.mAssessmentType = mAssessmentType;
    }

    public String getAssessmentTitle() {
        return mAssessmentTitle;
    }

    public void setAssessmentTitle(String mAssessmentTitle) {
        this.mAssessmentTitle = mAssessmentTitle;
    }

    public String getAssessmentStart() {
        return mAssessmentStart;
    }

    public void setAssessmentStart(String mAssessmentStart) {
        this.mAssessmentStart = mAssessmentStart;
    }

    public String getAssessmentEnd() {
        return mAssessmentEnd;
    }

    public void setAssessmentEnd(String mAssessmentEnd) {
        this.mAssessmentEnd = mAssessmentEnd;
    }

    public String getCourse() {
        return mCourse;
    }

    public void setCourse(String mCourse) {
        this.mCourse = mCourse;
    }
}
