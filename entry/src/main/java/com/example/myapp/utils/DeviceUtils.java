package com.example.myapp.utils;

import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;

import java.util.ArrayList;
import java.util.List;

public class DeviceUtils {
    public static String getDeviceId(){
        // 调用DeviceManager的getDeviceList接口，通过FLAG_GET_ONLINE_DEVICE标记获得在线设备列表
        List<DeviceInfo> onlineDevices = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        // 判断组网设备是否为空
        if (onlineDevices.isEmpty()) {
            return null;
        }
        int numDevices = onlineDevices.size();

        ArrayList<String> deviceIds = new ArrayList<>(numDevices);
        ArrayList<String> deviceNames = new ArrayList<>(numDevices);
        onlineDevices.forEach((device) -> {
            deviceIds.add(device.getDeviceId());
            deviceNames.add(device.getDeviceName());
        });
        // 我们这里只有两个设备，所以选择首个设备作为目标设备
        // 开发者也可按照具体场景，通过别的方式进行设备选择
        String selectDeviceId = deviceIds.get(0);
        return selectDeviceId;
    }
}
