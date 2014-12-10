package com.reed136.tutorme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class ListActivity extends Activity {

	// Use this to determine what type of search to use
	// default 0 = all
	// 1 = subject
	// 2 = course
	private int searchFlag = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		Spinner subjects = (Spinner) findViewById(R.id.subject_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.subject_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		subjects.setEnabled(false);
		subjects.setAdapter(adapter);
		subjects.setVisibility(View.INVISIBLE);
		Spinner courses = (Spinner) findViewById(R.id.course_spinner);
		Integer[] courseNumbers = new Integer[500];
		for (int i = 100; i < 600; i++)
			courseNumbers[i-100] = i;
		ArrayAdapter<Integer> courseAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, courseNumbers);
		courses.setEnabled(false);
		courses.setAdapter(courseAdapter);
		courses.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.list, menu);
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
	
	public void onRadioButtonClicked(View view) {
//		boolean checked = ((RadioButton) view).isChecked();
		Spinner subjects = (Spinner) findViewById(R.id.subject_spinner);
		Spinner courses = (Spinner) findViewById(R.id.course_spinner);
		
		switch(view.getId()) {
			case R.id.search_all:
				searchFlag = 0;
				subjects.setEnabled(false);
				subjects.setVisibility(View.INVISIBLE);
				courses.setEnabled(false);
				courses.setVisibility(View.INVISIBLE);
				break;
			case R.id.search_subject:
				searchFlag = 1;
				subjects.setEnabled(true);
				subjects.setVisibility(View.VISIBLE);
				courses.setEnabled(false);
				courses.setVisibility(View.INVISIBLE);
				break;
			case R.id.search_course:
				searchFlag = 2;
				subjects.setEnabled(true);
				subjects.setVisibility(View.VISIBLE);
				courses.setEnabled(true);
				courses.setVisibility(View.VISIBLE);
				break;
		}
	}
	
	public void runSQLConnection(View view) {
		String message = "";
		Spinner subjects = (Spinner) findViewById(R.id.subject_spinner);
		Spinner courses = (Spinner) findViewById(R.id.course_spinner);
		switch (searchFlag) {
			case 0:
				message = "GET-ALL-TUTORS|root|sae91229122\r\n";
				break;
			case 1:
				message = "GET-TUTORS-BY-SUBJECT|root|sae91229122|" + subjects.getSelectedItem().toString() + "\r\n";
				break;
			case 2:
				message = "GET-TUTORS-BY-COURSE|root|sae91229122|" + subjects.getSelectedItem().toString() + "|" + courses.getSelectedItem().toString() + "\r\n";
				break;
		}
		new MessageSender(this).execute(message);
	}
	
	private class MessageSender extends AsyncTask<String, Void, Socket> {
		private Socket socket;
		private LinkedList<String> answers = new LinkedList<String>();
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
				String line;
				while ((line = in.readLine()) != null) {
					answers.add(line);
				}
				return socket;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Socket socket) {
			String[] fields;
			ScrollView scroller = (ScrollView) findViewById(R.id.scroller);
			scroller.setSmoothScrollingEnabled(true);
			if (socket != null) {
				LinearLayout ll = (LinearLayout) findViewById(R.id.scroll_layout);
				ll.removeAllViews();
				for (String s:answers) {
					if (s.equals("No Results Found.")) {
						System.out.println("Here");
						TextView empty = new TextView(ListActivity.this);
						empty.setText(s);
						empty.setGravity(Gravity.CENTER_HORIZONTAL);
						ll.addView(empty);
					}
					else {
						fields = s.split("\\|");
						TextView name = new TextView(ListActivity.this);
						name.setText(fields[0]);
						ll.addView(name);
						
						TextView year = new TextView(ListActivity.this);
						year.setText(fields[1]);
						ll.addView(year);
						
						TextView major = new TextView(ListActivity.this);
						major.setText(fields[2]);
						ll.addView(major);
						
						TextView number = new TextView(ListActivity.this);
						number.setText(fields[3]);
						ll.addView(number);
						
						TextView email = new TextView(ListActivity.this);
						email.setText(fields[4]);
						ll.addView(email);
						
						TextView subject = new TextView(ListActivity.this);
						subject.setText(fields[5]);
						ll.addView(subject);
						
						TextView course = new TextView(ListActivity.this);
						course.setText(fields[6]);
						ll.addView(course);
						
						TextView blank = new TextView(ListActivity.this);
						blank.setText("X");
						blank.setVisibility(View.INVISIBLE);
						ll.addView(blank);
					}
				}
			} else {
				TextView error = new TextView(ListActivity.this);
				error.setText("Cannot connect to the server.");
				error.setGravity(Gravity.CENTER_HORIZONTAL);
				scroller.addView(error);
			}
		}
	}
}
