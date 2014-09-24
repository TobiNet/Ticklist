package org.tobinet.tick;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

import org.tobinet.tick.ColorChooser.AmbilWarnaDialog;
import org.tobinet.tick.ColorChooser.AmbilWarnaDialog.OnAmbilWarnaListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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
	private List<Item> list;
	private List<ItemList> mItemList;
	private int listID;
	private ListView itemview;
	private int mIndex;
	private boolean showtpd;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	private static final CharSequence mDrawerTitle = "TickList";
	private static final String TAG = "ItemActivity";
	private static final String PREFS_NAME = "TickList";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_drawer);

		final SharedPreferences settings = this.getSharedPreferences(
				PREFS_NAME, 0);
		this.listID = settings.getInt("ListID", 1);
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
				ItemActivity.this.getActionBar().setTitle(mDrawerTitle);
			}
		};

		this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setHomeButtonEnabled(true);

		this.itemview = (ListView) this.findViewById(R.id.Items);
		this.list = this.getAllItems(this.listID);
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
		editor.putInt("ListID", this.listID);
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
			ItemActivity.this.listID = ItemActivity.this.mItemList
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
			this.insertItem();
			return true;
		case R.id.addList:
			this.insertItemList();
			return true;
		case R.id.renameList:
			this.renameList(this.listID);
			return true;
		case R.id.removeList:
			this.removeList(this.listID);
			return true;
		case R.id.toggletpd:
			this.showtpd = !item.isChecked();
			this.refreshData();
			item.setChecked(this.showtpd);
			return true;
		case R.id.info:
			this.info();
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
			this.renameDialog(index);
			break;
		case R.id.itemremove:
			this.mRemoveDialog(index);
			break;
		case R.id.itemreset:
			this.itemResetDialog(index);
			break;
		case R.id.itemcolor:
			this.itemSetColor(index, this.list.get(index).getColor());
			break;
		default:
			this.mIndex = index;
		}
		return true;
	}

	private void info() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		try {
			builder.setTitle(this.getResources().getString(R.string.app_name)
					+ " - "
					+ this.getPackageManager().getPackageInfo(
							this.getPackageName(), 0).versionName.toString());
		} catch (final NameNotFoundException e) {
			Log.v(TAG, e.toString());
			builder.setTitle(R.string.app_name);
		}

		final View view = View.inflate(this, R.layout.info, null);
		builder.setView(view);
		builder.setPositiveButton("OK", null);

		view.findViewById(R.id.rateApp).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(final View v) {
						final Uri uri = Uri.parse("market://details?id="
								+ ItemActivity.this.getPackageName());
						final Intent goToMarket = new Intent(
								Intent.ACTION_VIEW, uri);
						try {
							ItemActivity.this.startActivity(goToMarket);
						} catch (final ActivityNotFoundException e) {
							ItemActivity.this.startActivity(new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("http://play.google.com/store/apps/details?id="
											+ ItemActivity.this
													.getPackageName())));
						}

					}
				});

		final Dialog info = builder.create();
		info.show();
	}

	public void insertItem() {
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
						if (ItemActivity.this.mItemList.isEmpty()) {
							Toast.makeText(ItemActivity.this,
									R.string.listfirst, Toast.LENGTH_LONG)
									.show();
						} else if (!ItemActivity.this.isEmpty(input)) {
							final String value = input.getText().toString();
							ItemActivity.this.insertItem(value);
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

	public void insertItem(final String name) {
		try {
			data.open();
			data.createItem(this.listID, name, 0, 0);
		} catch (final Exception ex) {
			Log.v(TAG, ex.toString());
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		this.refreshData();
	}

	public void insertItemList(final String itemListName) {
		try {
			data.open();
			final ItemList il = data.createItemList(itemListName);
			this.listID = il.getID();
			this.setTitle(il.getListName());
		} catch (final Exception ex) {
			Log.v(TAG, ex.toString());
			Toast.makeText(ItemActivity.this, ex.toString(), Toast.LENGTH_LONG)
					.show();
		} finally {
			data.close();
		}
		Toast.makeText(this, R.string.newlistcreated, Toast.LENGTH_LONG).show();
		this.refreshData();
	}

	public void insertItemList() {
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
							ItemActivity.this.insertItemList(value);
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

	public List<Item> getAllItems(final int listID) {
		try {
			data.open();
			this.list = data.getAllItems(listID);
		} catch (final Exception ex) {
			Log.v(TAG, ex.toString());
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		Collections.reverse(this.list);

		return this.list;
	}

	public List<ItemList> getAllItemLists() {
		try {
			data.open();
			this.mItemList = data.getAllItemLists();
		} catch (final Exception ex) {
			Log.v(TAG, ex.toString());
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		Collections.reverse(this.mItemList);

		return this.mItemList;
	}

	private boolean isEmpty(final EditText text) {
		return text.getText().toString().trim().length() == 0;
	}

	private void refreshData() {
		this.list = this.getAllItems(this.listID);
		this.itemview.setAdapter(new ItemAdapter(this, this.list));

		this.mItemList = this.getAllItemLists();
		this.mDrawerList.setAdapter(new ItemListAdapter(this, this.mItemList));
	}

	private void plusTick(final int iD) {
		try {
			data.open();
			data.tickPlus(iD, this.listID);
		} catch (final Exception ex) {
			Log.v(TAG, ex.toString());
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		this.refreshData();
	}

	private void minusTick(final int iD) {
		try {
			data.open();
			data.tickMinus(iD, this.listID);
		} catch (final Exception ex) {
			Log.v(TAG, ex.toString());
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		this.refreshData();
	}

	private void renameDialog(final int index) {
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
							ItemActivity.this.renameItem(item, value);
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

	private void mRemoveDialog(final int pos) {
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
							data.removeItem(ItemActivity.this.list.get(index)
									.getID());
						} catch (final Exception ex) {
							Log.v(TAG, ex.toString());
							Toast.makeText(ItemActivity.this.getBaseContext(),
									ex.toString(), Toast.LENGTH_LONG).show();
						} finally {
							data.close();
						}
						ItemActivity.this.list.remove(index);
						ItemActivity.this.refreshData();
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

	private void renameItem(final Item item, final String name) {
		try {
			data.open();
			data.renameItem(item, name);
		} catch (final Exception ex) {
			Log.v(TAG, ex.toString());
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		this.refreshData();
	}

	private void renameList(final int listID) {
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
							ItemActivity.this.renameItemList(listID, value);
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

	private void renameItemList(final int listID, final String name) {
		try {
			data.open();
			data.renameList(listID, name);
			this.setTitle(name);
		} catch (final Exception ex) {
			Log.v(TAG, ex.toString());
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		this.refreshData();
	}

	private void removeList(final int listID) {

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
							data.removeItemList(listID);
						} catch (final Exception ex) {
							Log.v(TAG, ex.toString());
							Toast.makeText(ItemActivity.this, ex.toString(),
									Toast.LENGTH_LONG).show();
						} finally {
							data.close();
						}
						ItemActivity.this.setTitle(R.string.app_name);
						ItemActivity.this.refreshData();
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

	private void itemResetDialog(final int pos) {
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
							data.resetItem(ItemActivity.this.list.get(index)
									.getID());
						} catch (final Exception ex) {
							Log.v(TAG, ex.toString());
							Toast.makeText(ItemActivity.this.getBaseContext(),
									ex.toString(), Toast.LENGTH_LONG).show();
						} finally {
							data.close();
						}
						ItemActivity.this.refreshData();
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

	private double getTicksperDay(final int listID, final int itemID) {
		double hpd = 0;
		try {
			data.open();
			hpd = data.getTicksperDay(listID, itemID);
		} catch (final Exception ex) {
			Log.v(TAG, ex.toString());
			Toast.makeText(ItemActivity.this, ex.toString(), Toast.LENGTH_LONG)
					.show();
		} finally {
			data.close();
		}
		return hpd;
	}

	private void itemSetColor(final int index, final int color) {

		final AmbilWarnaDialog dia = new AmbilWarnaDialog(this, color,
				new OnAmbilWarnaListener() {

					@Override
					public void onOk(final AmbilWarnaDialog dialog,
							final int color) {

						ItemActivity.this.setColor(
								ItemActivity.this.list.get(index), color);
					}

					@Override
					public void onCancel(final AmbilWarnaDialog dialog) {

					}
				});
		dia.show();
	}

	public void setColor(final Item item, final int color) {
		try {
			data.open();
			data.setColor(item, color);
		} catch (final Exception ex) {
			Log.v(TAG, ex.toString());
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally {
			data.close();
		}
		this.refreshData();

	}

	private class ItemAdapter extends ArrayAdapter<Item> {

		private final Context context;
		private final List<Item> list;

		public ItemAdapter(final Context context, final List<Item> list) {
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
					ItemActivity.this.plusTick(ItemAdapter.this.list.get(
							position).getID());
				}
			});

			final Button minus = (Button) convertView.findViewById(R.id.minus);

			minus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					ItemActivity.this.minusTick(ItemAdapter.this.list.get(
							position).getID());
				}
			});

			final Item i = this.list.get(position);

			if (i != null) {
				final TextView name = (TextView) convertView
						.findViewById(R.id.ItemName);
				name.setText(i.getItemName());

				final TextView ticks = (TextView) convertView
						.findViewById(R.id.ItemTicks);
				ticks.setText(String.valueOf(i.getTicks()));

				if (ItemActivity.this.showtpd) {
					final TextView tpd = (TextView) convertView
							.findViewById(R.id.tpd);

					final NumberFormat nf = NumberFormat.getNumberInstance();
					nf.setMinimumFractionDigits(0);
					nf.setMaximumFractionDigits(2);

					tpd.setText(nf.format(ItemActivity.this.getTicksperDay(
							i.getListID(), i.getID())));
				}

				convertView.setBackgroundColor(i.getColor());
			}

			return convertView;
		}
	}

	private class ItemListAdapter extends ArrayAdapter<ItemList> {

		private final Context context;
		private final List<ItemList> list;

		public ItemListAdapter(final Context context, final List<ItemList> list) {
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
				final TextView name = (TextView) convertView
						.findViewById(R.id.itemlistviewitemname);
				name.setText(il.getListName());
			}

			return convertView;
		}

	}
}
