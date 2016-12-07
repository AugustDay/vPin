package uw.virtualpin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import uw.virtualpin.PinListFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Pin} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPinRecyclerViewAdapter extends RecyclerView.Adapter<MyPinRecyclerViewAdapter.ViewHolder> {

    private final List<Pin> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyPinRecyclerViewAdapter(List<Pin> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pin_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mMessageView.setText(mValues.get(position).getMessage());
        holder.mUserNameView.setText(mValues.get(position).getUserName());

        holder.mDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void removeItem(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mValues.size());
    }

/*    @Override
    public void onClick(View v) {
        //Log.d("View: ", v.toString());
        //Toast.makeText(v.getContext(), mTextViewTitle.getText() + " position = " + getPosition(), Toast.LENGTH_SHORT).show();
        if(v.equals(imgViewRemoveIcon)){
            removeAt(getPosition());
        }else if (mItemClickListener != null) {
            mItemClickListener.onItemClick(v, getPosition());
        }
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mUserNameView;
        public final TextView mMessageView;
        public final ImageButton mDeleteView;

        public Pin mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUserNameView = (TextView) view.findViewById(R.id.username);
            mMessageView = (TextView) view.findViewById(R.id.message);
            mDeleteView = (ImageButton)  view.findViewById(R.id.delete);

        }

        @Override
        public String toString() {
            //return super.toString() + " '" + mCreatorView.getText() + "'";
            return "";
        }
    }
}
