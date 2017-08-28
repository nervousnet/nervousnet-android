package ch.ethz.coss.nervousnet.hub.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.parse.Parse;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NervousnetNode;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NodesArrayAdapter;
import ch.ethz.coss.nervousnet.vm.NervousnetVM;

public class SharingNodeActivity extends BaseActivity {

    private static final String LOG_TAG = "SharingNodeActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_node);

        final NervousnetVM nn_VM = ((Application) getApplication()).nn_VM;

        ListView list = (ListView) findViewById(R.id.lst_Nodes);

        final NervousnetNode[] nodesList = getNodeList();
        NodesArrayAdapter adapter = new NodesArrayAdapter(this, R.layout.node_list_item, nodesList);
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                vibrate();
                Toast.makeText(SharingNodeActivity.this, "Do you want to remove " + nodesList[position].nodeName + "?", Toast.LENGTH_SHORT).show();
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

                            Log.d(LOG_TAG, "Successfully connected with node " + nodesList[pos].nodeName + " at " + urlstring);
                            Toast.makeText(SharingNodeActivity.this, "Starting to share with " + nodesList[pos].nodeName, Toast.LENGTH_LONG).show();

                            nn_VM.startSharing();
/*
                        } catch (MalformedURLException e) {
                            Toast.makeText(SharingNodeActivity.this, urlstring + " is an invalid IP address", Toast.LENGTH_LONG).show();
                            Log.d(LOG_TAG, "Malformed URL " + urlstring);
                            dialog.cancel(); */
                        } catch (Exception e) {
                            Toast.makeText(SharingNodeActivity.this, "Unable to connect to " + nodesList[pos].nodeName, Toast.LENGTH_LONG).show();
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
                            .setTitle("Share to " + nodesList[pos].nodeName);
                } else {
                    //Configure Cancel Button
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    builder.setMessage("Do you want to start sharing data to this node?")
                            .setTitle("Share to " + nodesList[pos].nodeName);
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
        vibrate();
        Toast.makeText(SharingNodeActivity.this, "Opens splash to add a new node", Toast.LENGTH_SHORT).show();
    }

    private NervousnetNode[] getNodeList() {
        //// TODO: 02/08/2016 get real node names
        NervousnetNode[] data = new NervousnetNode[]{
                new NervousnetNode("ETH Main Node"),
                new NervousnetNode("Alice's Node"),
                new NervousnetNode("Switzerland"),
                new NervousnetNode("nervousnet internal"),
        };

        return data;
    }
}
