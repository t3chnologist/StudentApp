package C196PA.BTerbish.StudentApp.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "coursesTable",
        foreignKeys = @ForeignKey(entity = Term.class,
        parentColumns = "id", childColumns = "term"))
public class Course {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "courseTitle")
    private String mCourseTitle;

    @ColumnInfo(name = "courseStartDate")
    private String mCourseStartDate;

    @ColumnInfo(name = "courseEndDate")
    private String mCourseEndDate;

    @ColumnInfo(name = "courseStatus")
    private String mCourseStatus;

    @ColumnInfo(name = "courseNote")
    private String mCourseNote;

    @ColumnInfo(name = "instructorName")
    private String mInstructorName;

    @ColumnInfo(name = "instructorPhone")
    private String mInstructorPhone;

    @ColumnInfo(name = "instructorEmail")
    private String mInstructorEmail;

    @ColumnInfo(name = "term")
    private String mTerm;

    @ColumnInfo(name = "lastUpdated")
    private long mLastUpdated;

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getCourseTitle() {
        return mCourseTitle;
    }

    public void setCourseTitle(String mCourseTitle) {
        this.mCourseTitle = mCourseTitle;
    }

    public String getCourseStartDate() {
        return mCourseStartDate;
    }

    public void setCourseStartDate(String mCourseStartDate) {
        this.mCourseStartDate = mCourseStartDate;
    }

    public String getCourseEndDate() {
        return mCourseEndDate;
    }

    public void setCourseEndDate(String mCourseEndDate) {
        this.mCourseEndDate = mCourseEndDate;
    }

    public String getCourseStatus() {
        return mCourseStatus;
    }

    public void setCourseStatus(String mCourseStatus) {
        this.mCourseStatus = mCourseStatus;
    }

    public String getCourseNote() {
        return mCourseNote;
    }

    public void setCourseNote(String mCourseNote) {
        this.mCourseNote = mCourseNote;
    }

    public String getInstructorName() {
        return mInstructorName;
    }

    public void setInstructorName(String mInstructorName) {
        this.mInstructorName = mInstructorName;
    }

    public String getInstructorPhone() {
        return mInstructorPhone;
    }

    public void setInstructorPhone(String mInstructorPhone) {
        this.mInstructorPhone = mInstructorPhone;
    }

    public String getInstructorEmail() {
        return mInstructorEmail;
    }

    public void setInstructorEmail(String mInstructorEmail) {
        this.mInstructorEmail = mInstructorEmail;
    }

    public String getTerm() {
        return mTerm;
    }

    public void setTerm(String mTerm) {
        this.mTerm = mTerm;
    }

    public long getLastUpdated() {
        return mLastUpdated;
    }

    public void setLastUpdated(long mLastUpdated) {
        this.mLastUpdated = mLastUpdated;
    }

    @Override
    public String toString() {
        return "Course{" +
                "mId=" + mId +
                ", mCourseTitle='" + mCourseTitle + '\'' +
                ", mCourseStartDate='" + mCourseStartDate + '\'' +
                ", mCourseEndDate='" + mCourseEndDate + '\'' +
                ", mCourseStatus='" + mCourseStatus + '\'' +
                '}';
    }
}
