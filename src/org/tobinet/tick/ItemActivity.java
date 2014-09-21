package org.tobinet.tick;

import java.text.NumberFormat;
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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemActivity extends Activity {

	private boolean doubleBackToExitPressedOnce;
	private static DataSource data;
	private ArrayList<Item> list;
	private ArrayList<ItemList> mItemList;
	private int ListID;
	private ListView itemview;
	private int mIndex;
	private boolean showtpd;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	private final CharSequence mDrawerTitle = "TickList";

	private static final String PREFS_NAME = "TickList";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_drawer);

		final SharedPreferences settings = this.getSharedPreferences(
				PREFS_NAME, 0);
		this.ListID = settings.getInt("ListID", 1);
		this.mTitle = settings.getString("mTitle", "TickList");
		this.showtpd = settings.getBoolean("isChecked", true);
		this.setTitle(this.mTitle);

		data = new DataSource(this);

		this.mDrawerLayout = (DrawerLayout) this
				.findViewById(R.id.drawer_layout);
		this.mDrawerList = (ListView) this.findViewById(R.id.left_drawer);

		this.mDrawerToggle = new ActionBarDrawerToggle(this,
				this.mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			@Override
			public void onDrawerClosed(final View view) {
				super.onDrawerClosed(view);
				ItemActivity.this.getActionBar().setTitle(
						ItemActivity.this.mTitle);
			}

			@Override
			public void onDrawerOpened(final View drawerView) {
				super.onDrawerOpened(drawerView);
				ItemActivity.this.getActionBar().setTitle(
						ItemActivity.this.mDrawerTitle);
			}
		};

		this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setHomeButtonEnabled(true);

		this.itemview = (ListView) this.findViewById(R.id.Items);
		this.list = this.getAllItems(this.ListID);
		final ItemAdapter adapter = new ItemAdapter(this, this.list);
		this.itemview.setAdapter(adapter);

		this.registerForContextMenu(this.itemview);

		this.mItemList = this.getAllItemLists();
		this.mDrawerList.setAdapter(new ItemListAdapter(this, this.mItemList));
		this.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	}

	@Override
	protected void onStop() {
		super.onStop();

		final SharedPreferences settings = this.getSharedPreferences(
				PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putInt("ListID", this.ListID);
		editor.putString("mTitle", this.mTitle.toString());
		editor.putBoolean("isChecked", this.showtpd);
		editor.commit();
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(final AdapterView<?> parent, final View view,
				final int position, final long id) {
			ItemActivity.this.list = ItemActivity.this
					.getAllItems(ItemActivity.this.mItemList.get(position)
							.getID());
			ItemActivity.this.ListID = ItemActivity.this.mItemList
					.get(position).getID();
			final ItemAdapter adapter = new ItemAdapter(ItemActivity.this,
					ItemActivity.this.list);
			ItemActivity.this.itemview.setAdapter(adapter);
			ItemActivity.this.mDrawerList.setItemChecked(position, true);
			ItemActivity.this.setTitle(ItemActivity.this.mItemList
					.get(position).getListName());
			ItemActivity.this.mDrawerLayout
					.closeDrawer(ItemActivity.this.mDrawerList);
		}
	}

	@Override
	public void setTitle(final CharSequence title) {
		this.mTitle = title;
		this.getActionBar().setTitle(this.mTitle);
	}

	@Override
	protected void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		this.mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		this.getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v,
			final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		this.getMenuInflater().inflate(R.menu.itemmenu, menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		final MenuItem checkable = menu.findItem(R.id.toggletpd);
		checkable.setChecked(this.showtpd);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {

		if (this.mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.addItemList:
			this.InsertItem();
			return true;
		case R.id.addList:
			this.InsertItemList();
			return true;
		case R.id.renameList:
			this.RenameList(this.ListID);
			return true;
		case R.id.removeList:
			this.RemoveList(this.ListID);
			return true;
		case R.id.toggletpd:
			this.showtpd = !item.isChecked();
			this.RefreshData();
			item.setChecked(this.showtpd);
			return true;
		case R.id.info:
			this.Info();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		if (this.doubleBackToExitPressedOnce) {
			super.onBackPressed();
			this.finish();
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, R.string.backagain, Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ItemActivity.this.doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		final int index = (info != null) ? info.position : this.mIndex;
		switch (item.getItemId()) {
		case R.id.itemrename:
			this.RenameDialog(index);
			break;
		case R.id.itemremove:
			this.RemoveDialog(index);
			break;
		case R.id.itemreset:
			this.ItemresetDialog(index);
			break;
		default:
			this.mIndex = index;
		}
		return true;
	}

	@Override
	public void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void Info() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.app_name);

		builder.setView(View.inflate(this, R.layout.info, null));
		builder.setPositiveButton("OK", null);

		final Dialog info = builder.create();
		info.show();

	}

	public void InsertItem() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.newelement);
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		builder.setView(input);

		builder.setPositiveButton(R.string.create,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						if (ItemActivity.this.mItemList.size() == 0) {
							Toast.makeText(ItemActivity.this,
									R.string.listfirst, Toast.LENGTH_LONG)
									.show();
						} else if (!ItemActivity.this.isEmpty(input)) {
							final String value = input.getText().toString();
							ItemActivity.this.InsertItem(value);
						}
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						return;
					}
				});

		final AlertDialog dialog = builder.create();

		dialog.show();
	}

	public void InsertItem(final String name) {
		try {
			data.open();
			data.createItem(this.ListID, name, 0);
		} catch (final Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		this.RefreshData();
	}

	public void InsertItemList(final String ItemListName) {
		try {
			data.open();
			final ItemList il = data.createItemList(ItemListName);
			this.ListID = il.getID();
			this.setTitle(il.getListName());
		} catch (final Exception ex) {
			Toast.makeText(ItemActivity.this, ex.toString(), Toast.LENGTH_LONG)
					.show();
		} finally {
			data.close();
		}
		Toast.makeText(this, R.string.newlistcreated, Toast.LENGTH_LONG).show();
		this.RefreshData();
	}

	public void InsertItemList() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.newlist);
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		builder.setView(input);

		builder.setPositiveButton(R.string.create,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						if (!ItemActivity.this.isEmpty(input)) {
							final String value = input.getText().toString();
							ItemActivity.this.InsertItemList(value);
						} else {
							Toast.makeText(ItemActivity.this,
									R.string.entername, Toast.LENGTH_LONG)
									.show();
						}
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						return;
					}
				});

		final AlertDialog dialog = builder.create();

		dialog.show();
	}

	public ArrayList<Item> getAllItems(final int ListID) {
		try {
			data.open();
			this.list = data.getAllItems(ListID);
		} catch (final Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		Collections.reverse(this.list);

		return this.list;
	}

	public ArrayList<ItemList> getAllItemLists() {
		try {
			data.open();
			this.mItemList = data.getAllItemLists();
		} catch (final Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		Collections.reverse(this.mItemList);

		return this.mItemList;
	}

	private boolean isEmpty(final EditText Text) {
		return Text.getText().toString().trim().length() == 0;
	}

	private void RefreshData() {
		this.list = this.getAllItems(this.ListID);
		this.itemview.setAdapter(new ItemAdapter(this, this.list));

		this.mItemList = this.getAllItemLists();
		this.mDrawerList.setAdapter(new ItemListAdapter(this, this.mItemList));
	}

	private void PlusTick(final int ID) {
		try {
			data.open();
			data.TickPlus(ID, this.ListID);
		} catch (final Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		this.RefreshData();
	}

	private void MinusTick(final int ID) {
		try {
			data.open();
			data.TickMinus(ID, this.ListID);
		} catch (final Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		this.RefreshData();
	}

	private void RenameDialog(final int index) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		final Item item = this.list.get(index);

		builder.setTitle(R.string.editelement);
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		builder.setView(input);

		builder.setPositiveButton(R.string.save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						if (!ItemActivity.this.isEmpty(input)) {
							final String value = input.getText().toString();
							ItemActivity.this.RenameItem(item, value);
						}
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						return;
					}
				});

		final AlertDialog dialog = builder.create();

		dialog.show();

	}

	private void RemoveDialog(final int pos) {
		final int index = pos;

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.removeentry);
		builder.setMessage(R.string.areyousure);
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						try {
							data.open();
							data.RemoveItem(ItemActivity.this.list.get(index)
									.getID());
						} catch (final Exception ex) {
							Toast.makeText(ItemActivity.this.getBaseContext(),
									ex.toString(), Toast.LENGTH_LONG).show();
						} finally {
							data.close();
						}
						ItemActivity.this.list.remove(index);
						ItemActivity.this.RefreshData();
						Toast.makeText(ItemActivity.this,
								R.string.entryremoved, Toast.LENGTH_LONG)
								.show();
					}
				});
		builder.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						dialog.dismiss();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();

	}

	private void RenameItem(final Item item, final String name) {
		try {
			data.open();
			data.RenameItem(item, name);
		} catch (final Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		this.RefreshData();
	}

	private void RenameList(final int ListID) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.editlist);
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		builder.setView(input);

		builder.setPositiveButton(R.string.save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						if (!ItemActivity.this.isEmpty(input)) {
							final String value = input.getText().toString();
							ItemActivity.this.RenameItemList(ListID, value);
						}
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						return;
					}
				});

		final AlertDialog dialog = builder.create();

		dialog.show();

	}

	private void RenameItemList(final int ListID, final String name) {
		try {
			data.open();
			data.RenameList(ListID, name);
			this.setTitle(name);
		} catch (final Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		this.RefreshData();
	}

	private void RemoveList(final int ListID) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.removeList);
		builder.setMessage(R.string.areyousure);
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						try {
							data.open();
							data.RemoveItemList(ListID);
						} catch (final Exception ex) {
							Toast.makeText(ItemActivity.this, ex.toString(),
									Toast.LENGTH_LONG).show();
						} finally {
							data.close();
						}
						ItemActivity.this.setTitle(R.string.app_name);
						ItemActivity.this.RefreshData();
						Toast.makeText(ItemActivity.this,
								R.string.entryremoved, Toast.LENGTH_LONG)
								.show();
					}
				});
		builder.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						dialog.dismiss();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();

	}

	private void ItemresetDialog(final int pos) {
		final int index = pos;

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.resetelement);
		builder.setMessage(R.string.areyousure);
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						try {
							data.open();
							data.ResetItem(ItemActivity.this.list.get(index)
									.getID());
						} catch (final Exception ex) {
							Toast.makeText(ItemActivity.this.getBaseContext(),
									ex.toString(), Toast.LENGTH_LONG).show();
						} finally {
							data.close();
						}
						ItemActivity.this.RefreshData();
					}
				});
		builder.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						dialog.dismiss();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();

	}

	private double getTicksperDay(final int ListID, final int ItemID) {
		double hpd = 0;
		try {
			data.open();
			hpd = data.getTicksperDay(ListID, ItemID);
		} catch (final Exception ex) {
			Toast.makeText(ItemActivity.this, ex.toString(), Toast.LENGTH_LONG)
					.show();
		} finally {
			data.close();
		}
		return hpd;
	}

	private class ItemAdapter extends ArrayAdapter<Item> {

		private final Context context;
		private final ArrayList<Item> list;

		public ItemAdapter(final Context context, final ArrayList<Item> list) {
			super(context, 0, list);
			this.context = context;
			this.list = list;
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			if (convertView == null) {
				final LayoutInflater inflater = (LayoutInflater) this.context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				if (ItemActivity.this.showtpd) {
					convertView = inflater.inflate(R.layout.items, parent,
							false);
				} else {
					convertView = inflater
							.inflate(R.layout.item, parent, false);
				}
			}

			final Button plus = (Button) convertView.findViewById(R.id.Plus);

			plus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					ItemActivity.this.PlusTick(ItemAdapter.this.list.get(
							position).getID());
				}
			});

			final Button minus = (Button) convertView.findViewById(R.id.minus);

			minus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					ItemActivity.this.MinusTick(ItemAdapter.this.list.get(
							position).getID());
				}
			});

			final Item i = this.list.get(position);

			if (i != null) {
				final TextView Name = (TextView) convertView
						.findViewById(R.id.ItemName);
				Name.setText(i.getItemName());

				final TextView Ticks = (TextView) convertView
						.findViewById(R.id.ItemTicks);
				Ticks.setText(String.valueOf(i.getTicks()));

				if (ItemActivity.this.showtpd) {
					final TextView tpd = (TextView) convertView
							.findViewById(R.id.tpd);

					final NumberFormat nf = NumberFormat.getNumberInstance();
					nf.setMinimumFractionDigits(0);
					nf.setMaximumFractionDigits(2);

					tpd.setText(nf.format(ItemActivity.this.getTicksperDay(
							i.getListID(), i.getID())));
				}
			}

			return convertView;
		}

	}

	private class ItemListAdapter extends ArrayAdapter<ItemList> {

		private final Context context;
		private final ArrayList<ItemList> list;

		public ItemListAdapter(final Context context,
				final ArrayList<ItemList> list) {
			super(context, 0, list);
			this.context = context;
			this.list = list;
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			if (convertView == null) {
				final LayoutInflater inflater = (LayoutInflater) this.context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.itemlistviewitem,
						parent, false);
			}

			final ItemList il = this.list.get(position);

			if (il != null) {
				final TextView Name = (TextView) convertView
						.findViewById(R.id.itemlistviewitemname);
				Name.setText(il.getListName());
			}

			return convertView;
		}

	}
}
