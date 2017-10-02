package com.example.nathanmorgenstern.todolistapp;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.view.View.OnLongClickListener;
import android.view.View;
import java.util.ArrayList;
import java.io.*;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.widget.Button;
import android.util.Log;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> items = new ArrayList<>();
    private EditText todoTitle_text;
    private EditText todoDescription_text;

    private String todoTitle;
    private String todoDescription;

    private String FileName = "todoItems.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set the custom toolbar from activity_main
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        loadItemsFromFile(); //load any old to do list items into array list
        updateListView();    //update the listview with either the file items or default view
    }

    /*
        method writeItemsToArray enters the to do title followed by
        the description. If either the title or description are blank
        then no update to the ArrayList will occur
    */
    public void writeItemsToArray(View view) {
        todoTitle_text        = (EditText)findViewById(R.id.inputTaskTitle);
        todoDescription_text  = (EditText)findViewById(R.id.inputTaskDescription);

        todoTitle = todoTitle_text.getText().toString();
        todoDescription = todoDescription_text.getText().toString();

        //Checks to make sure the items are not empty
        if(!todoTitle.equals("") && !todoDescription.equals(""))
        {
            String combine = todoTitle + "\n" + todoDescription;
            items.add(combine);
        }
        updateListView();
        writeItemsToFile();
    }

    /*
    This method is used in conjunction with the add button, as well as when an item is
    clicked for a long time. In the case of add, the ArrayList items is updated with a new task,
    in the case of a long click the item is removed from the list. Both of these changes are written
    to the file
    */
    public void writeItemsToFile() {
        int itemSize = items.size();

        //Checks to make sure the items are not empty
        if (itemSize > 0) {
            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(getFilesDir()+File.separator+FileName));

                for (int i = 0; i < items.size(); i++) {
                    buf.write(items.get(i));
                    buf.newLine();
                }
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    /*
        This method loads any items from the file if the file exists
     */
    public void loadItemsFromFile(){

        Boolean exists = fileExists(this,FileName);

        //Firstly, the file should exist. Verify that it does before proceeding...
        if(exists) {

            try (BufferedReader br = new BufferedReader(new FileReader(getFilesDir() + File.separator + FileName))) {
                int counter = 0;
                String temp = "";
                for (String line; (line = br.readLine()) != null; ) {
                    // process the line.
                    if (counter == 0) {
                        temp += line + "\n";
                        counter++;
                    } else if (counter == 1) {
                        temp += line;
                        items.add(temp);
                        counter = 0;
                        temp = "";
                    }
                }
                // Close the buffered reader file
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }//reference: https://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-using-java#5868528

        }
    }

    /*
        Sets the list view to the items in the ArrayList if the ArrayList is greater then 0,
        otherwise uses a default set of strings to temporarily fill the list. These items have
        no functionality
    */
    public void updateListView() {
        //check to see if the file exists first
        int itemsSize = items.size();
        if(itemsSize > 0)
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.my_text_view, items);

            ListView listView = (ListView) findViewById(R.id.todoListView);
            listView.setAdapter(adapter);

            listView.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    int itemPosition     = position;
                    //Remove the selected item from the array list
                    items.remove(itemPosition);
                    //update the file list
                    writeItemsToFile();
                    //then update the displayed listView
                    updateListView();
                    return true; //I wanted to do void, honest
                }
            });
        }

        else{
            Resources res = getResources();
            String[] arr = res.getStringArray(R.array.defaultArray);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.my_text_view, arr);

            ListView listView = (ListView) findViewById(R.id.todoListView);
            listView.setAdapter(adapter);
            //reset the action listener for the default list case
            listView.setOnItemLongClickListener(null);
        }

    }
    /*
        This method is used to check whether the file exists or not. When the user first downloads
        the app, there shouldn't be a file associated with the app.
    */
    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }//reference: https://stackoverflow.com/questions/8867334/check-if-a-file-exists-before-calling-openfileinput

    /***************************
       FOR DEBUGGING PURPOSES
     **************************/

    public void onDestroy() {
        super.onDestroy(); // always call super ...
        Log.v("destroyed", "onDestroy was called!");
    }


    public void onStart() {
        super.onStart(); // always call super ...
        Log.v("start", "onStart was called!");
    }

}
