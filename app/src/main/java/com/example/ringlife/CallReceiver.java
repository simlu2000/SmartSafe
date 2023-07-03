package com.example.ringlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUGGGGGGGG", "onReceive1");
        final Context finalContext = context;
        MyPhoneStateListener phoneListener = new MyPhoneStateListener(finalContext);
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        Log.d("DEBUGGGGGGGG", "onReceive2");
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        private boolean isPhoneCalling = false;
        private Context context;

        public MyPhoneStateListener(Context context) {
            this.context = context;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // telefono in suoneria (chiamata in arrivo)
                Log.d("DEBUGGGGGGGG", "Chiamata in arrivo");
            }
            else if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // telefono in chiamata (chiamata in partenza o in arrivo)
                isPhoneCalling = true;
                Log.d("DEBUGGGGGGGG", "In chiamata");
            }
            else if (TelephonyManager.CALL_STATE_IDLE == state) {
                // In idle (nessuna chiamata in arrivo o in partenza)
                if (isPhoneCalling) {
                    isPhoneCalling = false;

                    // Invia un broadcast
                    Intent intent = new Intent("com.example.ringlife.CALL_ENDED");
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    context.getApplicationContext().sendBroadcast(intent);
                    Log.d("DEBUGGGGGGGG", "INVIO ENDED");
                }
            }
            else{
                Log.d("DEBUGGGGGGGG", "ERRORE STRANO");
            }
        }
    }
}
