package test.com.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CountriesRecyclerViewAdapter extends
        RecyclerView.Adapter<CountriesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Country> countries;
    private Context context;

    CountriesRecyclerViewAdapter(ArrayList<Country> countries) {
        this.countries = countries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.country_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        Country country = countries.get(pos);
        holder.name.setText(String.format(context.getString(R.string.name), country.name));
        holder.currency.setText(String.format(context.getString(R.string.currency),
                country.currencies.get(0).name));
        holder.language.setText(String.format(context.getString(R.string.language),
                country.languages.get(0).name));
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public void setItems (ArrayList<Country> countries) {
        this.countries = countries;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        countries.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Country item, int position) {
        countries.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, currency, language;
        public ConstraintLayout foreGround, backGround;
        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            currency = v.findViewById(R.id.currency);
            language = v.findViewById(R.id.language);
            foreGround = v.findViewById(R.id.foreGround);
            backGround = v.findViewById(R.id.backGround);
        }
    }
}
