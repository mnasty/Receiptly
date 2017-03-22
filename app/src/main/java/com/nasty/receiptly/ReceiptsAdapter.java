package com.nasty.receiptly;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import static com.nasty.receiptly.Utility.logTag;

/**
 * Created by Mick on 3/10/17.
 */

public class ReceiptsAdapter extends CursorAdapter {

    public ReceiptsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewConstants {

        public final TextView dateView;
        public final TextView merchNameView;
        public final TextView moneySpentView;
        public final TextView taxPaidView;
        public final ImageView thumbnailView;

        public ViewConstants(View view) {
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            merchNameView = (TextView) view.findViewById(R.id.list_item_merchant_name_textview);
            moneySpentView = (TextView) view.findViewById(R.id.list_item_money_textview);
            taxPaidView = (TextView) view.findViewById(R.id.list_item_tax_textview);
            thumbnailView = (ImageView) view.findViewById(R.id.list_item_icon);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //store the layout to be inflated
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_receipts, parent, false);
        //implement view holders, reducing unnecessary calls traversing the view hierarchy for layout children id's
        ViewConstants vc = new ViewConstants(view);
        view.setTag(vc);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewConstants vc = (ViewConstants) view.getTag();

        String userReadableDate = cursor.getString(1);
        vc.dateView.setText(userReadableDate);

        String merchantName = cursor.getString(2);
        vc.merchNameView.setText(merchantName);

        String moneySpent = cursor.getString(3);
        vc.moneySpentView.setText(moneySpent);

        String taxPaid = cursor.getString(4);
        vc.taxPaidView.setText(taxPaid);

        // Get file path to the image taken and stored on the SD card from db
        File thumbnailSD = new File(cursor.getString(5));
        Log.d(logTag, "thumbnail sd location: " + thumbnailSD.toString());

        if (thumbnailSD.exists()) {
            // Convert to bitmap
            Bitmap thumbnailBitmap = BitmapFactory.decodeFile(thumbnailSD.toString());
            // Scale down the image for use in the list view
            Bitmap scaledThumbnailBitmap = Bitmap.createScaledBitmap(thumbnailBitmap, 144, 144, true);
            // Grab the image view and set it to the scaled image
            vc.thumbnailView.setImageBitmap(scaledThumbnailBitmap);
        }
    }
}
