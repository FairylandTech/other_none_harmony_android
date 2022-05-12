package com.example.myapp.slice;

import com.example.myapp.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.bundle.ElementName;

public class LoginAbilitySlice extends AbilitySlice {

    private Button loginButton;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_login);

        loginButton = (Button) findComponentById(ResourceTable.Id_loginButton);
        loginButton.setClickedListener(c -> {
            Intent raIntent = new Intent();
            raIntent.setElement(new ElementName("", "com.example.myapp", "MainAbility"));
            startAbility(raIntent);

        });
    }


    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
