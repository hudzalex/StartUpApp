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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                    FragmentTransaction fr=getFragmentManager().beginTransaction();
                   ResFragment refrag=new ResFragment();
                    fr.replace(R.id.container, refrag);
                    fr.commit();
                }}

                handler.postDelayed(this, 1000);
            }
        });

        return layout;
    }


}
