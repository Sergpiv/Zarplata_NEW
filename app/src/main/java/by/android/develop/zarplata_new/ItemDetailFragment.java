package by.android.develop.zarplata_new;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import by.android.develop.zarplata_new.dummy.DummyContent;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
String subStr1 = "",subStr2="";
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            TextView text=activity.findViewById(R.id.textView_rasz);
            if (mItem.content.indexOf(" ")!=-1) {
             subStr1=mItem.content.substring(0,mItem.content.indexOf(" "));
             if (mItem.content.indexOf(" ")<mItem.content.length())  subStr2=mItem.content.substring(mItem.content.indexOf(" ")+1);
            }
            text.setText(subStr1+"\n"+subStr2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {


            ((TextView) rootView.findViewById(R.id.item_oper)).setText(mItem.oper);
            ((TextView) rootView.findViewById(R.id.item_rasz)).setText(mItem.rasz);
            ((TextView) rootView.findViewById(R.id.item_poasnenie)).setText(mItem.poasn);
        }

        return rootView;
    }
}
