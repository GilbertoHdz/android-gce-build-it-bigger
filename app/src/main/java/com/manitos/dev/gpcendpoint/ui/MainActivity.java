package com.manitos.dev.gpcendpoint.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.manitos.dev.gpcendpoint.R;
import com.manitos.dev.gpcendpoint.api.JokeServiceAsyncTask;
import com.manitos.dev.gpcendpoint.api.network.InternetCheck;
import com.manitos.dev.jokedetail.JokeDetailActivity;

public class MainActivity extends AppCompatActivity implements JokeServiceAsyncTask.JokeValueFetcherListener {

    private ProgressBar _loader;
    private TextView _error_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _loader = (ProgressBar) findViewById(R.id.pg_loader);
        _error_message = (TextView) findViewById(R.id.tv_error_msg);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void tellJoke(View view) {
        Toast.makeText(this, "derp", Toast.LENGTH_SHORT).show();
        checkNetworkConnectionAndGetJokeService();
    }

    private void checkNetworkConnectionAndGetJokeService() {
        _loader.setVisibility(View.VISIBLE);
        _error_message.setVisibility(View.GONE);

        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    // Calling joke api services
                    JokeServiceAsyncTask endpointsAsyncTask = new JokeServiceAsyncTask(MainActivity.this);
                    endpointsAsyncTask.start(MainActivity.this);
                } else {
                    showErrorMessage(R.string.error_network_message);
                }
            }
        });
    }

    @Override
    public void onJokeValue(JokeServiceAsyncTask.Result jokeResult) {
        Log.i("SERVICE_JOKE", jokeResult.jokeValue + "");

        if (jokeResult.jokeValue != null) {
            openJokeDetailScreen(jokeResult.jokeValue);
        } else {
            showErrorMessage(R.string.error_message);
        }
    }

    private void openJokeDetailScreen(String value) {
        _loader.setVisibility(View.GONE);
        _error_message.setVisibility(View.GONE);

        Intent intent = new Intent(MainActivity.this, JokeDetailActivity.class);
        intent.putExtra(JokeDetailActivity.KEY_JOKE_GCP_RESULT, value);
        this.startActivityForResult(intent, JokeDetailActivity.KEY_ACTIVITY_RESULT);
    }

    private void showErrorMessage(int resMsgId) {
        String message = getApplicationContext().getString(resMsgId);
        _error_message.setText(message);
        _error_message.setVisibility(View.VISIBLE);
        _loader.setVisibility(View.GONE);
    }
}
