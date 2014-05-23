package org.tobinet.tick;

import android.support.v4.app.Fragment;

public class ItemFragment extends Fragment {

	public static ItemFragment newInstance(){
		return new ItemFragment();
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
