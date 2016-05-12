package org.projects.shoppinglist;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import android.support.v7.widget.ShareActionProvider;

public class MainActivity extends AppCompatActivity {
    ArrayAdapter<Product> adapter;
    ListView listView;
    //ArrayList<Product> bag = new ArrayList<Product>();
    Product lastDeletedProduct;
    int lastDeletedPosition;
    Firebase mRef;
    Firebase m2Ref;

    private ShareActionProvider actionProvider;
    private EditText inputText;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //----------------Firebase Adapter--(layout og actionbar)--------------------------------------

    FirebaseListAdapter<Product> fireAdapter;
    public FirebaseListAdapter getMyAdapter() {
        return fireAdapter;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRef = new Firebase("https://mshuskeliste.firebaseio.com/items");
        m2Ref = new Firebase("https://mshuskeliste.firebaseio.com");

        listView = (ListView) findViewById(R.id.list);
         fireAdapter =
                new FirebaseListAdapter<Product>(
                        this,
                        Product.class,
                        android.R.layout.simple_list_item_checked,
                        mRef
                ) {
                @Override
                protected void populateView(View view, Product product, int i) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(product.toString());
                }
                };
        listView.setAdapter(fireAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //----------------spinner------------------------------------------------------------------

        final Spinner howmanyspinner= (Spinner) findViewById(R.id.howmanyspinner);
        ArrayAdapter<CharSequence> adp3=ArrayAdapter.createFromResource(this,
                R.array.howmanyarray, android.R.layout.simple_list_item_1);

        adp3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        howmanyspinner.setAdapter(adp3);
        howmanyspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                String ss = howmanyspinner.getSelectedItem().toString();
                Toast.makeText(getBaseContext(), ss, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        //--------------------addButton---------------------------------------------------------------------------
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText itemTxt = (EditText) findViewById(R.id.itemInput);
                String ss=howmanyspinner.getSelectedItem().toString();
                Integer howMany = Integer.parseInt(ss);
                Product p = new Product(itemTxt.getText().toString(), howMany);
                mRef.push().setValue(p);
                itemTxt.setText("");//fjerner tekst
                howmanyspinner.setSelection(0);//antal sættes til 1
                getMyAdapter().notifyDataSetChanged();
            }
        });


//--------------------------------deletebutton--med snackbar------------------------
//

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        final View parent = findViewById(R.id.layout);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCopy(); //save a copy of the deleted item
                int index = listView.getCheckedItemPosition();
                getMyAdapter().getRef(index).setValue(null);
                getMyAdapter().notifyDataSetChanged(); //notify view

                Snackbar snackbar = Snackbar
                        .make(parent, "Vil du slette "+lastDeletedProduct+"?", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //ny her bag.add(lastDeletedPosition,lastDeletedProduct);
                                mRef.push().setValue(lastDeletedProduct);
                                getMyAdapter().notifyDataSetChanged();
                                Snackbar snackbar = Snackbar.make(parent, lastDeletedProduct+" på listen igen!", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        });

                snackbar.show();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Colorout();
    }

    //gemme en kopi til snacbarens undo
    public Product getItem(int index)
    {
        return (Product) getMyAdapter().getItem(index);
    }
    public void saveCopy()
    {
        lastDeletedPosition = listView.getCheckedItemPosition();
        //lastDeletedProduct= getMyAdapter().getRef(lastDeletedPosition);
        lastDeletedProduct= getItem(lastDeletedPosition);

    }

    //----------------tekst til share-----------------------------------------------
    public String convertListToString()
    {
        String result = "";
        for (int i = 0; i<fireAdapter.getCount();i++)
        {
            Product p = (Product) fireAdapter.getItem(i);
            String sp =p.toString();
            result += p.getQuantity()+" "+p.getName() + "\n";
             Log.d("her er listen", sp);
        }
        return result;
    }

//---------------------------------Menu----------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        //----------settings
            case R.id.action_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(this, SettingsActivity.class);
                //startActivity(intent); //this we can use if we DONT CARE ABOUT RESULT
                //we can use this, if we need to know when the user exists our preference screens
                startActivityForResult(intent, 1);

                return true;
        //---------About
            case R.id.item_about:
                Toast.makeText(this, "Opgave Android Studio, \nMarianne Skolander", Toast.LENGTH_SHORT)
                        .show();
                return true;


            //--------------Share List------------------------------------------------------




            case R.id.share:
            int id = item.getItemId();
            //This is the code to handle our manual way of sharing
            if (id==R.id.share)
            {
                Intent intentshare = new Intent(Intent.ACTION_SEND);
                intentshare.setType("text/plain"); //MIME type
                int totalItems = fireAdapter.getCount();
                SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
                String navn = prefs.getString("name", "");
                String textToShare =navn+"'s huskeliste med "+ totalItems +" forskellige ting:\n" +convertListToString();
                intentshare.putExtra(Intent.EXTRA_TEXT, textToShare);//add the text to t
                intentshare.putExtra(android.content.Intent.EXTRA_SUBJECT, "Huskeliste");//emne
                startActivity(intentshare);
            }
                return true;




        //-------clear list----med Dialogbox--------------------------
            case R.id.item_clear:
                //public void showDialog(View v) {

                MyDialogFragment dialog = new MyDialogFragment() {
                    @Override
                    protected void positiveClick() {
                        //m2Ref.push().setValue(null);
                        m2Ref.removeValue();
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Listen blev slettet", Toast.LENGTH_SHORT);
                        toast.show();
                        getMyAdapter().notifyDataSetChanged();
                    }

                    @Override
                    protected void negativeClick() {
                        //Here we override the method and can now do something
                        Toast toast = Toast.makeText(getApplicationContext(),
                         "negative button clicked", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                };

               dialog.show(getFragmentManager(), "MyFragment");

                return true;
        }

        return false; //we did not handle the event
    }


    //----------------Bundle outstate--------------------------------------------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    @Override
    public void onStart() {


        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://org.projects.shoppinglist/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://org.projects.shoppinglist/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();

        //-------------------------SETTINGS-----------------------------------------------
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1) //exited our preference screen
        {
            Colorout();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void Colorout (){
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String theme = prefs.getString("theme", "");
        String name = prefs.getString("name", "");
        Toast.makeText(
                this,
                name + "har sat baggrundsfarven til/n: "+  theme, Toast.LENGTH_SHORT).show();
        LinearLayout ln = (LinearLayout) this.findViewById(R.id.layout);
        ln.setBackgroundColor(Color.rgb(255, 255, 255));
        if(theme.equals("white")){
            ln.setBackgroundColor(Color.rgb(255,255,255));
        }
        else if(theme.equals("grey")){
            ln.setBackgroundColor(Color.rgb(207,216,220));
        }
        else if(theme.equals("yellow")){
            ln.setBackgroundColor(Color.rgb(255,249,196));
        }
        else if(theme.equals("lightblue")){
            ln.setBackgroundColor(Color.rgb(187,222,251));
        }

    }
    public void setPreferences(View v) {
        //Here we create a new activity and we instruct the Android system to start it
        Intent intent = new Intent(this, SettingsActivity.class);
        //startActivity(intent); //this we can use if we DONT CARE ABOUT RESULT
        //we can use this, if we need to know when the user exists our preference screens
        startActivityForResult(intent, 1);
    }


    public void getPreferences(View v) {
        //We read the shared preferences from the
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        //String gender = prefs.getString("gender", "");
        String theme = prefs.getString("theme", "");
        boolean soundEnabled = prefs.getBoolean("sound", false);

        Toast.makeText(
                this,
                "Email: " + email + "\nTheme: " + theme + "\nSound Enabled: "
                        + soundEnabled, Toast.LENGTH_SHORT).show();
    }


}
