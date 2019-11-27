package com.example.chargingstation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.finalproject.R;

public class AboutFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){

        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}
