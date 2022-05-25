package com.thetheu.qmttclient;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Config extends AppCompatActivity {
    private Button conectar;
    private Button topic;
    private Button disconect;
    private EditText tbIP;
    private EditText tbTopic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        tbIP = findViewById(R.id.tbIP);
        String IP = tbIP.getText().toString();
        tbTopic = findViewById(R.id.tbTopic);
        String Topic = tbTopic.getText().toString();
        conectar = findViewById(R.id.btnConect);
        topic = findViewById(R.id.button3);
        disconect = findViewById(R.id.btnDisconnect);
        String clientId = MqttClient.generateClientId();
        MqttAndroidClient client = new MqttAndroidClient(Config.this, "tcp://"+IP+":1883",
                clientId);
        conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                try {
                    MqttConnectOptions opts = new MqttConnectOptions();
                    opts.setConnectionTimeout(240000);
                    client.connect(opts).setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Log.d(TAG, "onSuccess");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Log.d(TAG, "onFailure");

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Estou aqui");
                String topic = "teste";
                int qos = 1;

                try {
                    Log.d(TAG, "Estou aqui2");
                    if(client.isConnected()) {
                        Log.d(TAG, "Estou aqui3");
                        client.subscribe(Topic, qos);
                        client.setCallback(new MqttCallback() {
                            @Override
                            public void connectionLost(Throwable cause) {
                                Log.d(TAG, "perdeu a conexão");
                            }

                            @Override
                            public void messageArrived(String Topic, MqttMessage message) throws Exception {

                                Log.d(TAG, "Estou aqui4");
                                String result = new String(message.getPayload());
                                Log.d(TAG, "message>>" + result);
                                Log.d(TAG, "topic>>" + Topic);

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
        disconect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.close();
                client.unregisterResources();
            }
        });

    }
}

