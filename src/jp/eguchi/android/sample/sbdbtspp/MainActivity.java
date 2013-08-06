package jp.eguchi.android.sample.sbdbtspp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * SBDBTとSPP接続するサンプル・アプリケーション
 * @author Kazuyuki Eguchi
 *
 */
public class MainActivity extends Activity
{
	private static final String TAG = "SBDBT_SPP";
	
	private BluetoothAdapter BtAdapter = null;
	private BluetoothDevice mBtDevice = null;
	private BluetoothSocket mBtSock = null;
	private OutputStream mOut = null;
	
	private Button btn00 = null; 
	private Button btn01 = null;
	private Button btn02 = null;
	private Button btn03 = null;
	private Button btn04 = null;
	private Button btn05 = null;
	private Button btn06 = null;
	private Button btn07 = null;
	
	private Spinner spinner01 = null; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		spinner01 = (Spinner)findViewById(R.id.spinner01);
		
		btn00 = (Button)findViewById(R.id.button00);
		btn01 = (Button)findViewById(R.id.button01);
		btn02 = (Button)findViewById(R.id.button02);
		btn03 = (Button)findViewById(R.id.button03);
		btn04 = (Button)findViewById(R.id.button04);
		btn05 = (Button)findViewById(R.id.button05);
		btn06 = (Button)findViewById(R.id.button06);
		btn07 = (Button)findViewById(R.id.button07);
		
		btn00.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(mBtSock == null)
				{
					if(spinner01 == null)
						return;
				
					String addr = (String)spinner01.getSelectedItem();
				
					if(addr != null)
					{
						connect(addr);
					}
				}
				else
				{
					disconnect();
				}
			}
		});
		
		btn01.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				send_data(0x31);
			}
		});

		btn02.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				send_data(0x32);
			}
		});

		btn03.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				send_data(0x33);
			}
		});
		
		btn04.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				send_data(0x34);
			}
		});
		
		btn05.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				send_data(0x35);
			}
		});

		btn06.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				send_data(0x36);
			}
		});

		btn07.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				send_data(0x37);
			}
		});
		
	}

	@Override
	protected void onPause()
	{
		disconnect();
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		step0();
	}

	/**
	 * @param a 送信するデータ
	 */
	void send_data(int a)
	{
		try
		{
			mOut.write(a);
		} catch (IOException e)
		{
		}
	}

	/**
	 * Bluetoothを初期化し、ペアリング済みのデバイスの一覧を生成する
	 */
	void step0()
	{
		Log.d(TAG,"step0()");
		
		BtAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(BtAdapter == null)
		{
			// Bluetooth対応機種でない場合は何もしない
			return;
		}

		if(BtAdapter.isEnabled() == false)
		{
			// Bluetoothが有効でない場合は有効にする
			BtAdapter.enable();
			return;
		}
		
		// 選択肢を生成する
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// ペアリング済みのデバイスの一覧を取得する
		Set<BluetoothDevice> paireds = BtAdapter.getBondedDevices();
		for(BluetoothDevice device : paireds)
		{
			adapter.add(device.getAddress());
		}
		
		// 選択肢をSpinnerに設定する
		spinner01.setAdapter(adapter);
	}
	
	/**
	 * 指定されたBluetoothデバイスに接続する
	 * @param addr 接続するBluetoothアドレス
	 */
	void connect(String addr)
	{
		BtAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(BtAdapter == null)
		{
			// Bluetooth対応機種でない場合は何もしない
			return;
		}

		// デバイスに接続する
		mBtDevice = BtAdapter.getRemoteDevice(addr);
		
		try
		{
			mBtSock = mBtDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			mBtSock.connect();

			mOut = mBtSock.getOutputStream();
			
			btn01.setEnabled(true);
			btn02.setEnabled(true);
			btn03.setEnabled(true);
			btn04.setEnabled(true);
			btn05.setEnabled(true);
			btn06.setEnabled(true);
			btn07.setEnabled(true);
			
			btn00.setText(R.string.disconnect);
		}
		catch(IOException ex)
		{
			Log.d(TAG,ex.toString());
			disconnect();
			return;
		}
	}
	
	/**
	 * 接続しているBluetoothデバイスを切断する
	 */
	void disconnect()
	{
		if(mOut != null)
		{
			try
			{
				mOut.close();
			}
			catch (IOException e)
			{
			}
			
			mOut = null;
		}
		
		if(mBtSock != null)
		{
			if(mBtSock.isConnected() == true)
			{
				try
				{
					mBtSock.close();
				}
				catch (IOException e)
				{
				}
			}
			
			mBtSock = null;
		}
		
		btn01.setEnabled(false);
		btn02.setEnabled(false);
		btn03.setEnabled(false);
		btn04.setEnabled(false);
		btn05.setEnabled(false);
		btn06.setEnabled(false);
		btn07.setEnabled(false);
		
		btn00.setText(R.string.connect);

	}
	
}
