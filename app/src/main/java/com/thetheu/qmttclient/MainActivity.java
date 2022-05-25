package com.thetheu.qmttclient;

import static android.content.ContentValues.TAG;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.ParcelableMqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private Button btn2;
    private Button btnConfig;
    private TextView temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        btnConfig = findViewById(R.id.btnConfig);
        temp= findViewById(R.id.textView);
        String clientId = MqttClient.generateClientId();
        MqttAndroidClient client = new MqttAndroidClient(MainActivity.this, "tcp://192.168.0.191:1883",
                clientId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MqttConnectOptions opts = new MqttConnectOptions();
                    opts.setConnectionTimeout(240000);
                    client.connect(opts).setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.d(TAG, "onSuccess");
                            Context context = getApplicationContext();
                            CharSequence text = "Conectado";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.d(TAG, "onFailure");
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Estou aqui");
                String topic = "teste";
                int qos = 1;
                try {
                    Log.d(TAG, "Estou aqui2");
                    if(client.isConnected()) {
                        Log.d(TAG, "Estou aqui3");
                        client.subscribe("RIBEIRAO_TEMPERATURE", qos);
                        client.setCallback(new MqttCallback() {
                            @Override
                            public void connectionLost(Throwable cause) {
                                Log.d(TAG, "perdeu a conexão");
                            }
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                topic = "RIBEIRAO_TEMPERATURE";
                                Log.d(TAG, "Estou aqui4");
                                String result = new String(message.getPayload());
                                temp.setText(result+"º");
                                Log.d(TAG, "Temperatura: " + result+"º");
                                Log.d(TAG, "topic>>" + topic);
                                Context context = getApplicationContext();
                                CharSequence text = "Conectado";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                            @Override
                            public void deliveryComplete(IMqttDeliveryToken token) {
                                Log.d(TAG, "Ifood chegou bb");
                            }
                        });
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void setBtnConfig(View view) {
        Intent config = new Intent(this, Config.class);
        startActivity(config);
    }
}