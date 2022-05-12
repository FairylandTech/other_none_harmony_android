package com.example.myapp.utils;

import ohos.app.Context;
import ohos.data.distributed.common.*;
import ohos.data.distributed.user.SingleKvStore;

import java.util.ArrayList;

public class DBUtils {
    //具体的实现数据库的初始化
    public static SingleKvStore initOrGetDB(Context context,String storeId){
        //要做的是事情，定义数据库，设计数据库的表里有什么字段
        FieldNode fdid = new FieldNode("id");
        fdid.setNullable(false);
        fdid.setType(FieldValueType.LONG);

        FieldNode fdmoney = new FieldNode("money");
        fdmoney.setType(FieldValueType.DOUBLE);

        FieldNode fdpaymentMatters = new FieldNode("paymentMatters");
        fdpaymentMatters.setType(FieldValueType.STRING);

        FieldNode fddateYear = new FieldNode("dateYear");
        fddateYear.setType(FieldValueType.INTEGER);

        FieldNode fddateMonth = new FieldNode("dateMonth");
        fddateMonth.setType(FieldValueType.INTEGER);

        FieldNode fddateDay = new FieldNode("dateDay");
        fddateDay.setType(FieldValueType.INTEGER);

        FieldNode fdtimestamp = new FieldNode("timestamp");
        fdtimestamp.setType(FieldValueType.LONG);

        FieldNode fdremark = new FieldNode("remark");
        fdremark.setType(FieldValueType.STRING);

        FieldNode fdinOrExp = new FieldNode("inOrExp");
        fdinOrExp.setType(FieldValueType.BOOLEAN);

        //把上面的字段，封装到Schema对象
        Schema schema = new Schema();
        ArrayList<String> indexList = new ArrayList<>();
        indexList.add("$.id"); //schema默认有一个rootFieldNode
        schema.setIndexes(indexList);
        schema.getRootFieldNode().appendChild(fdid);
        schema.getRootFieldNode().appendChild(fdmoney);
        schema.getRootFieldNode().appendChild(fdpaymentMatters);
        schema.getRootFieldNode().appendChild(fddateYear);
        schema.getRootFieldNode().appendChild(fddateMonth);
        schema.getRootFieldNode().appendChild(fddateDay);
        schema.getRootFieldNode().appendChild(fdtimestamp);
        schema.getRootFieldNode().appendChild(fdremark);
        schema.getRootFieldNode().appendChild(fdinOrExp);
        schema.setSchemaMode(SchemaMode.STRICT);

        KvManagerConfig kvManagerConfig = new KvManagerConfig(context);
        KvManager kvManager = KvManagerFactory.getInstance().createKvManager(kvManagerConfig);
        Options options = new Options();
        options.setCreateIfMissing(true)
                .setEncrypt(false)
                .setKvStoreType(KvStoreType.SINGLE_VERSION)
                .setSchema(schema);
        SingleKvStore singleKvStore = kvManager.getKvStore(options, storeId);
        return  singleKvStore;
    }
}
