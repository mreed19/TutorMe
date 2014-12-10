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

public class AddActivity extends Activity {

	private EditText name;
	private EditText major;
	private EditText number;
	private EditText email;
	private Spinner subject;
	private Spinner course;
	private Spinner year;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		name = (EditText) findViewById(R.id.name);
		major = (EditText) findViewById(R.id.major);
		number = (EditText) findViewById(R.id.number);
		email = (EditText) findViewById(R.id.email);
		
		name.addTextChangedListener(formDoneTextWatcher);
		major.addTextChangedListener(formDoneTextWatcher);
		number.addTextChangedListener(formDoneTextWatcher);
		email.addTextChangedListener(formDoneTextWatcher);
		
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
		
		year = (Spinner) findViewById(R.id.year);
		ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		year.setAdapter(yearAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
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
		String s2 = major.getText().toString();
		String s3 = number.getText().toString();
		String s4 = email.getText().toString();
		Button button = (Button) findViewById(R.id.add_button);
		
		if (s1.equals("") || s2.equals("") || s3.equals("") || s4.equals(""))
			button.setEnabled(false);
		else
			button.setEnabled(true);
	}
	
	public void addTutor(View view) {
		String message="ADD-TUTOR|root|sae91229122|" + name.getText().toString() + 
				"|" + year.getSelectedItem().toString() + "|" + major.getText().toString()
				+ "|" + number.getText().toString() + "|" + email.getText().toString() + "|"
				+ subject.getSelectedItem().toString() + "|" + course.getSelectedItem().toString() + "\r\n";
		
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
			TextView text = (TextView) findViewById(R.id.add_results);
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
