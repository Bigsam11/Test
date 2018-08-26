package test.com.test;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class handles the data fetching process in the background and also the
 * data parsing process to fetch the required information from the JSON response from the API
 */
public class FetchCountries extends AsyncTask<Void, Void, ArrayList<Country>> {

    private LoadCompleteListener listener;

    FetchCountries(LoadCompleteListener listener) {
        this.listener = listener;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onStartLoading();
    }

    @Override
    protected ArrayList<Country> doInBackground(Void... voids) {
        BufferedReader reader = null;
        try {
            URL myUrl = new URL("https://restcountries.eu/rest/v2/all");
            HttpURLConnection conn = (HttpURLConnection) myUrl
                    .openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            int code = conn.getResponseCode();
            if (code != 200){
                return null;
            }

            InputStream in = conn.getInputStream();
            StringBuilder buffer = new StringBuilder();

            if (in == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            String response = buffer.toString();

            return getCountries(response);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Country> countries) {
        super.onPostExecute(countries);
        if (countries != null && countries.size() > 0) {
            listener.onLoadComplete(countries);
        } else {
            listener.onError();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        listener.onError();
    }

    @Override
    protected void onCancelled(ArrayList<Country> countries) {
        super.onCancelled(countries);
        if (countries != null && countries.size() > 0)
            listener.onLoadComplete(countries);
        else
            listener.onError();
    }

    /**
     * Deserialize method to get the objects from the JSONArray response from the API
     * @param array jsonString to be deserialized
     * @return list of all {@link Country} from the response
     */
    private ArrayList<Country> getCountries(String array) {
        ArrayList<Country> result = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(array);

            for (int i = 0; i < jsonArray.length(); i++) {
                Country country = new Country();
                JSONObject object = jsonArray.getJSONObject(i);

                country.name = object.getString("name");
                country.capital = object.getString("capital");
                country.flag = object.getString("flag");

                JSONArray currencies = object.getJSONArray("currencies");

                for (int j = 0; j < currencies.length(); j++) {
                    Country.Currency currency = country.new Currency();

                    JSONObject obj = currencies.getJSONObject(j);
                    currency.name = obj.getString("name");
                    country.currencies.add(currency);

                }

                JSONArray languages = object.getJSONArray("languages");

                for (int j = 0; j < languages.length(); j++) {
                    JSONObject obj = languages.getJSONObject(j);

                    Country.Language language = country.new Language();
                    language.name = obj.getString("name");
                    country.languages.add(language);
                }
                result.add(country);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onError();
        }
        return result;
    }

    /**
     * Callback to help listen to state changes and responses from the network call on the
     * MainThread
     */
    public interface LoadCompleteListener {

        void onStartLoading();

        void onLoadComplete(ArrayList<Country> countries);

        void onError();
    }
}
