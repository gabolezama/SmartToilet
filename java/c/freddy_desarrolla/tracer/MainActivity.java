package c.freddy_desarrolla.tracer;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageButton ibVoiceCom;
    Button btnConsultar, btnGuardar;
    EditText etDato1,etResult,etIPservidor;
    TextView tvComando;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConsultar = (Button)findViewById(R.id.btnConsultar);
        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        etResult = (EditText)findViewById(R.id.etResult) ;
        etDato1 = (EditText)findViewById(R.id.etDato1);
        etIPservidor = (EditText)findViewById(R.id.etIPservidor);
        ibVoiceCom = (ImageButton) findViewById(R.id.ibVoiceCom);
        tvComando = (TextView) findViewById(R.id.tvComando);

        ibVoiceCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceCommand();

            }
        });


        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new ConsultarDatos().execute("http://"+etIPservidor.getText().toString()+"/BDremota_tut34/consultarReserva.php?id="+etDato1.getText().toString());

            }
        });


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new CargarDatos().execute("http://192.168.43.161/BDremota_tut34/registrarReserva.php?dato1=" + etDato1.getText().toString() );

            }
        });



    }

    private void VoiceCommand() {
        /*Intent intent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try{
            startActivityForResult(intent, REQUEST_CODE);
        }catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(), "Error al Inicial Reconocimiento", Toast.LENGTH_LONG).show();
        }*/
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        try {//Start the Activity and wait for the response//
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException a) {   }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*switch(requestCode){
            case REQUEST_CODE:{
                if(requestCode==RESULT_OK && null!=data){
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etIPservidor.setText(result.get(0));
                }
                break;}*/

        switch (requestCode) {

            case REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvComando.setText(result.get(0));

                }
                if(tvComando.getText().toString().equals("activar")){
                    //new CargarDatos().execute("http://192.168.43.161/BDremota_tut34/registrarReserva.php?dato1=RR" );
                    new CargarDatos().execute("http://"+etIPservidor+"/room_light" );
                }
                break;
            }
        }
    }

    private class CargarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getApplicationContext(), "Se almacenaron los datos correctamente", Toast.LENGTH_LONG).show();

        }
    }


    private class ConsultarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {

                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


            JSONArray ja = null;
            try {
                ja = new JSONArray(result);
                //etDato1.setText(ja.getString(1));
                etResult.setText(ja.getString(1));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl = myurl.replace(" ","%20");
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("respuesta", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }


}
