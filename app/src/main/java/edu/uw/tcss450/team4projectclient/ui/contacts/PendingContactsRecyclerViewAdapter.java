package edu.uw.tcss450.team4projectclient.ui.contacts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Icon;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import edu.uw.tcss450.team4projectclient.R;
import edu.uw.tcss450.team4projectclient.databinding.FragmentContactCardBinding;

public class PendingContactsRecyclerViewAdapter extends RecyclerView.Adapter<PendingContactsRecyclerViewAdapter.PendingContactsViewHolder> {

    //Store all of the blogs to present
    private final List<PendingContactsInfo> mContacts;
    private Context context;

    private Integer key;

    public PendingContactsRecyclerViewAdapter(List<PendingContactsInfo> items) {
        this.mContacts = items;
    }

    @NonNull
    @Override
    public PendingContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new PendingContactsViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_pendcontact_card, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull PendingContactsViewHolder holder, int position) {
        holder.setBlog(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }
    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Blog Recycler View.
     */
    public class PendingContactsViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentContactCardBinding binding;
        public PendingContactsViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactCardBinding.bind(view);
            //binding.buittonMore.setOnClickListener(this::handleMoreOrLess);

            binding.buittonMore.setVisibility(View.GONE);
        }
        /**
         * connects to my backend for deleting a contact
         */
        private void verifyAuthWithServer(Integer pKey) {

            PendingContacts.mAcceptContact.accept_contact(mContacts.get(getAdapterPosition()).getMprimaryKey(),
                    mContacts.get(getAdapterPosition()).getWantedPrimaryA(),
                    mContacts.get(getAdapterPosition()).getWantedPrimaryB()
            );
        }
        //        /**
//         * When the button is clicked in the more state, expand the card to display
//         * the blog preview and switch the icon to the less state. When the button
//         * is clicked in the less state, shrink the card and switch the icon to the
//         * more state.
//         * @param button the button that was clicked
//         */
//        private void handleMoreOrLess(final View button) {
//            if (binding.textPreview.getVisibility() == View.GONE) {
//                binding.textPreview.setVisibility(View.VISIBLE);
//                binding.buittonMore.setImageIcon(
//                        Icon.createWithResource(
//                                mView.getContext(),
//                                R.drawable.ic_less_grey_24dp));
//            } else {
//                binding.textPreview.setVisibility(View.GONE);
//                binding.buittonMore.setImageIcon(
//                        Icon.createWithResource(
//                                mView.getContext(),
//                                R.drawable.ic_more_grey_24dp));
//            }
//        }
        void setBlog(final PendingContactsInfo blog) {
//            Navigation.findNavController(mView).navigate(
//                    ContactsFragmentDirections
//                            .actionNavigationContactsToContactsPostFragment(blog)
            //key = blog.getMprimaryKey();
            // Log.e("pppp", String.valueOf(mContacts.get(getAdapterPosition()).getMprimaryKey()));


            binding.buttonFullPost.setOnClickListener(view -> buildDialog(context).show());
            //TODO add navigation later step
//            );
            binding.textTitle.setText("First Name : " + blog.getFName()  + "\nLast Name: " + blog.getLastName());
            binding.textPubdate.setText(blog.getUserName());
            //Use methods in the HTML class to format the HTML found in the text
            final String fName = blog.getFName();
            final String lName = blog.getFName(); //Html.fromHtml(
//                    blog.getTeaser(),
//                    Html.FROM_HTML_MODE_COMPACT)
//                    .toString().substring(0,53) //just a preview of the teaser
//                    + "...";
            binding.textPreview.setText(lName);
        }

        public AlertDialog.Builder buildDialog(Context c) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("Disclaimer!");
            builder.setMessage("Are you sure you want to accept this contact?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    verifyAuthWithServer(key);

                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            return builder;
        }
    }



}
