package com.example.myapp.slice;

import com.example.myapp.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Button;
import ohos.agp.components.Component;

public class MainAbilitySlice extends AbilitySlice {

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        // 加载XML布局
        super.setUIContent(ResourceTable.Layout_ability_login);
        Button button = (Button) findComponentById(ResourceTable.Id_loginButton);
        if (button != null) {
            // 为按钮控件设置点击事件
            button.setClickedListener(component -> {
                Intent intent1 = new Intent();
                Operation operation = new Intent.OperationBuilder()
                        .withDeviceId("")
                        .withBundleName("com.example.myapp")
                        .withAbilityName("com.example.myapp.HomePageAbility")
                        .build();
                intent1.setOperation(operation);
                startAbility(intent1);
            });
        }
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