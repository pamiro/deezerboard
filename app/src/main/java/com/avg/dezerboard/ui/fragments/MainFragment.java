package com.avg.dezerboard.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.avg.dezerboard.DezerApp;
import com.avg.dezerboard.events.Events;
import com.avg.dezerboard.events.EventsFragment;
import com.avg.dezerboard.events.EventsReceiver;
import com.avg.dezerboard.ui.adapters.GridAdapter;

import pm.me.deezerboard.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements EventsFragment {

    private static final String TAG = MainFragment.class.getName();
    private static final String ARG_SECTION_NUMBER = "section_number";

    private OnFragmentInteractionListener mListener;
    public GridAdapter adapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int sectionNum = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate resource .xml
        final Context context = this.getActivity();
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // setup the gridview to be just two columns and 6 cells

        final RecyclerView grid = (RecyclerView) rootView.findViewById(R.id.grid);
        grid.setHasFixedSize(true);
        adapter = new GridAdapter(getActivity());
        grid.setAdapter(adapter);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                grid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width  = grid.getMeasuredWidth();
                int height = grid.getMeasuredHeight();

                // caclualte cell dimensions
                int cellWidth = width / 2;
                int cellHeight = height / 3;

                adapter.setCellHeight(cellWidth, cellHeight);
            }
        });

        final int numColumns = 2;
        GridLayoutManager manager = new GridLayoutManager(this.getActivity(), numColumns);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        grid.setLayoutManager(manager);

        rootView.invalidate();

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        // unregister events
        DezerApp.getLocalBrdcstMgr().unregisterReceiver(EventsReceiver.getInstance());
    }

    @Override
    public void onResume() {
        super.onResume();
        // register all events
        EventsReceiver.getInstance().setActivity(this.getActivity());
        EventsReceiver.getInstance().setFragment(this);
        DezerApp.getLocalBrdcstMgr().registerReceiver(EventsReceiver.getInstance(), new IntentFilter(Events.REFRESH_SCREEN));
        DezerApp.getLocalBrdcstMgr().registerReceiver(EventsReceiver.getInstance(), new IntentFilter(Events.SHOW_BLOCKED_APPS));
        DezerApp.getLocalBrdcstMgr().registerReceiver(EventsReceiver.getInstance(), new IntentFilter(Events.EVENT_SHOW_VISUALIZER));
        DezerApp.getLocalBrdcstMgr().registerReceiver(EventsReceiver.getInstance(), new IntentFilter(Events.EVENT_SEARCH_TRACKS));

        DezerApp.sendLocalEvent(Events.REFRESH_SCREEN);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    /**
     *
     * @param state
     */
    public void eventRefreshScreen(int state) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() :" + data.toString());
    }
}
