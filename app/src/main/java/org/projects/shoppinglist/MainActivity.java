package org.projects.shoppinglist;

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
import android.widget.ListView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayAdapter<Product> adapter;
    ListView listView;
    //ArrayList<Product> bag = new ArrayList<Product>();
    Product lastDeletedProduct;
    int lastDeletedPosition;
    Firebase mRef;
    Firebase m2Ref;



//gemme en kopi til snackbar med undo-------------------------------------------





    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
        }};
        listView.setAdapter(fireAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
/*
        if (savedInstanceState != null) {
            bag = savedInstanceState.getParcelableArrayList("savedList");
        }
        listView = (ListView) findViewById(R.id.list);
        //here we create a new adapter linking the bag and the
        adapter = new ArrayAdapter<Product>(this,
                android.R.layout.simple_list_item_checked, bag);
        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
*/

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

        //int position = spinner.getSelectedItemPosition();
        //String item = (String) spinner.getSelectedItem();


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


                //bag.add(new Product(itemTxt.getText().toString(), howMany));
                itemTxt.setText("");//fjerner tekst
                howmanyspinner.setSelection(0);//antal sættes til 1
                getMyAdapter().notifyDataSetChanged();
            }
        });


//--------------------------------deletebutton--------------------------
//

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        final View parent = findViewById(R.id.layout);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int selected = listView.getCheckedItemPosition();
                //bag.remove(selected);
                saveCopy(); //save a copy of the deleted item
                //mRef.push().setValue(lastDeletedProduct);
                int index = listView.getCheckedItemPosition();
                getMyAdapter().getRef(index).setValue(null);



                //fireAdapter().getRef(lastDeletedProduct).setValue(null);
                //bag.remove(lastDeletedPosition); //remove item
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
    }
    //gemme en kopi til snacbarens undo
    public Product getItem(int index)
    {
        return (Product) getMyAdapter().getItem(index);

    }
    public void saveCopy()
    {
        lastDeletedPosition = listView.getCheckedItemPosition();
        //lastDeletedProduct = bag.get(lastDeletedPosition);
        //lastDeletedProduct= getMyAdapter().getRef(lastDeletedPosition);
        lastDeletedProduct= getItem(lastDeletedPosition);

    }
//------------------------------Clearbutton - Dialog----------------------------------------------
//action via XML android:onClick="showDialog"
//public void showDialog(View v) {
/*public void showDialog(MenuItem item) {
    //showing our dialog.
    MyDialogFragment dialog = new MyDialogFragment() {
        @Override
        protected void positiveClick() {
            bag.clear();
            getMyAdapter().notifyDataSetChanged();
        }

        @Override
        protected void negativeClick() {
            //Here we override the method and can now do something
            //oast toast = Toast.makeText(getApplicationContext(),
                   // "negative button clicked", Toast.LENGTH_SHORT);
            //toast.show();
        }
    };
    dialog.show(getFragmentManager(), "MyFragment");//Here we show the dialog
}
*/
//---------------------------------Menu----------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                Toast.makeText(this, "Application icon clicked!",
                        Toast.LENGTH_SHORT).show();
                return true; //return true, means we have handled the event
            case R.id.item_about:
                Toast.makeText(this, "Opgave Android Studio, \nMarianne Skolander", Toast.LENGTH_SHORT)
                        .show();
                return true;

            case R.id.item_clear:

                //public void showDialog(View v) {
                //showing our dialog.

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

                //Here we show the dialog
               dialog.show(getFragmentManager(), "MyFragment");
                //Toast.makeText(this, "Opgave Android Studio, Marianne Skolander", Toast.LENGTH_SHORT)
                        //.show();
                return true;

            //dialog.show(getFragmentManager(), "MyFragment");

        }

        return false; //we did not handle the event
    }
//oprindelig
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    /*----------------tilføjet-------------------------*/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //......................outState.putStringArrayList("savedList", bag);
        //outState.putParcelableArrayList("savedList", bag);

        //outState.putInt("selected");
        //outState.putString("savedpos", );
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
    }
}
