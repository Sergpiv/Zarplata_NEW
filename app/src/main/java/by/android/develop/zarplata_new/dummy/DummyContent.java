package by.android.develop.zarplata_new.dummy;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.android.develop.zarplata_new.ItemListActivity;
import by.android.develop.zarplata_new.R;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.Settings.System.getString;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();


    public static void setContext(Cursor c, SQLiteDatabase db, boolean bool) {
        Integer i;
        if (c != null) {
            i = 1;
            ITEMS.clear();
            ITEM_MAP.clear();
            if (c.moveToFirst()) {

                do {
                    addItem(createDummyItem(i, c, db, bool));
                        i = i + 1;
                    }
                    while (c.moveToNext()) ;
                }
        }
    }


    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position, Cursor c, SQLiteDatabase db, boolean bool) {
        return new DummyItem(String.valueOf(position) + ".", c.getString(1), makeDetails(0,c.getString(0), db, bool), makeDetails(1,c.getString(0), db, bool), makeDetails(2,c.getString(0), db, bool));
    }

    private static String makeDetails(int i, String cod, SQLiteDatabase db, boolean bool) {
        Cursor cc;
        StringBuilder builder = new StringBuilder();
       int length=20;
       if (bool)
       {cc=db.rawQuery(ItemListActivity.getAppResources().getString(R.string.query_rasz), new String[]{cod,ItemListActivity.sPref.getString("tab","")} );}
       else {cc=db.rawQuery(ItemListActivity.getAppResources().getString(R.string.query_rasz_all), new String[]{cod} );};

        if (cc != null) {
            if (cc.moveToFirst()) {

                do {if (cc.getString(i).length()>length)
                    builder.append(cc.getString(i).substring(0,length)+"\n"+"\n");
                    else builder.append(cc.getString(i)+"\n"+"\n");
                }
                while (cc.moveToNext()) ;
            }
        }

        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String oper;
        public final String rasz;
        public final String poasn;

        public DummyItem(String id, String content, String oper, String rasz, String poasn) {
            this.id = id;
            this.content = content;
            this.oper= oper;
            this.rasz= rasz;
            this.poasn= poasn;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
