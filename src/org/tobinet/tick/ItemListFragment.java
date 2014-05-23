package org.tobinet.tick;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ItemListFragment extends Fragment {

	private DataSource data;
	private Activity activity;
	private ListView listview;
	private ArrayList<ItemList> itemlist;
	
	public static ItemListFragment newInstance(){
		return new ItemListFragment();
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.activity = activity;
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.itemlistfragment, container, false);
		
		data = new DataSource(activity);
		setHasOptionsMenu(true);
		
		listview = (ListView) view.findViewById(R.id.ItemList);
		//listview.setEmptyView(view.findViewById(R.id.emptyView));
		itemlist = getAllItemLists();
		ArrayAdapter<ItemList> adapter = new ArrayAdapter<ItemList>(activity, android.R.layout.simple_list_item_1, itemlist);
		listview.setAdapter(adapter);
		
		
		
		registerForContextMenu(listview);
		
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id){
				/*Intent intent = new Intent(getActivity(), DetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("Carname", adapter.getItem(position).getCarname());
				bundle.putLong("CarID", list.get(position).getID());
				intent.putExtras(bundle);
                startActivity(intent);*/
				
			}
		});
		
		return view;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
			case R.id.addItemList:
				InsertItemList();
				return true;
			default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	public void InsertItemList(String ItemListName){
		try{
			data.open();
			data.createItemList(ItemListName);
		} catch (Exception ex)	{
			Toast.makeText(activity, ex.toString(), Toast.LENGTH_LONG).show();
		} finally{
			data.close();
		}
		Toast.makeText(activity, "Neue Liste angelegt!", Toast.LENGTH_LONG).show();
	}
	
	public void InsertItemList(){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		
		builder.setTitle("Neue Liste anlegen");
		final EditText input = new EditText(activity);
		input.setSingleLine();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		builder.setView(input);
		
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String value = input.getText().toString();
				InsertItemList(value);
			}
		});
		builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});
		
		AlertDialog dialog = builder.create();
		
		dialog.show();
	}
	
	public ArrayList<ItemList> getAllItemLists(){
		try{
			data.open();
			itemlist = data.getAllItemLists();
		} catch (Exception ex) {
			Toast.makeText(activity,ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		Collections.reverse(itemlist);
		
		return itemlist;
	}
}
