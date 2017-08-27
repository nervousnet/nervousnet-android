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

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NervousnetNode;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NodesArrayAdapter;

public class SharingNodeActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_node);

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
                builder.setMessage("Do you want to start sharing data to this node?")
                        .setTitle("Share to" + nodesList[pos].nodeName);

                
                //Configure OK Button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Check if the server is connecting
                        String urlstring = "www.google.com";
                        try {
                            URL url = new URL(urlstring);

                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            ((Application) getApplication()).nn_VM.startSharing(urlConnection);

                        } catch (MalformedURLException e) {
                            Toast.makeText(SharingNodeActivity.this, urlstring + " is an invalid IP address", Toast.LENGTH_LONG).show();
                            Log.d("SharingNodeActivity", "Malformed URL" + urlstring);
                            dialog.cancel();
                        } catch (Exception e) {
                            Toast.makeText(SharingNodeActivity.this, "Unable to connect to " + nodesList[pos].nodeName, Toast.LENGTH_LONG).show();
                            Log.d("SharingNodeActivity", "Failed to connect to sharing server");
                            dialog.cancel();
                        }
                    }
                });


                //Configure Cancel Button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


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
