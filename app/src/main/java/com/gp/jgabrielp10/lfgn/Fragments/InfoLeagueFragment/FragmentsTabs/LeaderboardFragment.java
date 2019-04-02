package com.gp.jgabrielp10.lfgn.Fragments.InfoLeagueFragment.FragmentsTabs;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gp.jgabrielp10.lfgn.Models.Leaderboard;
import com.gp.jgabrielp10.lfgn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment {

    private List<Leaderboard> leaderList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public FirebaseFirestore db;

    public LeaderboardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_leaderboard);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        leaderList = new ArrayList<>();
        mAdapter = new LeaderboardAdapter(leaderList);
        recyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        if (drawable != null)
            dividerItemDecoration.setDrawable(drawable);
        recyclerView.addItemDecoration(dividerItemDecoration);

        db = FirebaseFirestore.getInstance();
        db.collection(getArguments().getString("CollectionId")).document(getArguments().getString("DocumentId")).collection("tournaments").orderBy("DateAdded", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                db.collection(getArguments().getString("CollectionId")).document(getArguments().getString("DocumentId")).collection("leaderboard").document(task.getResult().getDocuments().get(0).getString("Name")).collection("table").orderBy("pts", Query.Direction.DESCENDING).orderBy("dg",Query.Direction.DESCENDING).orderBy("gf",Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        leaderList.removeAll(leaderList);
                        for (QueryDocumentSnapshot doc : value)
                            leaderList.add(doc.toObject(Leaderboard.class));
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        return v;
    }
}
