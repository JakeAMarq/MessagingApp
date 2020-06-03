package edu.uw.tcss450.team4projectclient.ui.weather;

import android.graphics.drawable.Icon;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentFavoriteCardBinding;

public class FavoriteViewAdapter  extends RecyclerView.Adapter<FavoriteViewAdapter.FavoriteViewHolder> {
    //List of type FavoriteData for each card
    public List<FavoriteData> favoritesData;
    /**
     * The ViewModel containing the user's email and JWT
     */
    private static int mMemberid;

    /**
     * required constructor
     * @param favs
     */
    public FavoriteViewAdapter ( List<FavoriteData> favs, int memberid) {
        favoritesData = favs;
        mMemberid = memberid;
//        Log.e("SIZE: ", String.valueOf(favoritesData.size()));
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
        Log.e("onbind viewholder", String.valueOf(position));
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
            Log.e("ADAPTER", "id" + mMemberid);
            //sets the binding
            binding = FragmentFavoriteCardBinding.bind(view);

            binding.buittonMore.setOnClickListener(this::handleMoreOrLess);
            binding.buttonDelete.setOnClickListener(button -> delete());
            binding.buttonCityState.setOnClickListener(button -> updateWeatherZip());
            binding.buittonMore.setVisibility(View.GONE);

        }

        /**
         * makes call to method to delete item from database
         *
         */
        private void delete() {

            FavoriteFragment.deleteLocation(mMemberid, favData.getZipcode());
        }

        private void updateWeatherZip() {
            WeatherFragment.zipcode =  favData.getZipcode();
            //navigate back to weather frag
            Navigation.findNavController(mView).navigate(FavoriteFragmentDirections.actionFavoriteFragmentToNavigationWeather());
        }

        /**
         * Sets all the card items with data
         * @param fav
         */
        public void setFavoriteData(FavoriteData fav) {
            binding.buttonCityState.setText(fav.getCity() + " , " + fav.getState());
            // navigate to weather fragment
            StringBuilder sb = new StringBuilder("Zipcode: " + fav.getZipcode());
            sb.append("\nLatitude: " + fav.getLat());
            sb.append("\nLongitude: " + fav.getLong());
            binding.textPreview.setText(sb.toString());
            Log.e("SETTTTTTADAPTER", "id" + mMemberid);
            favData = fav;

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
