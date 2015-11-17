package com.mica.viva.utility;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactUtils {

	public static final int SEARCHMODE_DISPLAYNAME = 0;
	public static final int SEARCHMODE_PHONENUMBER = 1;
	
	public static boolean checkContactNameExist(String name) {
		return false;
	}

	public static ArrayList<ContactInfo> searchContact(Context context,
			String key, int searchMode) {
		final String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };
		String selection = null;
		String[] selectionArgs = null;
		switch (searchMode) {
		case SEARCHMODE_DISPLAYNAME:
			selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ? ";
			selectionArgs = new String[] { "%"+key+"%" };
			break;
		case SEARCHMODE_PHONENUMBER:
			// made key with form: 012345678 -> 12345678, +8412345678 -> 12345678 
			key = key.replace(" ", "");
			if(key.startsWith("+")){
				key = key.substring(3);
			}
			else if(key.startsWith("0")){
				key = key.substring(1);
			}
			selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ? ";
			selectionArgs = new String[] { "%" + key };
			break;
		default:
			break;
		}
		
		final Cursor contact = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
				selection,
				selectionArgs,
				null); // no sort

		ArrayList<ContactInfo> arr = new ArrayList<ContactInfo>();
		String nameTmp = "";
		String phoneTmp = "";
		if (contact.moveToFirst()) {
			int nameCol = contact
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
			int phoneCol = contact
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

			while (!contact.isAfterLast()) {
				// get name
				nameTmp = contact.getString(nameCol);
				phoneTmp = contact.getString(phoneCol);
				ContactInfo contactInfo = new ContactInfo(nameTmp, phoneTmp);
				arr.add(contactInfo);
				// move to next
				contact.moveToNext();
			}
		}
		contact.close();
		return arr;
	}

}
