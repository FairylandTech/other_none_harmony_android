package com.example.myapp.slice;
import com.example.myapp.ResourceTable;
import com.example.myapp.utils.DBUtils;
import com.example.myapp.utils.DateUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Text;
import ohos.bundle.ElementName;
import ohos.data.distributed.common.*;
import ohos.data.distributed.user.SingleKvStore;
import ohos.utils.zson.ZSONObject;
import java.util.List;

public class HomePageSlice extends AbilitySlice {
    private Text mTopMonthText;
    private Text mTopYearText;
    private Text mCurrentMonthIncomes;
    private Text mCurrentMonthExpenses;
    private Text mCurrentMonthBalance;
    private Button mRecodeOneBtn;
    private Text mTodayBottomText1;
    private Text mTodayText2;
    private Text mTodayBottomText2;
    private Text mWeekBottomText1;
    private Text mWeekText2;
    private Text mWeekBottomText2;
    private Text mMonthBottomText1;
    private Text mMonthText2;
    private Text mMonthBottomText2;
    private Text mYearBottomText1;
    private Text mYearText2;
    private Text mYearBottomText2;

    private SingleKvStore singleKvStore = null;
    private String todayBottomText1Str = "";
    private double currentMonthIncomes, currentMonthExpenses, currentMonthBalance,
            todayText2Str, todayBottomText2Str;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        //初始化日期
        mTopMonthText = (Text) findComponentById(ResourceTable.Id_top_month_text);
        mTopYearText = (Text) findComponentById(ResourceTable.Id_top_year_text);
        //本月收入支出结余
        mCurrentMonthIncomes = (Text) findComponentById(ResourceTable.Id_current_month_incomes);
        mCurrentMonthExpenses = (Text) findComponentById(ResourceTable.Id_current_month_expenses);
        mCurrentMonthBalance = (Text) findComponentById(ResourceTable.Id_current_month_balance);

        //按钮
        mRecodeOneBtn = (Button) findComponentById(ResourceTable.Id_recode_one_btn);
        mRecodeOneBtn.setClickedListener(component -> {
            Intent KeepAccountsAbilityIntent = new Intent();
            KeepAccountsAbilityIntent.setElement(new ElementName("","com.example.myapp","KeepAccountsAbility"));
            startAbility(KeepAccountsAbilityIntent);
        });

        //今天
        mTodayBottomText1 = (Text) findComponentById(ResourceTable.Id_today_bottom_text1);
        mTodayText2 = (Text) findComponentById(ResourceTable.Id_today_text2);
        mTodayBottomText2 = (Text) findComponentById(ResourceTable.Id_today_bottom_text2);

        //本周
        mWeekBottomText1 = (Text) findComponentById(ResourceTable.Id_week_bottom_text1);
        mWeekText2 = (Text) findComponentById(ResourceTable.Id_week_text2);
        mWeekBottomText2 = (Text) findComponentById(ResourceTable.Id_week_bottom_text2);

        //本月
        mMonthBottomText1 = (Text) findComponentById(ResourceTable.Id_month_bottom_text1);
        mMonthText2 = (Text) findComponentById(ResourceTable.Id_month_text2);
        mMonthBottomText2 = (Text) findComponentById(ResourceTable.Id_month_bottom_text2);

        //本年
        mYearBottomText1 = (Text) findComponentById(ResourceTable.Id_year_bottom_text1);
        mYearText2 = (Text) findComponentById(ResourceTable.Id_year_text2);
        mYearBottomText2 = (Text) findComponentById(ResourceTable.Id_year_bottom_text2);

        //获取数据库
        singleKvStore = DBUtils.initOrGetDB(this, "RecordAccouontsDB");
        //数据库里的数据变化监听
        singleKvStore.subscribe(SubscribeType.SUBSCRIBE_TYPE_ALL, new KvStoreObserver() {
            @Override
            public void onChange(ChangeNotification changeNotification) {
                //刷新页面上的数据，同样有一个坑，onChange方法实质上，在一个子线程里执行
                getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        //在这里执行页面ui组件的显示刷新
                        flushUIData();
                    }
                });
            }
        });


        //刷新显示的数据
        flushUIData();
    }

    private void flushUIData() {
        //刷新显示的日期
        mTopMonthText.setText(DateUtils.getCurrentMonth() + "");
        mTopYearText.setText("/" + DateUtils.getCurrentYear());
        mWeekBottomText1.setText(DateUtils.getCurrentWeekStartToEnd());
        mMonthBottomText1.setText(DateUtils.getCurrentMonthStartToEnd());
        mYearBottomText1.setText("1月1日-12月31日");

        //本月收入
        Query query = Query.select();
        query.equalTo("$.dateYear", DateUtils.getCurrentYear())
                .and().equalTo("$.dateMonth", DateUtils.getCurrentMonth())
                .and().greaterThanOrEqualTo("$.dateDay", 1).and().equalTo("$.inOrExp", false);
        List<Entry> entries = singleKvStore.getEntries(query);

        ZSONObject zsonObject = null;
        currentMonthIncomes = 0;
        for (Entry entry : entries) {
            zsonObject = ZSONObject.stringToZSON(entry.getValue().getString());
            currentMonthIncomes += zsonObject.getDouble("money");
        }
        mCurrentMonthIncomes.setText("￥" + currentMonthIncomes + " >");
        mMonthBottomText2.setText(currentMonthIncomes + "");

        //本月支出
        query.reset();
        query.equalTo("$.dateYear", DateUtils.getCurrentYear())
                .and().equalTo("$.dateMonth", DateUtils.getCurrentMonth())
                .and().greaterThanOrEqualTo("$.dateDay", 1).and().equalTo("$.inOrExp", true);
        entries = singleKvStore.getEntries(query);
        currentMonthExpenses = 0;
        for (Entry entry : entries) {
            zsonObject = ZSONObject.stringToZSON(entry.getValue().getString());
            currentMonthExpenses += zsonObject.getDouble("money");
        }
        mCurrentMonthExpenses.setText("￥" + currentMonthExpenses + " >");
        mMonthText2.setText(currentMonthExpenses + "");

        //本月可用
        currentMonthBalance = currentMonthIncomes - currentMonthExpenses;
        mCurrentMonthBalance.setText("￥" + currentMonthBalance + " >");

        //今天最新一笔
        query.reset();
        query.equalTo("$.dateYear", DateUtils.getCurrentYear())
                .and().equalTo("$.dateMonth", DateUtils.getCurrentMonth())
                .and().equalTo("$.dateDay", DateUtils.getcurrentDay()).orderByDesc("$.id");
        entries = singleKvStore.getEntries(query);
        if(entries.size()>0){
            zsonObject = ZSONObject.stringToZSON(entries.get(0).getValue().getString());
            todayBottomText1Str = zsonObject.getString("paymentMatters");
            mTodayBottomText1.setText(todayBottomText1Str);
        }

        //今天支出
        query.reset();
        query.equalTo("$.dateYear", DateUtils.getCurrentYear())
                .and().equalTo("$.dateMonth", DateUtils.getCurrentMonth())
                .and().equalTo("$.dateDay", DateUtils.getcurrentDay()).and().equalTo("$.inOrExp", true);
        entries = singleKvStore.getEntries(query);
        todayText2Str = 0;
        for (Entry entry : entries) {
            zsonObject = ZSONObject.stringToZSON(entry.getValue().getString());
            todayText2Str += zsonObject.getDouble("money");
        }
        mTodayText2.setText(todayText2Str + "");

        //今天收入
        query.reset();
        query.equalTo("$.dateYear", DateUtils.getCurrentYear())
                .and().equalTo("$.dateMonth", DateUtils.getCurrentMonth())
                .and().equalTo("$.dateDay", DateUtils.getcurrentDay()).and().equalTo("$.inOrExp", false);
        entries = singleKvStore.getEntries(query);
        todayBottomText2Str=0;
        for (Entry entry : entries) {
            zsonObject = ZSONObject.stringToZSON(entry.getValue().getString());
            todayBottomText2Str +=  zsonObject.getDouble("money");
        }
        mTodayBottomText2.setText(todayBottomText2Str + "");

        //本周的收入
        query.reset();
        query.equalTo("$.inOrExp", false).and()
                .greaterThanOrEqualTo("$.timestamp", DateUtils.getMondayOfWeektoEpochMilli());
        entries = singleKvStore.getEntries(query);
        double temp = 0;
        for (Entry entry : entries) {
            zsonObject = ZSONObject.stringToZSON(entry.getValue().getString());
            temp += zsonObject.getDouble("money");
        }
        mWeekBottomText2.setText(temp + "");

        //本周支出
        query.reset();
        query.equalTo("$.inOrExp", true).and()
                .greaterThanOrEqualTo("$.timestamp", DateUtils.getMondayOfWeektoEpochMilli());
        entries = singleKvStore.getEntries(query);
        temp = 0;
        for (Entry entry : entries) {
            zsonObject = ZSONObject.stringToZSON(entry.getValue().getString());
            temp += zsonObject.getDouble("money");
        }
        mWeekText2.setText(temp + "");


        //本年的收入
        query.reset();
        query.equalTo("$.inOrExp", false).and().equalTo("$.dateYear", DateUtils.getCurrentYear());
        entries = singleKvStore.getEntries(query);
        temp = 0;
        for (Entry entry : entries) {
            zsonObject = ZSONObject.stringToZSON(entry.getValue().getString());
            temp += zsonObject.getDouble("money");
        }
        mYearBottomText2.setText(temp + "");

        //本年支出
        query.reset();
        query.equalTo("$.inOrExp", true).and().equalTo("$.dateYear", DateUtils.getCurrentYear());
        entries = singleKvStore.getEntries(query);
        temp = 0;
        for (Entry entry : entries) {
            zsonObject = ZSONObject.stringToZSON(entry.getValue().getString());
            temp += zsonObject.getDouble("money");
        }
        mYearText2.setText(temp + "");

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