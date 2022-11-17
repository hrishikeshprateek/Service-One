package com.aigs.serviceone.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

public class BatteryUpdater extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        updateBatteryData(intent,context);
    }

    private void updateBatteryData(Intent intent,Context context) {
        String valueBatt = null,health_batt = null,tech = null,statusLble=null;
        boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);

        if (present) {
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            int healthLbl = -1;

            switch (health) {
                case BatteryManager.BATTERY_HEALTH_COLD:
                    health_batt = "BATTERY_HEALTH_COLD";
                    break;

                case BatteryManager.BATTERY_HEALTH_DEAD:
                    health_batt = "BATTERY_HEALTH_DEAD";
                    break;

                case BatteryManager.BATTERY_HEALTH_GOOD:
                    health_batt = "BATTERY_HEALTH_GOOD";
                    break;

                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    health_batt = "BATTERY_HEALTH_OVER_VOLTAGE";
                    break;

                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    health_batt = "BATTERY_HEALTH_OVERHEAT";
                    break;

                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    health_batt = "BATTERY_HEALTH_UNSPECIFIED_FAILURE";
                    break;

                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                default:
                    break;
            }

            // Calculate Battery Pourcentage ...
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (level != -1 && scale != -1) {
                int batteryPct = (int) ((level / (float) scale) * 100f);
                valueBatt = "Battery Pct : " + batteryPct + " %";
            }

            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            String pluggedLbl = null;

            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    pluggedLbl = "BATTERY PLUGGED WIRELESS";
                    break;

                case BatteryManager.BATTERY_PLUGGED_USB:
                    pluggedLbl = "BATTERY_PLUGGED_USB";
                    break;

                case BatteryManager.BATTERY_PLUGGED_AC:
                    pluggedLbl = "BATTERY_PLUGGED_AC";
                    break;

                default:
                    pluggedLbl = "BATT ERROR";
                    break;
            }

            // display plugged status ...
            pluggedLbl = "Plugged : " + pluggedLbl;

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String statusLbl = "DISCHARGING";

            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    statusLbl = "BATTERY_STATUS_CHARGING";
                    break;

                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    statusLbl = "BATTERY_STATUS_DISCHARGING";
                    break;

                case BatteryManager.BATTERY_STATUS_FULL:
                    statusLbl = "BATTERY_STATUS_FULL";
                    break;

                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    statusLbl ="BATTERY_STATUS_UNKNOWN";
                    break;

                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                default:
                    statusLbl = "DISCHARGING";
                    break;
            }

            //TODO UPDATE STATUS LABEL
            statusLble = "Battery Charging Status : " + statusLbl;


            if (intent.getExtras() != null) {
                String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);

                if (!"".equals(technology)) {
                    //TODO UPDATE TECHNOLOGY
                    tech = "Technology : " + technology;
                }
            }

            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            String tempStat  = null;

            if (temperature > 0) {
                float temp = ((float) temperature) / 10f;
                tempStat = "Temperature : " + temp + "°C";
            }

            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            long capacity = getBatteryCapacity(context);


            Log.e("TEMP STAT",tempStat);
            Log.e("CAPACITY",capacity+"");
            Log.e("VOLTAGE",voltage + " mV");
            Log.e("T",tempStat);
            Log.e("Tech",tech);
            Log.e("stat",statusLble);
            Log.e("PL",pluggedLbl);
            Log.e("VAL",valueBatt);
            Log.e("HEALTH",health_batt);

        } else {
            Log.e("ERROR","NO BATTERY PRESENT");
        }


    }

    public long getBatteryCapacity(Context ctx) {
        BatteryManager mBatteryManager = (BatteryManager) ctx.getSystemService(Context.BATTERY_SERVICE);
        long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        long capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        return (long) (((float) chargeCounter / (float) capacity) * 100f);

    }
}
