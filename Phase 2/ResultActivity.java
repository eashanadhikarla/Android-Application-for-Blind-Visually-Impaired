import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;



public class ResultActivity extends AppCompatActivity {
    private ImageView mimageview;
    private TextView Result;
    public String TAG ="*********TAG*********";
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mimageview =(ImageView) findViewById(R.id.RImage);
        Intent intent = getIntent();
        final int selection= intent.getIntExtra("Selection",0);
        if(selection==1){
            final String image_path= intent.getStringExtra("Image");
            Uri fileUri = Uri.parse(image_path);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
                mimageview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            final String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
            bitmap = BitmapFactory.decodeFile(imageFilePath);
            mimageview.setImageBitmap(bitmap);
        }

        //mimageview.setImageURI(fileUri);
        Result =(TextView) findViewById(R.id.RText);
        SparkApi tasks =new SparkApi();
        tasks.execute(bitmap);
    }

    private class SparkApi extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected String doInBackground(Bitmap... pic) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pic[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bbytes = stream.toByteArray();

            byte[] img = Base64.encode(bbytes,0);
            String urlstring ="http://192.168.1.229:8080/get_custom";  //Server may not be running all time
            String resultToDisplay = "";
            InputStream in = null;
            try {

                URL url = new URL(urlstring);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                String str = new String(img);
                wr.writeBytes(str);
                wr.flush();
                wr.close();

                in = new BufferedInputStream(urlConnection.getInputStream());


            } catch (Exception e) {

                e.printStackTrace();

            }
            if(in != null)
                try {
                    resultToDisplay = convertInputStreamToString(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else
                resultToDisplay = "Did not work!";

            return resultToDisplay;

        }
        protected void onProgressUpdate() {

        }
        @Override
        protected void onPostExecute(String result) {
            Result.setText(result);
        }
    }



    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
