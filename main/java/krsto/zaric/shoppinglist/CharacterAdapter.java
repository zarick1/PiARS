package krsto.zaric.shoppinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CharacterAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CharacterModel> characters;

    public CharacterAdapter(Context context) {
        this.context = context;
        characters = new ArrayList<CharacterModel>();
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
            view = inflater.inflate(R.layout.welcome_list_row, null);

            ViewHolder vh = new ViewHolder();
            vh.naslov = view.findViewById(R.id.tvNaslov);
            vh.shared = view.findViewById(R.id.tvShared);
            view.setTag(vh);
        }

        CharacterModel character = (CharacterModel) getItem(i);
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.naslov.setText(character.getText());
        holder.shared.setText(Boolean.toString(character.isBul()));

        return view;
    }

    private class ViewHolder {
        public TextView naslov;
        public TextView shared;
    }
}
