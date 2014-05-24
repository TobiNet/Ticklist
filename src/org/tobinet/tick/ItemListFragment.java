package org.tobinet.tick;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemListFragment extends Fragment {

	private DataSource data;
	private Activity activity;
	private ListView listview;
	private ArrayList<ItemList> itemlist;
	private ItemListAdapter adapter;
	private int mIndex;
	
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
		itemlist = getAllItemLists();
		adapter = new ItemListAdapter(activity, itemlist);
		listview.setAdapter(adapter);
		
		registerForContextMenu(listview);
		
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id){
				Intent intent = new Intent(activity, ItemActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("ListName", adapter.getItem(position).getListName());
				bundle.putInt("ListID", adapter.getItem(position).getID());
				intent.putExtras(bundle);
                startActivity(intent);
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
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu,  v,  menuInfo);
		activity.getMenuInflater().inflate(R.menu.itemlistmenu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		final int index = (info!=null) ? info.position : this.mIndex;
		switch(item.getItemId()){
			case R.id.rename:
				RenameDialog(index);
				break;
			case R.id.remove:
				break;
			default:
				this.mIndex = index;
		}
		return true;
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
		RefreshData();
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
				if(!isEmpty(input)){
					String value = input.getText().toString();
					InsertItemList(value);
				}
				else
					Toast.makeText(activity, "Bitte Namen eingeben", Toast.LENGTH_LONG).show();
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

	public void RefreshData(){
		itemlist = getAllItemLists();
		adapter = new ItemListAdapter(activity, itemlist);
		listview.setAdapter(adapter);
	}

	private boolean isEmpty(EditText Text) {
        return Text.getText().toString().trim().length() == 0;
	}
	
	private void RenameDialog(int index){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		
		final ItemList il = itemlist.get(index);
		
		builder.setTitle("Element bearbeiten");
		final EditText input = new EditText(activity);
		input.setSingleLine();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		builder.setView(input);
		
		builder.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if(!isEmpty(input)){
					String value = input.getText().toString();
					RenameItemList(il, value);
				}
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
	
	private void RenameItemList(ItemList il, String name){
		try {
			data.open();
			data.RenameList(il, name);
		} catch (Exception ex) {
			Toast.makeText(activity, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		RefreshData();
	}
	
	private class ItemListAdapter extends ArrayAdapter<ItemList>{
		
		private Context context;
		private ArrayList<ItemList> list;
		
		public ItemListAdapter(Context context, ArrayList<ItemList> list){
			super(context, 0, list);
			this.context = context;
			this.list = list;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.itemlistviewitem, parent, false);
			}
			
			ItemList il = list.get(position);
			
			if (il != null){
				TextView Name = (TextView) convertView.findViewById(R.id.itemlistviewitemname);
				Name.setText(il.getListName());
								
			}
			
			return convertView;
		}
		
	}
}
