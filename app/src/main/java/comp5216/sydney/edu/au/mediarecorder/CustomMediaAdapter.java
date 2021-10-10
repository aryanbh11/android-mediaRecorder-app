package comp5216.sydney.edu.au.mediarecorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Custom Media Adapter class for the Grid View
 */
public class CustomMediaAdapter extends BaseAdapter {

    private ArrayList<String> list;
    private final Context context;

    /**
     * Constructor to create an instance of this custom adapter
     * @param localContext
     * @param list list of media items to display on grid view
     */
    public CustomMediaAdapter(Context localContext, ArrayList<String> list) {
        context = localContext;
        this.list = list;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("GridView", "position accessed: " + Integer.toString(position));
        ImageView picturesView;
        Bitmap bitmap = null;
        if (convertView == null) {
            picturesView = new ImageView(context);
            if(list.get(position).contains(".jpg")) {
                // Image Thumbnail
                bitmap = BitmapFactory.decodeFile(list.get(position));
                bitmap = rotateImage(bitmap, 90);
            }
            else if(list.get(position).contains(".mp4")) {
                // Video Thumbnail
                bitmap = ThumbnailUtils.createVideoThumbnail(list.get(position),
                        MediaStore.Video.Thumbnails.MINI_KIND);
            }

            picturesView.setAdjustViewBounds(true);
            picturesView.setImageBitmap(bitmap);
            picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            picturesView.setPadding(5, 5, 5, 5);
        }
        else {
            picturesView = (ImageView)convertView;
        }
        return picturesView;
    }

}
