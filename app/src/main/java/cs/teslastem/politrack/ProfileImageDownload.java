package cs.teslastem.politrack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by karan on 11/8/2016.
 */

public class ProfileImageDownload extends AsyncTask<String, Void, Bitmap> {

    ImageView bitmap;
    String profileID;

    public ProfileImageDownload(String profileID, ImageView bitmap){
        this.bitmap = bitmap;
        this.profileID = profileID;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = String.format("https://graph.facebook.com/%s/picture?type=large", profileID);

        Bitmap img = null;

        try{
            InputStream stream = new java.net.URL(url).openStream();
            img = BitmapFactory.decodeStream(stream);
        }
        catch(MalformedURLException e){
            Log.e("Invalid URL", e.toString());
        }
        catch (Exception e){
            Log.e("Image Download Error:",e.toString());
        }


        return img;
    }

}
