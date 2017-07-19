package cargo.floter.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cargo.floter.user.R;
import cargo.floter.user.model.RateCard.RateCardResponse;
import cargo.floter.user.utils.MyAnimationUtils;

public class VehicleAdapter extends Adapter<VehicleAdapter.DataHolder> {
    private LayoutInflater inflater;
    private List<RateCardResponse> listdata;
    int previousPosition = 0;

    class DataHolder extends ViewHolder implements OnClickListener {
        View container;
        TextView dimension;
        TextView load;
        TextView ppkm;
        TextView ppmin;
        TextView subTitle;
        TextView title;
        ImageView vehicleImage;

        public DataHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.merchant);
            this.dimension = (TextView) itemView.findViewById(R.id.dimension);
            this.subTitle = (TextView) itemView.findViewById(R.id.address_merchant);
            this.vehicleImage = (ImageView) itemView.findViewById(R.id.vehicle_image);
            this.load = (TextView) itemView.findViewById(R.id.timestamp);
            this.ppkm = (TextView) itemView.findViewById(R.id.ppkm);
            this.ppmin = (TextView) itemView.findViewById(R.id.ppmin);
            this.load.setOnClickListener(this);
        }

        public void onClick(View v) {
        }
    }

    public VehicleAdapter(List<RateCardResponse> listdata, Context c) {
        this.inflater = LayoutInflater.from(c);
        this.listdata = listdata;
    }

    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataHolder(this.inflater.inflate(R.layout.card_view, parent, false));
    }

    public void onBindViewHolder(DataHolder holder, int position) {
        RateCardResponse item = (RateCardResponse) this.listdata.get(position);
        holder.title.setText(item.getCar_name());
        holder.subTitle.setText("Capacity : " + item.getCapacity() + " KG");
        holder.load.setText("Base Fare : " + item.getBase_fare() + " Rs. for 2 Km");
        holder.ppkm.setText("Price Per Km : " + item.getPrice_per_km() + " Rs. after 2 km");
        holder.ppmin.setText("Load Unload free : " + item.getCharge_after_free_time() + " Rs./min after " + item.getFree_load_unload_time() + " mins");
        holder.dimension.setText("LxBxH : " + item.getLength() + "x" + item.getWidth() + "x" + item.getHeight());
        if (item.getCar_name().equals("Tata Ace")) {
            holder.vehicleImage.setImageResource(R.drawable.truck_tata_ace_active);
        } else if (item.getCar_name().equals("Tata Super Ace")) {
            holder.vehicleImage.setImageResource(R.drawable.truck_super_ace_active);
        } else if (item.getCar_name().equals("Piaggio Ape")) {
            holder.vehicleImage.setImageResource(R.drawable.truck_piaggio_active);
        } else if (item.getCar_name().equals("Bolero Pick Up")) {
            holder.vehicleImage.setImageResource(R.drawable.truck_bolero_active);
        } else if (item.getCar_name().equals("Tata 407")) {
            holder.vehicleImage.setImageResource(R.drawable.truck_tata_active);
        } else if (item.getCar_name().equals("Ashok Leyland Dost")) {
            holder.vehicleImage.setImageResource(R.drawable.truck_ashok_active);
        }
        MyAnimationUtils myAnimationUtils;
        if (position > this.previousPosition) {
            myAnimationUtils = new MyAnimationUtils();
            MyAnimationUtils.animate(holder, true);
        } else {
            myAnimationUtils = new MyAnimationUtils();
            MyAnimationUtils.animate(holder, false);
        }
        this.previousPosition = position;
    }

    public int getItemCount() {
        return this.listdata.size();
    }
}
