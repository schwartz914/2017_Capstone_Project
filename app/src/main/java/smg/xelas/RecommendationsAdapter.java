package smg.xelas;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ahmad-PC on 14/10/2017.
 */

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.RecommendationViewHolder> {

    public static class RecommendationViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView title;
        TextView date;
        ImageView photo;

        RecommendationViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_recommedations);
            title = (TextView)itemView.findViewById(R.id.person_name);
            date = (TextView)itemView.findViewById(R.id.person_age);
            photo = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    List<Recommendation> recommendations;

    RecommendationsAdapter(List<Recommendation> recommendations){
        this.recommendations = recommendations;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecommendationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        RecommendationViewHolder pvh = new RecommendationViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RecommendationViewHolder recommendationViewHolder, int i) {
        recommendationViewHolder.title.setText(recommendations.get(i).Title);
        recommendationViewHolder.date.setText(recommendations.get(i).Date);
        recommendationViewHolder.photo.setImageResource(recommendations.get(i).photoId);
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }
}
