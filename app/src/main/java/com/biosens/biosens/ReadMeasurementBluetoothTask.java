package com.biosens.biosens;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ReadMeasurementBluetoothTask {
    static final boolean random = true;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final String deviceAddress = "20:16:01:05:89:69";


    private static double[] generate(double min, double max){

        double[] randomResult = new double[7];

        for (int i = 0; i < randomResult.length; i++) {
            randomResult[i] = Math.floor((max - min) * Math.random() + min);
        }

        return randomResult;
    }

    public static double[] measure2() throws InterruptedException {
        if (random) {
            return generate(5, 50);
        } else {
            return measure();
        }
    }

        public static double[] measure() throws InterruptedException {
        if (random) {
            return generate(0, 0);
        }

        double[] result;

        while (true) {
            BluetoothSocket bluetoothSocket = null;

            try {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                bluetoothSocket.connect();

                InputStream in = bluetoothSocket.getInputStream();
                OutputStream out = bluetoothSocket.getOutputStream();

                byte[] bytes = ("m\n").getBytes();
                out.write(bytes);

                Thread.sleep(5000);

                String resultStr = "";

                while (in.available() > 0) {
                    resultStr += (char) in.read();
                }

                if (resultStr.length() == 0) {
                    Thread.sleep(2000);

                    while (in.available() > 0) {
                        resultStr += (char) in.read();
                    }
                }

                ArrayList<Double> resultNum = new ArrayList<>();

                for (String part : resultStr.split(" ")) {
                    if (part.length() > 0) {
                        resultNum.add(Double.parseDouble(part));
                    }
                }

                result = new double[resultNum.size()];

                int i = 0;
                for (Double num : resultNum) {
                    if (i == 0) {
                        result[i] = 0.22950819672131 * num - 5.1475409836066;
                    } else {
                        result[i] = 0;
                    }
                    i++;
                }

                return result;
            } catch (IOException e) {
                Thread.sleep(5000);
            } finally {
                if (bluetoothSocket != null) {
                    try {
                        bluetoothSocket.close();
                    } catch (IOException e2) {
                    }
                }
            }
        }
    }
}