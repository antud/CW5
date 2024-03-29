package com.example.cw5;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String url = "https://api.textcortex.com/v1/texts/social-media-posts";
    String contextString;
    String keywordsString;
    EditText context;
    EditText keywords;
    TextView story;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = findViewById(R.id.context);
        keywords = findViewById(R.id.keywords);
        story = findViewById(R.id.story);
    }

    void makeHttpRequest(String c, String[] k) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("context", c);
        data.put("max_tokens", 250);
        data.put("mode", "twitter");
        data.put("model", "chat-sophos-1");

        String[] keywords = k;
        data.put("keywords", new JSONArray(keywords));

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray outArr = data.getJSONArray("outputs");
                    JSONObject newRes = outArr.getJSONObject(0);
                    story.setText("Response: " + newRes.getString("text"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", new String(error.networkResponse.data));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + Key.API_KEY);
                return headers;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(req);
    }

    public void onSubmit(View view) throws JSONException {
        contextString = context.getText().toString();
        keywordsString = keywords.getText().toString();

        String[] k = keywordsString.split(",");

        for (int i = 0; i < k.length; i++) {
            k[i] = k[i].trim();
        }
        makeHttpRequest(contextString, k);
    }
}