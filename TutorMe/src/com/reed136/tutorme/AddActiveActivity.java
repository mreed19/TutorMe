package com.reed136.tutorme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddActiveActivity extends Activity {

	private EditText name;
	private EditText number;
	private EditText location;
	private Spinner subject;
	private Spinner course;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_active);
		name = (EditText) findViewById(R.id.name);
		number = (EditText) findViewById(R.id.number);
		location = (EditText) findViewById(R.id.location);
		
		name.addTextChangedListener(formDoneTextWatcher);
		number.addTextChangedListener(formDoneTextWatcher);
		location.addTextChangedListener(formDoneTextWatcher);
		
		subject = (Spinner) findViewById(R.id.subject);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.subject_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		subject.setAdapter(adapter);
		
		course = (Spinner) findViewById(R.id.course);
		Integer[] courseNumbers = new Integer[500];
		for (int i = 100; i < 600; i++)
			courseNumbers[i-100] = i;
		ArrayAdapter<Integer> courseAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, courseNumbers);
		course.setAdapter(courseAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.add_active, menu);
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
	
	@Override
	protected void onDestroy() {
		stopActive(null);
		super.onDestroy();
	}
	
	private TextWatcher formDoneTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			checkFields();
		}
		
	};
	
	void checkFields() {
		String s1 = name.getText().toString();
		String s2 = number.getText().toString();
		String s3 = location.getText().toString();
		Button button = (Button) findViewById(R.id.start_button);
		
		if (s1.equals("") || s2.equals("") || s3.equals(""))
			button.setEnabled(false);
		else
			button.setEnabled(true);
	}
	
	public void startActive(View view) {
		String message = "ADD-ACTIVE|root|sae91229122|" + name.getText().toString()
				+ "|" + number.getText().toString() + "|" + subject.getSelectedItem().toString()
				+ "|" + course.getSelectedItem().toString() + "|" + location.getText().toString()
				+ "\r\n";
		name.setEnabled(false);
		number.setEnabled(false);
		subject.setEnabled(false);
		course.setEnabled(false);
		location.setEnabled(false);
		Button start = (Button) findViewById(R.id.start_button);
		Button stop = (Button) findViewById(R.id.stop_button);
		start.setEnabled(false);
		stop.setEnabled(true);
		
		new MessageSender(this).execute(message);
	}
	
	public void stopActive(View view) {
		String message = "REMOVE-ACTIVE|root|sae91229122|" + name.getText().toString()
				+ "|" + number.getText().toString() + "|" + subject.getSelectedItem().toString()
				+ "|" + course.getSelectedItem().toString() + "|" + location.getText().toString()
				+ "\r\n";
		name.setEnabled(true);
		number.setEnabled(true);
		subject.setEnabled(true);
		course.setEnabled(true);
		location.setEnabled(true);
		Button start = (Button) findViewById(R.id.start_button);
		Button stop = (Button) findViewById(R.id.stop_button);
		start.setEnabled(true);
		stop.setEnabled(false);
		
		new MessageSender(this).execute(message);
	}
	
	private class MessageSender extends AsyncTask<String, Void, Socket> {

		private Socket socket;
		private String answer;
		private BufferedWriter out;
		private BufferedReader in;
		
		public MessageSender(Context context) {
			socket = null;
			out = null;
			in = null;
		}

		@Override
		protected Socket doInBackground(String... params) {
			try {
				if (socket == null) {
					socket = new Socket("data.cs.purdue.edu", 3300);
					out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				}
				
				out.write(params[0]);
				out.flush();
				answer = in.readLine();
				return socket;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Socket socket) {
			TextView text = (TextView) findViewById(R.id.display_text);
			if (socket != null) {
				text.setText(answer);
				text.setVisibility(View.VISIBLE);
			}
			else {
				text.setText("Cannot connect to the server.");
				text.setVisibility(View.VISIBLE);
			}
		}
		
	}
}
