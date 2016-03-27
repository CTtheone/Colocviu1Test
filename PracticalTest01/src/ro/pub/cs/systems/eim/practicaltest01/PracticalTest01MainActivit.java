package ro.pub.cs.systems.eim.practicaltest01;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PracticalTest01MainActivit extends Activity {

	private final static int SECONDARY_ACTIVITY_REQUEST_CODE = 1;
	protected int serviceStatus = Constants.SERVICE_STOPPED;
	private IntentFilter intentFilter = new IntentFilter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practical_test01_main);

		EditText eText = (EditText) findViewById(R.id.editText1);
		eText.setText(String.valueOf(0));
		eText = (EditText) findViewById(R.id.editText2);
		eText.setText(String.valueOf(0));

		MyListener listener = new MyListener();
		Button btn = (Button) findViewById(R.id.button_left);
		btn.setOnClickListener(listener);
		btn = (Button) findViewById(R.id.button_right);
		btn.setOnClickListener(listener);
		btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(listener);

		for (int index = 0; index < Constants.actionTypes.length; index++) {
			intentFilter.addAction(Constants.actionTypes[index]);
		}

	}

	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("leftCount", ((EditText) findViewById(R.id.editText1)).getText().toString());
		outState.putString("rightCount", ((EditText) findViewById(R.id.editText2)).getText().toString());
	};

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("leftCount")) {
			((EditText) findViewById(R.id.editText1)).setText(savedInstanceState.getString("leftCount"));
		} else {
			((EditText) findViewById(R.id.editText1)).setText(String.valueOf(0));
		}

		if (savedInstanceState.containsKey("rightCount")) {
			((EditText) findViewById(R.id.editText2)).setText(savedInstanceState.getString("rightCount"));
		} else {
			((EditText) findViewById(R.id.editText2)).setText(String.valueOf(0));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.practical_test01_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class MyListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_left:
				EditText eText = (EditText) findViewById(R.id.editText1);
				int value = Integer.parseInt(eText.getText().toString());
				value++;
				eText.setText(Integer.toString(value));
				break;
			case R.id.button_right:
				eText = (EditText) findViewById(R.id.editText2);
				value = Integer.parseInt(eText.getText().toString());
				value++;
				eText.setText(Integer.toString(value));
				break;
			case R.id.button1:
				Intent intent = new Intent(getApplicationContext(), PracticalTest01SecondaryActivity.class);
				int numberOfClicks = Integer.parseInt(((EditText) findViewById(R.id.editText1)).getText().toString())
						+ Integer.parseInt(((EditText) findViewById(R.id.editText2)).getText().toString());
				intent.putExtra("numberOfClicks", numberOfClicks);
				startActivityForResult(intent, SECONDARY_ACTIVITY_REQUEST_CODE);
			}

			int leftNumberOfClicks = Integer.parseInt(((EditText) findViewById(R.id.editText1)).getText().toString());
			int rightNumberOfClicks = Integer.parseInt(((EditText) findViewById(R.id.editText2)).getText().toString());

			if (leftNumberOfClicks + rightNumberOfClicks > Constants.NUMBER_OF_CLICKS_THRESHOLD
					&& serviceStatus == Constants.SERVICE_STOPPED) {
				Intent intent = new Intent(getApplicationContext(), PracticalTest01Service.class);
				intent.putExtra("firstNumber", leftNumberOfClicks);
				intent.putExtra("secondNumber", rightNumberOfClicks);
				getApplicationContext().startService(intent);
				serviceStatus = Constants.SERVICE_STARTED;

			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SECONDARY_ACTIVITY_REQUEST_CODE) {
			Toast.makeText(this, "The activity returned with result " + resultCode, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onDestroy() {
		Intent intent = new Intent(this, PracticalTest01Service.class);
		stopService(intent);
		super.onDestroy();
	}

	private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver();

	private class MessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("[Message]", intent.getStringExtra("message"));
		}
	}

	protected void onResume() {
		super.onResume();
		registerReceiver(messageBroadcastReceiver, intentFilter);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(messageBroadcastReceiver);
		super.onPause();
	}

}
