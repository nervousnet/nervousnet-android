package ch.ethz.coss.nervousnet.hub.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import com.parse.Parse;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NervousnetNode;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NodesArrayAdapter;
import ch.ethz.coss.nervousnet.vm.NervousnetVM;

public class SharingNodeActivity extends BaseActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private EditText nameEditText;
    private EditText ipEditText;
    private NodesArrayAdapter adapter;
    private ListView list;

    private NervousnetNode[] nodesList;
    private static final String LOG_TAG = "SharingNodeActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_node);

        list = (ListView) findViewById(R.id.lst_Nodes);
        final NervousnetVM nn_VM = ((Application) getApplication()).nn_VM;


        refreshData();

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                vibrate();
                Toast.makeText(SharingNodeActivity.this, "Do you want to remove " + nodesList[position].getName() + "?", Toast.LENGTH_SHORT).show();

                return false; //FIXME ask for permission to remove (remove from DB and refresh UI)
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SharingNodeActivity.this);

                final int pos = position;

                //Configure OK Button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Check if the server is connecting and inform user about result
                        String urlstring = "http://207.154.242.197:1337/parse";
                        try {
                            //configure parse and share
                            Parse.initialize(new Parse.Configuration.Builder(getApplication())
                                    .applicationId("nervousnet")
                                    .server(urlstring)
                                    .build()
                            );

                            Log.d(LOG_TAG, "Successfully connected with node " + nodesList[pos].getName() + " at " + urlstring);
                            Toast.makeText(SharingNodeActivity.this, "Starting to share with " + nodesList[pos].getName(), Toast.LENGTH_LONG).show();

                            nn_VM.startSharing();

                        } catch (Exception e) {
                            Toast.makeText(SharingNodeActivity.this, "Unable to connect to " + nodesList[pos].getName(), Toast.LENGTH_LONG).show();
                            Log.d(LOG_TAG, "Failed to connect to sharing server");
                            dialog.cancel();
                        }
                    }
                });

                //show cancel or stop button based on if app currently is sharing
                // TODO this should be improved, the "STOP" button appears if we click on any node but really it means to stop a previously started sharing (that could be with another node)
                if(nn_VM.isSharingActive()) {
                    //Configure Stop Button
                    builder.setNegativeButton("Stop", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            nn_VM.stopSharing();
                            dialog.dismiss();
                        }
                    });

                    builder.setMessage("Stop sharing to any node or (re)start sharing with this node")
                            .setTitle("Share to " + nodesList[pos].getName());
                } else {
                    //Configure Cancel Button
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    builder.setMessage("Do you want to start sharing data to this node?")
                            .setTitle("Share to " + nodesList[pos].getName());
                }


                //Show Dialog
                AlertDialog dialog = builder.create();
                dialog.show();
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
