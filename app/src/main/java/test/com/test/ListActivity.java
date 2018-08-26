package test.com.test;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import test.com.test.utils.NetworkUtils;
import test.com.test.utils.NewSwipeHelper;

public class ListActivity extends AppCompatActivity implements FetchCountries.LoadCompleteListener,
        NewSwipeHelper.RecyclerItemTouchHelperListener {

    private RecyclerView countriesRecycler;
    private ProgressBar progress;
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<Country> countries;
    private CountriesRecyclerViewAdapter adapter;
    private AlertDialog dialog;

    private BroadcastReceiver networkChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (countries == null) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                fetch();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        countriesRecycler = findViewById(R.id.countries_list);
        progress = findViewById(R.id.progress);
        coordinatorLayout = findViewById(R.id.coordinator);
        setupRecyclerView();
        fetch();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Register receiver to monitor network state change
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChange, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(networkChange);
    }

    /**
     * This method handles data fetching with network checks
     */
    private void fetch() {
        FetchCountries fetch = new FetchCountries(this);
        if (NetworkUtils.isConnected(this))
            fetch.execute();
        else
            showMyDialog();
    }

    /**
     * This method instantiates the recycler and adds the swipe listener to the view
     */
    private void setupRecyclerView() {
        countriesRecycler.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        countriesRecycler.setHasFixedSize(true);
        countriesRecycler.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        adapter = new CountriesRecyclerViewAdapter(new ArrayList<>());
        countriesRecycler.setAdapter(adapter);

        new NewSwipeHelper(this, countriesRecycler, this) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder,
                                                  List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new UnderlayButton(
                        "",
                        R.drawable.bomb,
                        Color.parseColor("#9400D3"),
                        pos -> {
                            ListActivity.this.onSwiped(viewHolder,ItemTouchHelper.LEFT,pos);
                            /*adapter.removeItem(pos);*/
                        }));
            }
        };
    }

    /**
     * This method displays a dialog in case of network unavailability to prompt the user to
     * fix and try again
     */
    private void showMyDialog() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle("Info")
                    .setMessage("Internet not available, Cross check your internet connectivity " +
                            "and try again")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("RETRY", (dialog, which) -> fetch())
                    .setNegativeButton("CLOSE", (dialog, which) -> finish())
                    .setCancelable(false);
            dialog = alertDialog.create();
            dialog.show();
        } catch (Exception e) {
            Log.d(ListActivity.class.getSimpleName(), "Show Dialog: " + e.getMessage());
        }
    }

    @Override
    public void onStartLoading() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadComplete(ArrayList<Country> countries) {
        this.countries = countries;
        adapter.setItems(countries);
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onError() {
        runOnUiThread(() -> {
            Toast.makeText(ListActivity.this, "An Error occurred",
                    Toast.LENGTH_LONG).show();
            progress.setVisibility(View.GONE);
        });
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CountriesRecyclerViewAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String name = countries.get(viewHolder.getAdapterPosition()).name;

            // backup of removed item for undo purpose
            final Country deletedItem = countries.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from country list",
                            Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", view -> {

                // undo is selected, restore the deleted item
                adapter.restoreItem(deletedItem, deletedIndex);
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
