package by.android.develop.zarplata_new;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import by.android.develop.zarplata_new.dummy.DummyContent;

import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements View.OnLongClickListener{
    DatabaseHelper dbHelper;
   public static SharedPreferences sPref;
    private static Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resources = getResources();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_item_list);
        dbHelper = new DatabaseHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        sPref = getSharedPreferences(getString(R.string.APP_PREFERENCES_NAME),MODE_PRIVATE);
        Cursor curs=db.rawQuery(getString(R.string.query_detal), new String[]{sPref.getString("tab", "")} );
        DummyContent.setContext(curs, db, true);
        curs.close();
        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        TextView textView_detal=findViewById(R.id.textView_detal);
        textView_detal.setOnLongClickListener(this);
    }

    public static Resources getAppResources() {
        return resources;
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS));
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.textView_detal:
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor curs = db.rawQuery(getString(R.string.query_detal_all), null);
                DummyContent.setContext(curs, db, false );
                curs.close();
                View recyclerView = findViewById(R.id.item_list);
                assert recyclerView != null;
                setupRecyclerView((RecyclerView) recyclerView);

                break;

        }return true;
    }
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);
                    context.startActivity(intent);

            }
        };

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<DummyContent.DummyItem> items) {
            mValues = items;
            mParentActivity = parent;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
