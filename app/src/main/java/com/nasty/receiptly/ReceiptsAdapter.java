package com.nasty.receiptly;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        //TODO: Implement an ImageView ViewConstant Placeholder for the actual receipt image
        //public final ImageView receiptView;

        public ViewConstants(View view) {
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            merchNameView = (TextView) view.findViewById(R.id.list_item_merchant_name_textview);
            moneySpentView = (TextView) view.findViewById(R.id.list_item_money_textview);
            taxPaidView = (TextView) view.findViewById(R.id.list_item_tax_textview);
            //TODO: ...
            //receiptView = (ImageView) view.findViewById(R.id.list_item_icon);
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

        //TODO: grab the thumbnail for the specific receipt requested and populate it into the listview here

        String userReadableDate = cursor.getString(1);
        vc.dateView.setText(userReadableDate);

        String merchantName = cursor.getString(2);
        vc.merchNameView.setText(merchantName);

        String moneySpent = cursor.getString(3);
        vc.moneySpentView.setText(moneySpent);

        String taxPaid = cursor.getString(4);
        vc.taxPaidView.setText(taxPaid);
    }
}
