package org.tobinet.tick;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class ItemActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		Bundle bundle = getIntent().getExtras();
		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/*
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
		
	}*/
}
