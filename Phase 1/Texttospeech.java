import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.OkHttpClient;

public class Texttospeech extends AppCompatActivity {
    private TextToSpeech t1;
    private ImageView mimageview;
    private TextView annotations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texttospeech);
        mimageview=(ImageView)findViewById(R.id.image);
        String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
        mimageview.setImageBitmap(bitmap);
        Annotations(bitmap);

    }
    public void Annotations(Bitmap pic){
        String TesttoSpeech = "You have";
        annotations =(TextView)findViewById(R.id.anno);
        final ClarifaiClient client = new ClarifaiBuilder("Ofr6hOnpopDamQ5fWhmargWwhJLxqFFIN5RipraE", "CzEnwp18RhGqHx_tYMq-whw_36gD1fZOqBcD8V-x")
                .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
                .buildSync(); // or use .build() to get a Future<ClarifaiClient>
        client.getToken();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ClarifaiResponse response = client.getDefaultModels().generalModel().predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray))).executeSync();
        List<ClarifaiOutput<Concept>> predictions = (List<ClarifaiOutput<Concept>>) response.get();
        List<Concept> data = predictions.get(0).data();


        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });


        for(int i=0;i<5;i++){                                            //Top 5 Annotations
            TesttoSpeech=TesttoSpeech+" "+data.get(i).name().toString();
        }

        annotations.setText(TesttoSpeech);                                      //Display Annotations in TextView

        Toast.makeText(getApplicationContext(), TesttoSpeech, Toast.LENGTH_SHORT).show();
        t1.speak(TesttoSpeech, TextToSpeech.QUEUE_FLUSH, null);
    }
}
