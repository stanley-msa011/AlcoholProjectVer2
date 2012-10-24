package ioio.examples.hello;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class BracListActivity extends ListActivity {
	
	private final static String TAG = "BracListActivity";
	
	private BracDbAdapter mBracDbAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brac_history_list);
		
		mBracDbAdapter = new BracDbAdapter(this);
		mBracDbAdapter.open();
        fillData();
        registerForContextMenu(getListView());
	}
	
	private void fillData() {
        Cursor bracCursor = mBracDbAdapter.fetchAllHistory();
        
//        while (bracCursor.moveToNext()) {
//        	String date = bracCursor.getString(bracCursor.getColumnIndex(BracDbAdapter.KEY_DATE));
//        	String brac = bracCursor.getString(bracCursor.getColumnIndex(BracDbAdapter.KEY_BRAC));
//        }
        
        /* startManagingCursor(Cursor) is deprecated.
         * Consider using CursorLoader class with LoaderManager for HONEYCOMB or later.
         */
        startManagingCursor(bracCursor);
//        getLoaderManager();

        // Create an array to specify the fields we want to display in the list
//        String[] from = new String[]{BracDbAdapter.KEY_BRAC};
        String[] from = new String[]{BracDbAdapter.KEY_DATE, BracDbAdapter.KEY_BRAC};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.brac_row_date, R.id.brac_row_brac};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter brac_values = 
            new SimpleCursorAdapter(this, R.layout.brac_history_row, bracCursor, from, to, 0);
        setListAdapter(brac_values);
        
        mBracDbAdapter.close();
    }
	
}
