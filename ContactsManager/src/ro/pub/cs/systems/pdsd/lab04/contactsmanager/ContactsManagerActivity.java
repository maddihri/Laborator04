package ro.pub.cs.systems.pdsd.lab04.contactsmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactsManagerActivity extends Activity {
	final public static int CONTACTS_MANAGER_REQUEST_CODE = 1234;
	
	class BasicDetailsFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater layoutInflater,
				ViewGroup container, Bundle state) {
			return layoutInflater.inflate(R.layout.fragment_basic_details,
					container, false);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			Intent intent = getIntent();
			EditText phoneEditText = (EditText) getActivity().findViewById(R.id.phone);
			if (intent != null) {
				  String phone = intent.getStringExtra("ro.pub.cs.systems.pdsd.lab04.contactsmanager.PHONE_NUMBER_KEY");
				  if (phone != null) {
				    phoneEditText.setText(phone);
				  } else {
				    Activity activity = getActivity();
				    Toast.makeText(activity, activity.getResources().getString(R.string.phone_error), Toast.LENGTH_LONG).show();
				  }
				} 
			
			Button showHide = (Button) getActivity().findViewById(R.id.show_hide);
			Button save = (Button) getActivity().findViewById(R.id.save);
			Button cancel = (Button) getActivity().findViewById(R.id.cancel);
			MyListener listener = new MyListener();
			showHide.setOnClickListener(listener);
			save.setOnClickListener(listener);
			cancel.setOnClickListener(listener);
		}
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			switch(requestCode) {
			  case CONTACTS_MANAGER_REQUEST_CODE:
				    setResult(resultCode, new Intent());
				    finish();
				    break;
				  }
		}

		class MyListener implements View.OnClickListener {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				switch(v.getId()) {
					case R.id.show_hide:
						FragmentManager fragmentManager = getActivity().getFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
						AdditionalDetailsFragment additionalDetailsFragment = (AdditionalDetailsFragment) fragmentManager
								.findFragmentById(R.id.frame_down);
						if (additionalDetailsFragment == null) {
							fragmentTransaction.add(R.id.frame_down,
									new AdditionalDetailsFragment());
							((Button) v).setText(R.string.hide_additional_fields);
							fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
						} else {
							fragmentTransaction.remove(additionalDetailsFragment);
							((Button) v).setText(getActivity().getResources().getString(R.string.show_additional_fields));
							fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_EXIT_MASK);
						}
						fragmentTransaction.commit();
						break;
					case R.id.save:
						Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
						intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
						String name = ((EditText) getActivity().findViewById(R.id.name)).getText().toString();
						String phone = ((EditText) getActivity().findViewById(R.id.phone)).getText().toString();
						String email = ((EditText) getActivity().findViewById(R.id.email)).getText().toString();
						String address = ((EditText) getActivity().findViewById(R.id.address)).getText().toString();
						
						if (name != null) {
						  intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
						}
						if (phone != null) {
						  intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
						}
						if (email != null) {
						  intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
						}
						if (address != null) {
						  intent.putExtra(ContactsContract.Intents.Insert.POSTAL, address);
						}
						
						if (((Button) getActivity().findViewById(R.id.show_hide)).getText().toString().equals(R.string.hide_additional_fields)) {
							String jobTitle = ((EditText) getActivity().findViewById(R.id.jobTitle)).getText().toString();
							String company = ((EditText) getActivity().findViewById(R.id.company)).getText().toString();
							String website = ((EditText) getActivity().findViewById(R.id.website)).getText().toString();
							String im = ((EditText) getActivity().findViewById(R.id.im)).getText().toString();
							if (jobTitle != null) {
							  intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, jobTitle);
							}
							if (company != null) {
							  intent.putExtra(ContactsContract.Intents.Insert.COMPANY, company);
							}
							ArrayList<ContentValues> contactData = new ArrayList<ContentValues>();
							if (website != null) {
							  ContentValues websiteRow = new ContentValues();
							  websiteRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
							  websiteRow.put(ContactsContract.CommonDataKinds.Website.URL, website);
							  contactData.add(websiteRow);
							}
							if (im != null) {
							  ContentValues imRow = new ContentValues();
							  imRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);
							  imRow.put(ContactsContract.CommonDataKinds.Im.DATA, im);
							  contactData.add(imRow);
							}
							intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, contactData);
						}
						getActivity().startActivityForResult(intent, CONTACTS_MANAGER_REQUEST_CODE);
						break;
					case R.id.cancel:
						getActivity().setResult(Activity.RESULT_CANCELED, new Intent());
						break;
				}
			}

		}
	
	}
	class AdditionalDetailsFragment extends Fragment {

			@Override
			public View onCreateView(LayoutInflater layoutInflater,
					ViewGroup container, Bundle state) {
				return layoutInflater.inflate(
						R.layout.fragment_additional_details, container, false);
			}
		}

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_manager);
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.add(R.id.frame_up, new BasicDetailsFragment());
		//fragmentTransaction.add(R.id.frame_down, new AdditionalDetailsFragment());
		fragmentTransaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts_manager, menu);
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
}
