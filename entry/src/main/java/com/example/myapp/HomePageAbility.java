package com.example.myapp;

import com.example.myapp.slice.HomePageSlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class HomePageAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(HomePageSlice.class.getName());
    }
}