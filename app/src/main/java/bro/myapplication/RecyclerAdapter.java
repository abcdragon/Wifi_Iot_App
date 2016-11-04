package bro.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<Item> items;
    int item_layout;
    private static int count = 0;

    public RecyclerAdapter(Context context, List<Item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Item item = items.get(position);
        Drawable drawable = ContextCompat.getDrawable(context, item.getImage());
        final Drawable changeDrawable_1 = ContextCompat.getDrawable(context, R.drawable.rock_close);
        final Drawable changeDrawable_2 = ContextCompat.getDrawable(context, R.drawable.rock_open);
        holder.image.setBackground(drawable);
        holder.title.setText(item.getTitle());
        holder.value.setText(item.getValue());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(item.getTitle().equals("보안모드")){
                if(item.getImage() == R.drawable.rock_close) {
                    holder.image.setBackground(changeDrawable_1);
                    holder.value.setText("실행 중.....");
                }

                else {
                    holder.image.setBackground(changeDrawable_2);
                    holder.value.setText("실행하기");
                }
            }

            count++;

            if(count % 2 == 0){
                MainActivity.MyAsyncTask myAsyncTask = new MainActivity.MyAsyncTask();
                myAsyncTask.execute();
                count = 0;
            }
        }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, value;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            value = (TextView) itemView.findViewById(R.id.value);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
}
