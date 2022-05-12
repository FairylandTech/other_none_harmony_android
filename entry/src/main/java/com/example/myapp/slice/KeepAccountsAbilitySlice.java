package com.example.myapp.slice;

import com.example.myapp.ResourceTable;
import com.example.myapp.utils.DBUtils;
import com.example.myapp.utils.DateUtils;
import com.example.myapp.utils.DeviceUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityContinuation;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.app.AbilityContext;
import ohos.data.distributed.user.SingleKvStore;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;
import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;

public class KeepAccountsAbilitySlice extends AbilitySlice implements IAbilityContinuation {
    private Text dateText,dateTimeText;
    private Button saveRecordBtn, goBackBtn, deviceMovingBtn;
    private TextField moneyTextfield, paymentMattersTextfield, remarkTextfield;
    private RadioContainer inOrexRadiocontainer;
    private boolean inOrExp = true; //true表示支出，false表示收入
    private SingleKvStore singleKvStore = null;
    private int dateYear, dateMonth, dateDay,hours,mimute,second;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_keep_accounts);

        dateText = (Text) findComponentById(ResourceTable.Id_date_text);
        dateText.setClickedListener(mClickedListener);

        dateTimeText = (Text)findComponentById(ResourceTable.Id_date_time_text);
        dateTimeText.setClickedListener(mClickedListener);

        saveRecordBtn = (Button) findComponentById(ResourceTable.Id_save_record_btn);
        saveRecordBtn.setClickedListener(mClickedListener);

        goBackBtn = (Button) findComponentById(ResourceTable.Id_goback_btn);
        goBackBtn.setClickedListener(mClickedListener);

        deviceMovingBtn = (Button) findComponentById(ResourceTable.Id_device_moving_btn);
        deviceMovingBtn.setClickedListener(mClickedListener);

        moneyTextfield = (TextField) findComponentById(ResourceTable.Id_money_textfield);
        paymentMattersTextfield = (TextField) findComponentById(ResourceTable.Id_payment_matters_textfield);
        remarkTextfield = (TextField) findComponentById(ResourceTable.Id_remark_textfield);

        inOrexRadiocontainer = (RadioContainer) findComponentById(ResourceTable.Id_in_ex_radiocontainer);
        inOrexRadiocontainer.setMarkChangedListener(new RadioContainer.CheckedStateChangedListener() {
            @Override
            public void onCheckedChanged(RadioContainer radioContainer, int i) {
                if(((RadioButton)radioContainer.getComponentAt(0)).isChecked()){
                    inOrExp = true;
                }else{
                    inOrExp = false; //表示radiobutton是第二个收入哪个被选择
                }
            }
        });

        //初始化我们的分布式数据库:创建数据库和获取数据库合二为一的
        singleKvStore = DBUtils.initOrGetDB(this,"RecordAccouontsDB");

    }

    private Component.ClickedListener mClickedListener = new Component.ClickedListener() {
        @Override
        public void onClick(Component component) {
            int componentId = component.getId();
            switch (componentId){
                case ResourceTable.Id_save_record_btn:{
                    try{
                        //保存数据
                        Double money = Double.parseDouble(moneyTextfield.getText().toString());
                        String paymentMatters= paymentMattersTextfield.getText();
                        String remark= remarkTextfield.getText();
                        long timestamp = DateUtils.getTimestamOfDate(
                                dateYear,
                                dateMonth,
                                dateDay,
                                hours,
                                mimute,
                                second);
                        long id = System.currentTimeMillis(); //家里成员使用，不用考虑高并发重复的问题
                        singleKvStore.putString("key"+ id, "{\"id\":"+id+
                                ",\"money\":"+money+
                                ",\"paymentMatters\":\""+paymentMatters+
                                "\",\"dateYear\":"+dateYear+
                                ",\"dateMonth\":"+dateMonth+
                                ",\"dateDay\":"+dateDay+
                                ",\"timestamp\":"+timestamp+
                                ",\"remark\":\""+remark+
                                "\",\"inOrExp\":"+inOrExp+"}");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    moneyTextfield.setText("");
                    paymentMattersTextfield.setText("");
                    dateText.setText("");
                    remarkTextfield.setText("");
                    remarkTextfield.setText("");
                    terminate();//结束当前的abilityslice
                }
                break;
                case ResourceTable.Id_goback_btn:{
                    //回退
                    terminate();//结束当前的abilityslice
                }
                break;
                case ResourceTable.Id_device_moving_btn:{
                    //设备流转
                    String deviceId = DeviceUtils.getDeviceId();
                    if(deviceId != null){
                        continueAbility(deviceId);
                    }
                }
                break;
                case ResourceTable.Id_date_text:{
                    //弹出日期dialog，选择日期
                    showPickDateConfirmTips(KeepAccountsAbilitySlice.this);

                }
                break;
                case ResourceTable.Id_date_time_text:{
                    //弹出时间dialog，选择时间
                    showPickTimeConfirmTips(KeepAccountsAbilitySlice.this);
                }
                break;

            }

        }
    };

    public void showPickDateConfirmTips(AbilityContext context) {
        CommonDialog commonDialog = new CommonDialog(context);
        Component rootView = LayoutScatter.getInstance(context)
                .parse(ResourceTable.Layout_dialog_date_layout_confirm, null, false);
        Text selectedDateText = (Text) findComponentById(ResourceTable.Id_date_text);
        DatePicker datePicker = (DatePicker) rootView.findComponentById(ResourceTable.Id_date_pick);
        Text confirm = (Text) rootView.findComponentById(ResourceTable.Id_dialog_confirm);
        confirm.setClickedListener(c -> {
            dateYear = datePicker.getYear();
            dateMonth = datePicker.getMonth();
            dateDay = datePicker.getDayOfMonth();
            selectedDateText.setText(dateYear + "年" + dateMonth + "月" + dateDay + "日");
            commonDialog.remove();
        });
        commonDialog.setSize(MATCH_PARENT, MATCH_CONTENT);
        commonDialog.setAlignment(LayoutAlignment.CENTER);
        commonDialog.setTransparent(true);
        commonDialog.setCornerRadius(15);
        commonDialog.setContentCustomComponent(rootView);
        commonDialog.show();
    }

    public void showPickTimeConfirmTips(AbilityContext context) {
        CommonDialog commonDialog = new CommonDialog(context);
        Component rootView = LayoutScatter.getInstance(context)
                .parse(ResourceTable.Layout_dialog_time_layout_confirm, null, false);
        Text selectedTimeText = (Text) findComponentById(ResourceTable.Id_date_time_text);
        TimePicker timePicker = (TimePicker) rootView.findComponentById(ResourceTable.Id_time_pick);
        Text confirm = (Text) rootView.findComponentById(ResourceTable.Id_dialog_confirm);
        confirm.setClickedListener(c -> {
            hours = timePicker.getHour();
            mimute = timePicker.getMinute();
            second = timePicker.getSecond();
            selectedTimeText.setText(hours+":"+mimute+":"+second);
            commonDialog.remove();
        });
        commonDialog.setSize(MATCH_PARENT, MATCH_CONTENT);
        commonDialog.setAlignment(LayoutAlignment.CENTER);
        commonDialog.setTransparent(true);
        commonDialog.setCornerRadius(15);
        commonDialog.setContentCustomComponent(rootView);
        commonDialog.show();
    }

    @Override
    public boolean onStartContinuation() {
        return true;
    }

    @Override
    public boolean onSaveData(IntentParams intentParams) {
        intentParams.setParam("money", moneyTextfield.getText());
        intentParams.setParam("paymentMatters", paymentMattersTextfield.getText());
        intentParams.setParam("date", dateText.getText());
        intentParams.setParam("timestamp",dateTimeText.getText());
        intentParams.setParam("remark", remarkTextfield.getText());
        intentParams.setParam("inOrExp", inOrExp);
        return true;
    }

    @Override
    public boolean onRestoreData(IntentParams intentParams) {
        //这里内部嵌入了一个线程，要更新UI主线程中的组件上的值，要先拿到UI线程，然后投递任务Runnable过去
        //在任务Runnable中，向UI组件写值
        getUITaskDispatcher().asyncDispatch(new Runnable() {
            @Override
            public void run() {
                moneyTextfield.setText(intentParams.getParam("money").toString());
                paymentMattersTextfield.setText(intentParams.getParam("paymentMatters").toString());
                dateText.setText(intentParams.getParam("date").toString());
                dateTimeText.setText(intentParams.getParam("timestamp").toString());
                remarkTextfield.setText(intentParams.getParam("remark").toString());
                Boolean inOrExp = (Boolean) intentParams.getParam("inOrExp");
                if (inOrExp) {
                    ((RadioButton)inOrexRadiocontainer.getComponentAt(0)).setChecked(true);
                } else {
                    ((RadioButton)inOrexRadiocontainer.getComponentAt(1)).setChecked(true);
                }
            }
        });
        return true;
    }

    @Override
    public void onCompleteContinuation(int i) {
        terminate();
    }
}
