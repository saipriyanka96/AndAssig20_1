package com.example.layout.assig20_1;
//Package objects contain version information about the implementation and specification of a Java package
import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //public keyword is used in the declaration of a class,method or field;public classes,method and fields can be accessed by the members of any class.
//extends is for extending a class. implements is for implementing an interface
//AppCompatActivity is a class from e v7 appcompat library. This is a compatibility library that back ports some features of recent versions of
// Android to older devices.
    //giving permission for 10 contacts
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    TextView textView;
    private String[] names = {"Neha", "Shalu", "Priya", "Vikash"};
    private String[] numbers = {"8791108392", "8791178370", "7894569734", "9865780532"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Variables, methods, and constructors, which are declared protected in a superclass can be accessed only by the subclasses
        // in other package or any class within the package of the protected members class.
        //void is a Java keyword.  Used at method declaration and definition to specify that the method does not return any type,
        // the method returns void.
        //onCreate Called when the activity is first created. This is where you should do all of your normal static set up: create views,
        // bind data to lists, etc. This method also provides you with a Bundle containing the activity's previously frozen state,
        // if there was one.Always followed by onStart().
        //Bundle is most often used for passing data through various Activities.
// This callback is called only when there is a saved instance previously saved using onSaveInstanceState().
// We restore some state in onCreate() while we can optionally restore other state here, possibly usable after onStart() has
// completed.The savedInstanceState Bundle is same as the one used in onCreate().
        // call the super class onCreate to complete the creation of activity like the view hierarchy
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //R means Resource
        //layout means design
        //  main is the xml you have created under res->layout->main.xml
        //  Whenever you want to change your current Look of an Activity or when you move from one Activity to another .
        // The other Activity must have a design to show . So we call this method in onCreate and this is the second statement to set
        // the design
        ///findViewById:A user interface element that displays text to the user.
        textView = (TextView) findViewById(R.id.textView);
        // Checking Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //ContextCompat.checkSelfPermission-Determine whether you have been granted a particular permission.
            //Parameters context	Context
           // permission	String: The name of the permission being checked
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
//Gets whether you should show UI with rationale for requesting a permission.
                //Parameters activity	Activity: The target activity.
                       // permission	String: A permission your app wants to request.
            } else {
                //  requesting the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    //Requests permissions to be granted to this application.
                /**Parameters
                 activity	Activity: The target activity.
                 permissions	String: The requested permissions. Must me non-null and not empty.
                 requestCode	int: Application specific request code to match with a result reported to onRequestPermissionsResult(int, String[], int[]). Should be >= 0.**/

            }
        }
    }

    //Method onRequestPermissionsResult()
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //Callback for the result from requesting permissions.
        /**Parameters
         requestCode	int: The request code passed in requestPermissions(android.app.Activity, String[], int)
         permissions	String: The requested permissions. Never null.
         grantResults	int: The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
**/
         switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, Do the
                    // contacts-related task you need to do.
                    for (int i = 0; i < names.length; i++) {
                        writeContact(names[i], numbers[i]);
                        textView.setText("Contact Saved");
                        //set the text to test view
                    }
                } else {

                    // permission denied, Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    // writeContact()
    private void writeContact(String name, String number) {
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<ContentProviderOperation>();
        //insert raw contact using RawContacts.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        //insert contact display name using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());
        //insert mobile number using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            getApplicationContext().getContentResolver().
                    applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
        } catch (RemoteException e) {
            //Parent exception for all Binder remote-invocation errors
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            //Thrown when an application of a ContentProviderOperation fails due the specified constraints.
            e.printStackTrace();
            //It prints a stack trace for this Throwable object on the error output stream that is the value of the field System.err.
        }
    }

    //Method onDestroy()
    //Called by the system to notify a Service that it is no longer used and is being removed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //Method onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Initialize the contents of the Activity's standard options menu.
        //menu	Menu: The options menu in which you place your items.
        return true;

    }
}
/**
 * contentProviderOperations-Represents a single operation to be performed as part of a batch of operations.
 *  contentProviderOperations are set into an arraylist
 *  newInsert-Create a ContentProviderOperation.Builder suitable for building an insert ContentProviderOperation.
 Parameters
 uri	Uri: The Uri that is the target of the insert.
 ContactsContract.RawContacts.CONTENT_URI-Constants for the raw contacts table, which contains one row of contact information for each person in each synced account.
 Sync adapters and contact management apps are the primary consumers of this API.
 withValue()-A value to insert or update. This value may be overwritten by the corresponding value specified by withValueBackReference(String, int). This can only be used with builders of type insert, update, or assert.
 Parameters
 key	String: the name of this value
 value	Object: the value itself. the type must be acceptable for insertion by put(String, byte[])
 withValueBackReference-Add a ContentValues back reference. A column value from the back references takes precedence over a value specified in withValues(ContentValues). This can only be used with builders of type insert, update, or assert.
 Parameters
 key	String
 previousResult	int
 ContactsContract.CommonDataKinds.Structured-A data kind representing the contact's proper name. You can use all columns defined for ContactsContract.Data as well as the following aliases.
 ContactsContract.RawContacts-Constants for the raw contacts table, which contains one row of contact information for each person in each synced account. Sync adapters and contact management apps are the primary consumers of this API.
 getApplicationContext-Return the context of the single, global Application object of the current process.
 getContentResolver-Return a ContentResolver instance for your application's package.
 apply()-Commit your preferences changes back from this Editor to the SharedPreferences object it is editing.
 ContactsContract.AUTHORITY-The authority for the contacts provider
 */
