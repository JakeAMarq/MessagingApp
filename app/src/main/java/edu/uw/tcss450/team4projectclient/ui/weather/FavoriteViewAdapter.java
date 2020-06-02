package edu.uw.tcss450.team4projectclient.ui.weather;

import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentFavoriteCardBinding;
import edu.uw.tcss450.team4projectclient.ui.auth.signin.SignInFragment;

public class FavoriteViewAdapter  extends RecyclerView.Adapter<FavoriteViewAdapter.FavoriteViewHolder> {
    //List of type FavoriteData for each card
    public List<FavoriteData> favoritesData;
    /**
     * required constructor
     * @param favs
     */
    public FavoriteViewAdapter ( List<FavoriteData> favs) {
        favoritesData = favs;
    }
    /**
     * Gets called when each card gets created.
     * @param parent
     * @param viewType
     */
    @NonNull
    @Override
    public FavoriteViewAdapter.FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind the holder to fragment_favorite_card
        return new FavoriteViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_favorite_card, parent, false));
    }
    /**
     *calls and sets all data for each card at given position
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.setFavoriteData(favoritesData.get(position));
    }
    /**
     * returns size of the card set
     * @return size
     */
    @Override
    public int getItemCount() {
        return favoritesData.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        // instance of the view
        public final View mView;
        // instance of the fav cardbinding xml
        public FragmentFavoriteCardBinding binding;
        // holds onto date
        private FavoriteData favData;
        /**
         * contructor to set up fields
         * @param view
         */
        public FavoriteViewHolder(View view) {
            super(view);
            mView = view;
            //sets the binding
            binding = FragmentFavoriteCardBinding.bind(view);
            binding.buittonMore.setOnClickListener(this::handleMoreOrLess);
            binding.buttonDelete.setOnClickListener(button -> delete());

        }

        /**
         * makes call to method to delete item from database
         *
         */
        private void delete() {
            FavoriteFragment.deleteLocation(favData.getZipcode());
        }

        /**
         * Sets all the card items with data
         * @param fav
         */
        public void setFavoriteData(FavoriteData fav) {
            binding.buttonCityState.setText(fav.getCity() + " , " + fav.getState());
            StringBuilder sb = new StringBuilder("Zipcode: " + fav.getZipcode());
            sb.append("\nLatitude: " + fav.getLat());
            sb.append("\nLongitude: " + fav.getLong());
            binding.textPreview.setText(sb.toString());


        }
        /**
         * When the button is clicked in the more state, expand the card to display
         * the blog preview and switch the icon to the less state. When the button
         * is clicked in the less state, shrink the card and switch the icon to the
         * more state.
         * @param button the button that was clicked
         */
        private void handleMoreOrLess(final View button) {
            if (binding.textPreview.getVisibility() == View.GONE) {
                binding.textPreview.setVisibility(View.VISIBLE);
                binding.buittonMore.setImageIcon(
                        Icon.createWithResource(
                                mView.getContext(),
                                R.drawable.ic_less_grey_24dp));
            } else {
                binding.textPreview.setVisibility(View.GONE);
                binding.buittonMore.setImageIcon(
                        Icon.createWithResource(
                                mView.getContext(),
                                R.drawable.ic_more_grey_24dp));
            }
        }

    }
}
