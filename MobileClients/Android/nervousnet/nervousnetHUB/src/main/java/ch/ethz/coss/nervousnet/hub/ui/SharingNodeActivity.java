package ch.ethz.coss.nervousnet.hub.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NervousnetNode;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NodesArrayAdapter;

public class SharingNodeActivity extends BaseActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private EditText nameEditText;
    private EditText ipEditText;
    private NodesArrayAdapter adapter;
    private ListView list;

    private NervousnetNode[] nodesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_node);

        list = (ListView) findViewById(R.id.lst_Nodes);

        refreshData();

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                vibrate();
                Toast.makeText(SharingNodeActivity.this, "Do you want to remove " + nodesList[position].getName() + "?", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }


    private void vibrate() {
        Vibrator v = (Vibrator) SharingNodeActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(50);
    }

    public void clickedAdd(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New sharing node");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_add_new_server, null);
        builder.setView(dialogView);

        nameEditText = (EditText) dialogView.findViewById(R.id.nameEditText);
        ipEditText = (EditText) dialogView.findViewById(R.id.IPEditText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setSharedPreferences(nameEditText.getText().toString(), ipEditText.getText().toString());
                refreshData();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
        vibrate();
        Toast.makeText(SharingNodeActivity.this, "Opens splash to add a new node", Toast.LENGTH_SHORT).show();
    }

    private void refreshData() {
        nodesList = getNodeList();
        adapter = new NodesArrayAdapter(this, R.layout.node_list_item, nodesList);
        list.setAdapter(adapter);
    }

    private String getSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("sharingNodesJSON", "[]");
        return restoredText;
    }

    private void setSharedPreferences(String name, String IP) {
        try {
            JSONObject object = new JSONObject();
            object.put("name", name);
            object.put("ip", IP);

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String sharedNodes = prefs.getString("sharingNodesJSON", "[]");
            JSONArray jsonArray = new JSONArray(sharedNodes);

            jsonArray.put(object);
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("sharingNodesJSON", jsonArray.toString());
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private NervousnetNode[] getNodeList() {
        NervousnetNode[] nodesList = new NervousnetNode[] {
                new NervousnetNode("LOPLOP", "asdf"),
        };

        try {
            JSONArray jsonArray = new JSONArray(getSharedPreferences());
            nodesList = new NervousnetNode[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                NervousnetNode node = new NervousnetNode(object.getString("name"), object.getString("ip"));
                nodesList[i] = node;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return nodesList;
    }
}
