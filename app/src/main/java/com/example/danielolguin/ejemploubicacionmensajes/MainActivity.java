package com.example.danielolguin.ejemploubicacionmensajes;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.danielolguin.ejemploubicacionmensajes.Utilidades.GPSTracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class MainActivity extends AppCompatActivity {

    private static final int MY_SCAN_REQUEST_CODE = 2;
    int codigo = 0;
    public static MainActivity INSTANCE = null;
    CountDownTimer timer1 = null;
    long Duracion = 120000;
    long tiempoGuardado;
    long TiempoAgregado = 0;
    GPSTracker gps;

    RelativeLayout lytTemporizador;
    TextView txtCronometro;
    Button btnConfirmarNumero;

    private synchronized static void createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainActivity();
        }
    }

    public static MainActivity getInstance() {
        createInstance();
        return INSTANCE;
    }

    public MainActivity() {
        INSTANCE = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConfirmarNumero = (Button) findViewById(R.id.btnConfirmarNumero);
        txtCronometro = (TextView) findViewById(R.id.txtCronometro);
        lytTemporizador = (RelativeLayout) findViewById(R.id.lytTemporizador);

        btnConfirmarNumero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanPress();
            }
        });
        txtCronometro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarSmss();
            }
        });

        gps = new GPSTracker(MainActivity.this);

        // Check if GPS enabled
        if(gps.canGetLocation()) {
            getLocation();
        }
        else
        {
            gps.showSettingsAlert();
        }

    }

    private void enviarSmss() {
        //if (txtNumero.getText().toString().equalsIgnoreCase("") == false) {
            Random r = new Random();
            codigo = r.nextInt(9999);
            SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+52" + "6673905526", null, "mensaje de prueba para checar code:" + codigo, null, null);
        lytTemporizador.setVisibility(View.VISIBLE);
        temporizador();
        //}
    }
    public void verificarCodigoSms(String prcodigo) {
        if (String.valueOf(codigo).equalsIgnoreCase(prcodigo) == true) {
            lytTemporizador.setVisibility(View.GONE);
        }
    }
    private void temporizador() {
        timer1 = new CountDownTimer((Duracion + TiempoAgregado), 1000) {
            @Override
            public void onFinish() {
                txtCronometro.setText("Reintentar");
            }

            @Override
            public void onTick(long millisUntilFinished) {
                String sTiempo = longToFormaTime(millisUntilFinished - TiempoAgregado);
                String Hora1 = sTiempo.substring(0, 1);
                String Hora2 = sTiempo.substring(1, 2);
                String Minuto1 = sTiempo.substring(3, 4);
                String Minuto2 = sTiempo.substring(4, 5);
                String Segundos = sTiempo.substring(6, 8);
                txtCronometro.setText(Minuto2 + ":" + Segundos);
                tiempoGuardado = millisUntilFinished - TiempoAgregado;
                if (tiempoGuardado < 0) {
                }
            }
        }.start();
    }
    private String longToFormaTime(long prTimeSpan) {
        String time;
        if (prTimeSpan >= 0) {
            long second = Math.abs(prTimeSpan / 1000) % 60;
            long minute = Math.abs(prTimeSpan / (1000 * 60)) % 60;
            long hour = Math.abs(prTimeSpan / (1000 * 60 * 60)) % 24;

            time = String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            long second = Math.abs(prTimeSpan / 1000) % 60;
            long minute = Math.abs(prTimeSpan / (1000 * 60)) % 60;
            long hour = Math.abs(prTimeSpan / (1000 * 60 * 60)) % 24;

            time = String.format("-" + "%02d:%02d:%02d", hour, minute, second);
        }
        return time;
    }

    public void onScanPress() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }
    public String getLocation() {
        if(gps.canGetLocation() == false) {
            return  null;
        }
        if (gps.getLatitude() != 0.0 && gps.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        gps.getLatitude(), gps.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address address = list.get(0);
                    return address.getAddressLine(0);
                }
                else
                {
                    return null;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

                //numero de tarjeta sin ocultar
                //resultDisplayStr = scanResult.cardNumber;

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
        }
    }
}
