package org.tobinet.tick;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private boolean doubleBackToExitPressedOnce;
	private DataSource data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
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
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		} finally{
			data.close();
		}
		Toast.makeText(this, "Neue Liste angelegt!", Toast.LENGTH_LONG).show();
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
	
	public void InsertItemList(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Neue Liste anlegen");
		final EditText input = new EditText(this);
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
}
