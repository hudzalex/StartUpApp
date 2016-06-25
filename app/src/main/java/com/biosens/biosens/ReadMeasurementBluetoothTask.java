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

public class ReadMeasurementBluetoothTask extends AsyncTask<Void, Void, Void>
{
    public interface ReadMeasurementBluetoothCallback
    {
        void onMeasurement(double[] result);
    }

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final String deviceAddress = "20:91:48:42:75:42";

    private ReadMeasurementBluetoothCallback callback;

    public ReadMeasurementBluetoothTask(ReadMeasurementBluetoothCallback callback)
    {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... devices)
    {
        while (true)
        {
            BluetoothSocket bluetoothSocket = null;

            try
            {
                try
                {
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

                    String result = "";

                    while(in.available() > 0)
                    {
                        result +=(char)in.read();
                    }

                    ArrayList<Double> resultNum = new ArrayList<>();

                    for (String part : result.split(" ")) {
                        resultNum.add(Double.parseDouble(part));
                    }

                    double[] resultArr = new double[resultNum.size()];

                    int i = 0;
                    for (Double num : resultNum) {
                        resultArr[i] = num;
                        i++;
                    }

                    callback.onMeasurement(resultArr);

                    return null;
                }
                catch (IOException e)
                {
                    Thread.sleep(5000);
                }
                finally
                {
                    if (bluetoothSocket != null)
                    {
                        try {
                            bluetoothSocket.close();
                        } catch (IOException e2){}
                    }
                }
            }
            catch (InterruptedException e2)
            {
                return null;
            }
        }
    }
}