package krsto.zaric.shoppinglist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CharacterTaskAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CharacterModel> characters;

    private DbHelper dbHelper;

    public CharacterTaskAdapter(Context context) {
        this.context = context;
        characters = new ArrayList<CharacterModel>();
        dbHelper = new DbHelper(context);
    }

    public void addElement(CharacterModel element) {
        characters.add(element);
        notifyDataSetChanged();
    }

    public void removeElement(CharacterModel element) {
        characters.remove(element);
        notifyDataSetChanged();
    }

    public void update (CharacterModel[] lists) {
        characters.clear();
        if(lists != null) {
            for(CharacterModel list : lists) {
                characters.add(list);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return characters.size();
    }

    @Override
    public Object getItem(int i) {
        if(i >= 0) {
            return characters.get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.show_list_row, null);

            ViewHolder vh = new ViewHolder();
            vh.naslov = view.findViewById(R.id.tvZadatak);
            vh.shared = view.findViewById(R.id.cbShared);
            view.setTag(vh);
        }

        CharacterModel character = (CharacterModel) getItem(i);
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.shared.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    holder.naslov.setPaintFlags(holder.naslov.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    character.setBul(true);
                    dbHelper.updateTask(character.getTaskId(), Boolean.toString(true));
                }else {
                    holder.naslov.setPaintFlags(holder.naslov.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    character.setBul(false);
                    dbHelper.updateTask(character.getTaskId(), Boolean.toString(false));
                }
                notifyDataSetChanged();
            }
        });

        holder.naslov.setText(character.getText());
        holder.shared.setChecked(character.isBul());

        if(holder.shared.isChecked()){
            holder.naslov.setPaintFlags(holder.naslov.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        return view;
    }

    private class ViewHolder {
        public TextView naslov;
        public CheckBox shared;
    }
}
