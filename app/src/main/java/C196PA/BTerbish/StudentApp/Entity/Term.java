package C196PA.BTerbish.StudentApp.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "termsTable")
public class Term {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @NonNull
    @ColumnInfo(name = "termTitle")
    private String mTermTitle;

    @ColumnInfo(name = "startDate")
    private String mStartDate;

    @ColumnInfo(name = "endDate")
    private String mEndDate;

    @ColumnInfo(name = "lastUpdated")
    private long mLastUpdated;

    public Term() {
        mLastUpdated = System.currentTimeMillis();
    };

    public Term(@NonNull String termTitle, String startDate, String endDate) {
        mTermTitle = termTitle;
        mStartDate = startDate;
        mEndDate = endDate;
        mLastUpdated = System.currentTimeMillis();
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    @NonNull
    public String getTermTitle() {
        return mTermTitle;
    }

    public void setTermTitle(@NonNull String mTermTitle) {
        this.mTermTitle = mTermTitle;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String mStartDate) {
        this.mStartDate = mStartDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String mEndDate) {
        this.mEndDate = mEndDate;
    }

    public long getLastUpdated() {
        return mLastUpdated;
    }

    public void setLastUpdated(long mLastUpdated) {
        this.mLastUpdated = mLastUpdated;
    }

    @Override
    public String toString() {
        return "Term{" +
                "mId=" + mId +
                ", mTermTitle='" + mTermTitle + '\'' +
                ", mStartDate='" + mStartDate + '\'' +
                ", mEndDate='" + mEndDate + '\'' +
                ", mLastUpdated='" + mLastUpdated + '\'' +
                '}';
    }
}