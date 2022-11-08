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
import java.util.Random;
import C196PA.BTerbish.StudentApp.Database.StudentDatabase;
import C196PA.BTerbish.StudentApp.Entity.Term;
import C196PA.BTerbish.StudentApp.R;
import android.widget.Toast;

public class TermListAdapter extends AppCompatActivity {

    private final int REQUEST_CODE_NEW_TERM = 0;
    private final int REQUEST_CODE_UPDATE_TERM = 1;
    private final int REQUEST_CODE_DELETE_TERM = 2;
    StudentDatabase mStudentDb;
    private TermAdapter mTermAdapter;
    private RecyclerView mRecyclerView;
    private int[] mTermColors;
    private Term mSelectedTerm;
    private int mSelectedTermPosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        mTermAdapter = new TermAdapter(mStudentDb.termDao().getTerms());
        mRecyclerView.setAdapter(mTermAdapter);
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

        public void addTerm(Term term) {
            mTerms.add(0, term);
            notifyItemInserted(0);
            mRecyclerView.scrollToPosition(0);
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
            mTextView = itemView.findViewById(R.id.termTextView);
        }

        public void bind(Term term, int position) {
            mTerm = term;
            mTextView.setText(term.getTermTitle());

            if (mSelectedTermPosition == position) {
                mTextView.setBackgroundColor(Color.RED);
            } else {
                int colorIndex = new Random().nextInt(8);
                mTextView.setBackgroundColor(mTermColors[colorIndex]);
            }
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(TermListAdapter.this, TermDetailsActivity.class);
            intent.putExtra(TermEditActivity.EXTRA_TERM_ID, mTerm.getId());
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
        Intent intent = new Intent(TermListAdapter.this, TermEditActivity.class);
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
                    mode.finish();
                    mStudentDb.termDao().deleteTerm(mSelectedTerm);
                    mTermAdapter.removeTerm(mSelectedTerm);
                    Toast.makeText(TermListAdapter.this, "Term deleted",
                                    Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.edit:
                    mode.finish();
                    Intent intent = new Intent(TermListAdapter.this, TermEditActivity.class);
                    intent.putExtra(TermEditActivity.EXTRA_TERM_ID, mSelectedTerm.getId());
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
            Toast.makeText(this, "Term added", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_TERM) {
            Toast.makeText(this, "Term updated", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_DELETE_TERM) {
            Toast.makeText(this, "Term deleted", Toast.LENGTH_SHORT).show();
        }
    }
}