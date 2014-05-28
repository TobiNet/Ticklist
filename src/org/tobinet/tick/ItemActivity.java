package org.tobinet.tick;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
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

	private boolean doubleBackToExitPressedOnce;
	private static DataSource data;
	private ArrayList<Item> list;
	private ArrayList<ItemList> mItemList;
	private int ListID;
	private ListView itemview;
	private int mIndex;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle = "TickList";
    
    private static final String PREFS_NAME = "TickList";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ListID = settings.getInt("ListID", 1);
		mTitle = settings.getString("mTitle", "TickList");
		setTitle(mTitle);
		
		data = new DataSource(this);
		
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

		itemview = (ListView) this.findViewById(R.id.Items);
		list = getAllItems(ListID);
		ItemAdapter adapter = new ItemAdapter(this, list);
		itemview.setAdapter(adapter);
		
		registerForContextMenu(itemview);

		mItemList = getAllItemLists();
		mDrawerList.setAdapter(new ItemListAdapter(this, mItemList));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("ListID", ListID);
		editor.putString("mTitle", mTitle.toString());
		editor.commit();
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			list = getAllItems(mItemList.get(position).getID());
			ListID = mItemList.get(position).getID();
			ItemAdapter adapter = new ItemAdapter(ItemActivity.this, list);
			itemview.setAdapter(adapter);
			mDrawerList.setItemChecked(position, true);
			setTitle(mItemList.get(position).getListName());
			mDrawerLayout.closeDrawer(mDrawerList);
	    }
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
	    getActionBar().setTitle(mTitle);
	}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }		
		switch (item.getItemId()){
			case R.id.addItemList:
				InsertItem();
				return true;
			case R.id.addList:
				InsertItemList();
				return true;
			case R.id.renameList:
				RenameList(ListID);
				return true;
			case R.id.removeList:
				RemoveList(ListID);
				return true;
			case R.id.info:
				Info();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
				super.onBackPressed();
	    finish();
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
	
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				doubleBackToExitPressedOnce=false;
			}
		}, 2000);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		final int index = (info!=null) ? info.position : this.mIndex;
		switch(item.getItemId()){
			case R.id.itemrename:
				RenameDialog(index);
				break;
			case R.id.itemremove:
				RemoveDialog(index);
				break;
			default:
				this.mIndex = index;
		}
		return true;
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void Info() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(R.string.app_name);
		
		builder.setView(View.inflate(this, R.layout.info, null));
		builder.setPositiveButton("OK", null);

		Dialog info = builder.create();
		info.show();
		
	}
	
	public void InsertItem(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(R.string.newelement);
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		builder.setView(input);
		
		builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if (mItemList.size() == 0)
					Toast.makeText(ItemActivity.this, R.string.listfirst, Toast.LENGTH_LONG).show();
				else if(!isEmpty(input)){
					String value = input.getText().toString();
					InsertItem(value);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

	public void InsertItemList(String ItemListName){
		try{
			data.open();
			ItemList il = data.createItemList(ItemListName);
			ListID = il.getID();
			setTitle(il.getListName());
		} catch (Exception ex)	{
			Toast.makeText(ItemActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally{
			data.close();
		}
		Toast.makeText(this, R.string.newlistcreated, Toast.LENGTH_LONG).show();
		RefreshData();
	}
	
	public void InsertItemList(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(R.string.newlist);
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		builder.setView(input);
		
		builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if(!isEmpty(input)){
					String value = input.getText().toString();
					InsertItemList(value);
				}
				else
					Toast.makeText(ItemActivity.this, R.string.entername, Toast.LENGTH_LONG).show();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});
		
		AlertDialog dialog = builder.create();
		
		dialog.show();
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

	public ArrayList<ItemList> getAllItemLists(){
		try{
			data.open();
			mItemList = data.getAllItemLists();			
		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		Collections.reverse(mItemList);
		
		return mItemList;
	}
	
	private boolean isEmpty(EditText Text) {
        return Text.getText().toString().trim().length() == 0;
	}
	
	private void RefreshData(){
		list = getAllItems(ListID);
		itemview.setAdapter(new ItemAdapter(this, list));
		

		mItemList = getAllItemLists();
		mDrawerList.setAdapter(new ItemListAdapter(this, mItemList));
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
	
	private void RenameDialog(int index){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		final Item item = list.get(index);
		
		builder.setTitle(R.string.editelement);
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		builder.setView(input);
		
		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if(!isEmpty(input)){
					String value = input.getText().toString();
					RenameItem(item, value);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});
		
		AlertDialog dialog = builder.create();
		
		dialog.show();

	}

	private void RemoveDialog(int pos){
		final int index = pos;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.removeentry);
    	builder.setMessage(R.string.areyousure);
    	builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which){
    			try{
        		data.open();
        		data.RemoveItem(list.get(index).getID());
            	} catch (Exception ex) {
            		Toast.makeText(ItemActivity.this.getBaseContext(), ex.toString(), Toast.LENGTH_LONG).show();
            	} finally {
            		data.close();
            	}
            	list.remove(index);
            	RefreshData();
                Toast.makeText(ItemActivity.this, R.string.entryremoved, Toast.LENGTH_LONG).show();
    		}
    	});
    	builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which){
    			dialog.dismiss();
    		}
    	});
    	AlertDialog alert = builder.create();
    	alert.show();

	}
	
	private void RenameItem(Item item, String name){
		try {
			data.open();
			data.RenameItem(item, name);
		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		RefreshData();
	}

	private void RenameList(final int ListID){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(R.string.editlist);
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		builder.setView(input);
		
		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if(!isEmpty(input)){
					String value = input.getText().toString();
					RenameItemList(ListID, value);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				return;
			}
		});
		
		AlertDialog dialog = builder.create();
		
		dialog.show();

	}
	
	private void RenameItemList(int ListID, String name){
		try {
			data.open();
			data.RenameList(ListID, name);
			setTitle(name);
		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		RefreshData();
	}

	private void RemoveList(final int ListID){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.removeList);
    	builder.setMessage(R.string.areyousure);
    	builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which){
    			try{
        		data.open();
        		data.RemoveItemList(ListID);
            	} catch (Exception ex) {
            		Toast.makeText(ItemActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
            	} finally {
            		data.close();
            	}
    			setTitle(R.string.app_name);
            	RefreshData();
                Toast.makeText(ItemActivity.this, R.string.entryremoved, Toast.LENGTH_LONG).show();
    		}
    	});
    	builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which){
    			dialog.dismiss();
    		}
    	});
    	AlertDialog alert = builder.create();
    	alert.show();

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
				
				Button minus = (Button) convertView.findViewById(R.id.minus);
				
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
