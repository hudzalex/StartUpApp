package com.biosens.biosens;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class WaitFragment extends Fragment {
    private int seconds=0;
    private boolean running;
    int ResStartValue1=0,ResStartValue2=0,ResStartValue3=0,ResStartValue4=0,ResStartValue5=0,ResStartValue6=0 ;
    String ResearchId="" ;
    boolean toxin1=false;
    boolean toxin2=false;
    boolean toxin3=false;
    boolean toxin4=false;
    boolean toxin5=false;
    boolean toxin6=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ResStartValue1 = bundle.getInt("ResStartValue1", 0);
            ResStartValue2 = bundle.getInt("ResStartValue2", 0);
            ResStartValue3 = bundle.getInt("ResStartValue3", 0);
            ResStartValue4 = bundle.getInt("ResStartValue4", 0);
            ResStartValue5 = bundle.getInt("ResStartValue5", 0);
            ResStartValue6 = bundle.getInt("ResStartValue6", 0);
            ResearchId = bundle.getString("ResearchId", "");
            toxin1=bundle.getBoolean("Toxin1");
            toxin2=bundle.getBoolean("Toxin2");
            toxin3=bundle.getBoolean("Toxin3");
            toxin4=bundle.getBoolean("Toxin4");
            toxin5=bundle.getBoolean("Toxin5");
            toxin6=bundle.getBoolean("Toxin6");

        }
        running=true;
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_wait, container, false);
        // Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        ImageView img = (ImageView)layout.findViewById(R.id.animationContainer);
        img.setBackgroundResource(R.drawable.tube_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
        final Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                if(running){
                if (seconds!=10) {
                    seconds++;
                }
                else{
                    running=false;

                    Bundle bundle = new Bundle();
                    bundle.putString("ResearchId",ResearchId);
                    bundle.putInt("ResStartValue1",ResStartValue1);
                    bundle.putInt("ResStartValue2", ResStartValue2);
                    bundle.putInt("ResStartValue3", ResStartValue3);
                    bundle.putInt("ResStartValue4",ResStartValue4);
                    bundle.putInt("ResStartValue5",ResStartValue5);
                    bundle.putInt("ResStartValue6", ResStartValue6);
                    bundle.putBoolean("Toxin1", toxin1);
                    bundle.putBoolean("Toxin2", toxin2);
                    bundle.putBoolean("Toxin3", toxin3);
                    bundle.putBoolean("Toxin4", toxin4);
                    bundle.putBoolean("Toxin5", toxin5);
                    bundle.putBoolean("Toxin6", toxin6);
                   ResFragment Rfragment =new ResFragment();
                    Rfragment.setArguments(bundle);

                    FragmentTransaction fr=getFragmentManager().beginTransaction();

                    fr.replace(R.id.container, Rfragment);
                    fr.commit();
                }}

                handler.postDelayed(this, 1000);
            }
        });

        return layout;
    }


}
