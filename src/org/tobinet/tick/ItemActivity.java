package org.tobinet.tick;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ItemActivity extends Activity {

	private static DataSource data;
	private ArrayList<Item> list;
	private String ListName;
	private int ListID;
	private ListView itemview;
	private int mIndex;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		data = new DataSource(this);
		
		Bundle bundle = getIntent().getExtras();
		ListName = bundle.getString("ListName");
		ListID = bundle.getInt("ListID");

		getActionBar().setTitle(ListName);
		
		itemview = (ListView) this.findViewById(R.id.Items);
		list = getAllItems(ListID);
		ItemAdapter adapter = new ItemAdapter(this, list);
		itemview.setAdapter(adapter);
		
		registerForContextMenu(itemview);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu,  v,  menuInfo);
		this.getMenuInflater().inflate(R.menu.itemmenu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case android.R.id.home:
				finish();
				return true;
			case R.id.addItemList:
				InsertItem();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		final int index = (info!=null) ? info.position : this.mIndex;
		switch(item.getItemId()){
			case R.id.itemrename:
				break;
			case R.id.itemremove:
				break;
			default:
				this.mIndex = index;
		}
		return true;
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

	}
	
	public void InsertItem(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Neues Element anlegen");
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		builder.setView(input);
		
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if(!isEmpty(input)){
					String value = input.getText().toString();
					InsertItem(value);
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
	
	public void InsertItem(String name){
		try{
			data.open();
			data.createItem(ListID, name, 0);
		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally{
			data.close();
		}
		RefreshData();
	}
	
	public ArrayList<Item> getAllItems(int ListID){
		try{
			data.open();
			list = data.getAllItems(ListID);
		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		Collections.reverse(list);
		
		return list;
	}

	private boolean isEmpty(EditText Text) {
        return Text.getText().toString().trim().length() == 0;
	}
	
	private void RefreshData(){
		list = getAllItems(ListID);
		ItemAdapter adapter = new ItemAdapter(this, list);
		itemview.setAdapter(adapter);
	}
	
	private void PlusTick(int ID){
		try{
			data.open();
			data.TickPlus(ID, ListID);
		} catch (Exception ex){
				Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			} finally {
			data.close();
		}
		RefreshData();
	}

	private void MinusTick(int ID){
		try{
			data.open();
			data.TickMinus(ID, ListID);
		} catch (Exception ex){
				Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			} finally {
			data.close();
		}
		RefreshData();
	}
	
	private class ItemAdapter extends ArrayAdapter<Item>{
		
		private Context context;
		private ArrayList<Item> list;
		
		public ItemAdapter(Context context, ArrayList<Item> list){
			super(context, 0, list);
			this.context = context;
			this.list = list;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.items, parent, false);
				
				Button plus = (Button) convertView.findViewById(R.id.Plus);
				
				plus.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PlusTick(list.get(position).getID());

					}
				});
				
				Button minus = (Button) convertView.findViewById(R.id.Minus);
				
				minus.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						MinusTick(list.get(position).getID());
					}
				});
			}
			
			Item i = list.get(position);
			
			if (i != null){
				TextView Name = (TextView) convertView.findViewById(R.id.ItemName);
				Name.setText(i.getItemName());
				
				TextView Ticks = (TextView) convertView.findViewById(R.id.ItemTicks);
				Ticks.setText(String.valueOf(i.getTicks()));								
			}
			
			return convertView;
		}
		
	}
}
