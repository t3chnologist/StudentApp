package C196PA.BTerbish.StudentApp.UIController;

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
import java.util.List;
import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Term;
import C196PA.BTerbish.StudentApp.R;
import com.google.android.material.snackbar.Snackbar;

public class TermListAdapter extends AppCompatActivity {

    private final int REQUEST_CODE_NEW_TERM = 0;
    private final int REQUEST_CODE_UPDATE_TERM = 1;
    StudentDatabase mStudentDb;
    private TermAdapter mTermAdapter;
    private RecyclerView mRecyclerView;
    private int[] mTermColors;
    private Term mSelectedTerm;
    private int mSelectedTermPosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;
    private ViewGroup mShowTermsLayout;
    private ViewGroup mNoTermsLayout;
    private List<Term> mTermList;
    private long mTermId;
    private Term mDeletedTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acvitity_term_list_adapter);

        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        mTermColors = getResources().getIntArray(R.array.termColors);
        mRecyclerView = findViewById(R.id.termRecyclerView);
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mStudentDb = StudentDatabase.getInstance(getApplicationContext());
        mNoTermsLayout = findViewById(R.id.noTermsLayout);
        mShowTermsLayout = findViewById(R.id.showTermsLayout);
        mTermList = mStudentDb.termDao().getTerms();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTermList = mStudentDb.termDao().getTerms();
        if (mTermList.size() == 0) {
            displayTerm(false);
        }
        else {
            displayTerm(true);
        }
        mTermAdapter = new TermAdapter(mStudentDb.termDao().getTerms());
        mRecyclerView.setAdapter(mTermAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean("deleteTerm")) {
            getIntent().removeExtra("deleteTerm");
            mTermId = bundle.getLong("termId");
            mSelectedTerm = mStudentDb.termDao().getTermById(mTermId);
            deleteTerm(mSelectedTerm);
        }
    }

    private void displayTerm(boolean display) {
        if (display) {
            mShowTermsLayout.setVisibility(View.VISIBLE);
            mNoTermsLayout.setVisibility(View.GONE);
        } else {
            mShowTermsLayout.setVisibility(View.GONE);
            mNoTermsLayout.setVisibility(View.VISIBLE);
        }
    }

    private class TermAdapter extends RecyclerView.Adapter<TermHolder>{

        private final List<Term> mTerms;

        public TermAdapter(List<Term> terms) {
            mTerms = terms;
        }

        @Override
        public TermHolder onCreateViewHolder(ViewGroup parent, int viewType){

            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new TermHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(TermHolder holder, int position){
            holder.bind(mTerms.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mTerms.size();
        }

        public void removeTerm(Term term) {
            int index = mTerms.indexOf(term);
            if (index >= 0) {
                mTerms.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    private class TermHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private Term mTerm;
        private TextView mTextView;

        public TermHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mTextView = itemView.findViewById(R.id.recyclerTextView);
        }

        public void bind(Term term, int position) {
            mTerm = term;
            mTermId = mTerm.getId();
            mTextView.setText(term.getTermTitle());

            if (mSelectedTermPosition == position) {
                mTextView.setBackgroundColor(Color.BLUE);
            } else {
                int colorIndex = term.getTermTitle().length() % mTermColors.length;
                mTextView.setBackgroundColor(mTermColors[colorIndex]);
            }
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(TermListAdapter.this, TermDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong("termId", mTerm.getId());
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            if (mActionMode != null) {
                return false;
            }
            mActionMode = TermListAdapter.this.startActionMode(mActionModeCallback);
            mSelectedTerm = mTerm;
            mSelectedTermPosition = getAdapterPosition();
            mTermAdapter.notifyItemChanged(mSelectedTermPosition);
            mActionMode = TermListAdapter.this.startActionMode(mActionModeCallback);

            return true;
        }
    }

    public void onTermAddClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("termId", -1);
        Intent intent = new Intent(TermListAdapter.this, TermEditActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_NEW_TERM);
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
                    if (mStudentDb.courseDao().getCoursesByTermId(mSelectedTerm.getId()).size() > 0) {
                        mode.finish();
                        mTermId = mSelectedTerm.getId();
                        showMessage("Unable to delete! Remove associated courses first.",
                                    "See courses");
                        return true;
                    }
                    else {
                        mode.finish();
                        deleteTerm(mSelectedTerm);
                        return true;
                    }

                case R.id.edit:
                    mode.finish();
                    Bundle bundle = new Bundle();
                    bundle.putLong("termId", mSelectedTerm.getId());
                    Intent intent = new Intent(TermListAdapter.this, TermEditActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE_UPDATE_TERM);
                default:
                    return false;
            }
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mTermAdapter.notifyItemChanged(mSelectedTermPosition);
            mSelectedTermPosition = RecyclerView.NO_POSITION;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NEW_TERM) {
            Bundle bundle = data.getExtras();
            mTermId = bundle.getLong("termId");
            String newTermTitle = mStudentDb.termDao().getTermById(mTermId).getTermTitle();
            showMessage("Added: " + newTermTitle, "Add course");
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_TERM) {
            showMessage("Term updated", "Update course");
        }
    }

    public void deleteTerm(Term selectedTerm) {
        mDeletedTerm = selectedTerm;
        mStudentDb.termDao().deleteTerm(selectedTerm);
        mTermAdapter.removeTerm(selectedTerm);
        onResume();
        showMessage("Term deleted", "Undo");
    }

    public void showMessage(String message, String action) {

        Snackbar snackbar = Snackbar.make(findViewById(R.id.termListCoordinatorLayout),
                                          message, Snackbar.LENGTH_LONG);

        if (action == "Undo"){
            snackbar.setAction(action, (v) -> {
                mStudentDb.termDao().insertTerm(mDeletedTerm);
                mTermList.add(mDeletedTerm);
                onResume();
            });
        }
        else if (action == "See courses" || action == "Update course") {
            snackbar.setAction(action, (v) -> {
                Bundle bundle = new Bundle();
                bundle.putLong("termId", mTermId);
                Intent intent = new Intent(TermListAdapter.this, CourseListAdapter.class);
                intent.putExtras(bundle);
                startActivity(intent);
            });
        }
        else if (action == "Add course") {
            snackbar.setAction(action, (v) -> {
                Bundle bundle = new Bundle();
                bundle.putLong("courseId", -1);
                bundle.putLong("termId", mTermId);
                Intent intent = new Intent(TermListAdapter.this, CourseEditActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            });
        }
        snackbar.setDuration(6000);
        snackbar.show();
    }
}