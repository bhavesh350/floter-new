package cargo.floter.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cargo.floter.user.R;
import cargo.floter.user.model.RestUser;

import java.util.List;

/**
 * Created by Abhishek on 19-04-2017.
 */

public class CatagoryAdapter extends RecyclerView.Adapter<CatagoryAdapter.Holder> {

    private List<RestUser.Response> horizontalList;
    private Context mContext;

    public CatagoryAdapter(Context context, List<RestUser.Response> horizontalList) {
        this.horizontalList = horizontalList;
        mContext = context;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtview;

        public Holder(View view) {
            super(view);
            txtview = (TextView)view.findViewById(R.id.txtview);
            txtview.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {

        }
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_catagory, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.txtview.setText(horizontalList.get(position).getGoodtype_name());

    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}
