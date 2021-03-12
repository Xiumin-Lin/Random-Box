package fr.iut.random_box;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class BoxInfoItemAdapter extends BaseAdapter {
    private final ArrayList<Map.Entry<String,String>> infos;
    private final LayoutInflater inflater;
    public BoxInfoItemAdapter(Context context, Map<String, String> items){
        this.infos = new ArrayList<>();
        infos.addAll(items.entrySet());
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Map.Entry<String,String> getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_adapter, null);

        Map.Entry<String, String> item = getItem(position);
        Log.d("rb", position + " Key = " + item.getKey() + " & Value = " + item.getValue());
        ((TextView) convertView.findViewById(R.id.txtItemKey)).setText(item.getKey());
        ((TextView) convertView.findViewById(R.id.txtItemValue)).setText(item.getValue());

        return convertView;
    }
}
