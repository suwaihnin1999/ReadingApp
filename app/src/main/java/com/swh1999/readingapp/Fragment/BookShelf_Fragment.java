package com.swh1999.readingapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swh1999.readingapp.BookShelfAdapter;
import com.swh1999.readingapp.LibraryBookInfo;
import com.swh1999.readingapp.R;
import com.swh1999.readingapp.ReadBookActivity;
import com.swh1999.readingapp.StoryInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookShelf_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookShelf_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BookShelf_Fragment() {
        // Required empty public constructor
    }

    public static BookShelf_Fragment newInstance(String param1, String param2) {
        BookShelf_Fragment fragment = new BookShelf_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_shelf_, container, false);
    }

    RecyclerView mBookShelfRecycler;
    DatabaseReference reffLibrary,reffStory;
    FirebaseAuth fAuth;
    String uid;
    ArrayList<LibraryBookInfo> mLibraryList;
    ArrayList<StoryInfo> mStoryList;
    int temp;
    ProgressBar mProgressbar;
    TextView mTextView;
    BookShelfAdapter bookShelfAdapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        //todo get current user's id
        fAuth=FirebaseAuth.getInstance();
        uid=fAuth.getCurrentUser().getUid();

        mLibraryList=new ArrayList<>();
        mStoryList=new ArrayList<>();



        mBookShelfRecycler.setLayoutManager(new GridLayoutManager(getContext(),3));
        bookShelfAdapter=new BookShelfAdapter(getContext(),mStoryList,
                new BookShelfAdapter.onItemClickListener(){
            public void onItemClick(StoryInfo storyInfo){
                Intent intent=new Intent(getContext(), ReadBookActivity.class);
                intent.putExtra("title",storyInfo.getStoryTitle());
                intent.putExtra("authorId",storyInfo.getUid());
                startActivity(intent);
            }
        });
        mBookShelfRecycler.setAdapter(bookShelfAdapter);

        getLibraryList();


    }

    private void getLibraryList() {
        reffLibrary=FirebaseDatabase.getInstance().getReference("Library").child(uid);
        reffLibrary.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mLibraryList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    LibraryBookInfo bookInfo = snapshot1.getValue(LibraryBookInfo.class);
                    mLibraryList.add(bookInfo);
                }
                Log.e("gg", "Library list=" + mLibraryList.size());

                if (mLibraryList.size() == 0) {
                    mTextView.setVisibility(View.VISIBLE);
                    mProgressbar.setVisibility(View.GONE);
                }
                else{
                    showLibraryList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showLibraryList() {
        FirebaseDatabase.getInstance().getReference("Story").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mStoryList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    StoryInfo sf=snapshot1.getValue(StoryInfo.class);
                    for(LibraryBookInfo LB:mLibraryList){
                        if(LB.getAuthorId().toString().equals(sf.getUid()) &&
                        LB.getTitle().equals(sf.getStoryTitle())){
                            mStoryList.add(sf);
                        }
                    }
                }
                bookShelfAdapter.notifyDataSetChanged();
                mProgressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        mBookShelfRecycler=getView().findViewById(R.id.bookShelf_recycler);
        mProgressbar=getView().findViewById(R.id.bookShelf_progressbar);
        mTextView=getView().findViewById(R.id.bookShelf_infoText);
    }
}