package ch.ethz.coss.nervousnet.vm.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.BatteryReading;
import ch.ethz.coss.nervousnet.lib.ConnectivityReading;
import ch.ethz.coss.nervousnet.lib.GyroReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.LightReading;
import ch.ethz.coss.nervousnet.lib.LocationReading;
import ch.ethz.coss.nervousnet.lib.NoiseReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.sensors.BaseSensor.BaseSensorListener;
import ch.ethz.coss.nervousnet.vm.storage.DaoMaster.DevOpenHelper;
import de.greenrobot.dao.query.QueryBuilder;

public class SQLHelper implements BaseSensorListener {
	private static final String LOG_TAG = SQLHelper.class.getSimpleName();
	Config config = null;
	DaoMaster daoMaster;
	DaoSession daoSession;
	SQLiteDatabase sqlDB;
	ConfigDao configDao;
	SensorConfigDao sensorConfigDao;
	AccelDataDao accDao;
	BatteryDataDao battDao;
	LightDataDao lightDao;
	NoiseDataDao noiseDao;
	LocationDataDao locDao;
	ConnectivityDataDao connDao;
	GyroDataDao gyroDao;
	PressureDataDao pressureDao;
	
	ArrayList<SensorDataImpl> accelDataArrList, battDataArrList,connDataArrList,gyroDataArrList,lightDataArrList,locDataArrList, noiseDataArrList; 

	public SQLHelper(Context context, String DB_NAME) {
		initDao(context, DB_NAME);
	}

	private void initDao(Context context, String DB_NAME) {
		NNLog.d(LOG_TAG, "Inside initDao");
		try {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
			sqlDB = helper.getWritableDatabase();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Inside constructor and creating DB = " + DB_NAME, e);
		}

		daoMaster = new DaoMaster(sqlDB);
		daoSession = daoMaster.newSession();
		configDao = daoSession.getConfigDao();
		sensorConfigDao = daoSession.getSensorConfigDao();
		accDao = daoSession.getAccelDataDao();
		battDao = daoSession.getBatteryDataDao();
		locDao = daoSession.getLocationDataDao();
		connDao = daoSession.getConnectivityDataDao();
		gyroDao = daoSession.getGyroDataDao();
		lightDao = daoSession.getLightDataDao();
		noiseDao = daoSession.getNoiseDataDao();
		pressureDao = daoSession.getPressureDataDao();

		populateSensorConfig();
		 accelDataArrList = new ArrayList<SensorDataImpl>(); 
				 battDataArrList = new ArrayList<SensorDataImpl>(); 
				 connDataArrList = new ArrayList<SensorDataImpl>(); 
				 gyroDataArrList = new ArrayList<SensorDataImpl>(); 
				 lightDataArrList = new ArrayList<SensorDataImpl>(); 
				 locDataArrList = new ArrayList<SensorDataImpl>(); 
				 noiseDataArrList = new ArrayList<SensorDataImpl>(); 
		
	}

	public synchronized void populateSensorConfig() {
		NNLog.d(LOG_TAG, "Inside populateSensorConfig");
		boolean success = true;
		SensorConfig sensorconfig = null;

		NNLog.d(LOG_TAG, "sensorConfigDao - count = " + sensorConfigDao.queryBuilder().count());
		if (sensorConfigDao.queryBuilder().count() == 0) {

			for (int i = 0; i < NervousnetVMConstants.sensor_ids.length; i++)
				sensorConfigDao.insert(new SensorConfig(NervousnetVMConstants.sensor_ids[i],
						NervousnetVMConstants.sensor_labels[i], (byte) 0));
		}
	}

	public synchronized Config loadVMConfig() {
		NNLog.d(LOG_TAG, "Inside loadVMConfig");

		NNLog.d(LOG_TAG, "Config - count = " + configDao.queryBuilder().count());
		if (configDao.queryBuilder().count() != 0) {
			config = configDao.queryBuilder().unique();
		}

		return config;
	}

	public synchronized void storeVMConfig(byte state, UUID uuid) {
		NNLog.d(LOG_TAG, "Inside storeVMConfig");
		Config config = null;

		if (configDao.queryBuilder().count() == 0) {
			NNLog.d(LOG_TAG, "Config DB Is empty.");
			config = new Config(state, uuid.toString(), Build.MANUFACTURER, Build.MODEL, "Android",
					Build.VERSION.RELEASE, System.currentTimeMillis());
			configDao.insert(config);
		} else if (configDao.queryBuilder().count() == 1) {
			NNLog.d(LOG_TAG, "Config DB exists.");
			config = configDao.queryBuilder().unique();
			configDao.deleteAll();
			config.setState(state);
			configDao.insert(config);
			config = configDao.queryBuilder().unique();
			NNLog.d(LOG_TAG, "state = " + config.getState());
		} else
			Log.e(LOG_TAG, "Config DB count is more than 1. There is something wrong.");

	}

	public synchronized void updateSensorConfig(SensorConfig config) throws Exception {

		sensorConfigDao.insertOrReplace(config);
	}

	public synchronized void updateAllSensorConfig(SensorConfig... entities) throws Exception {

		sensorConfigDao.insertOrReplaceInTx(entities);
	}

	public synchronized void updateAllSensorConfig(Iterable entities) throws Exception {

		sensorConfigDao.insertOrReplaceInTx(entities);
	}

	
	public synchronized List<SensorConfig> getSensorConfigList() {
		return sensorConfigDao.queryBuilder().list();
	}

	public void storeSensorAsync(int type, ArrayList<SensorDataImpl> sensorDataList) {

		 new StoreTask(type).execute(sensorDataList);
	}

	class StoreTask extends AsyncTask<ArrayList<SensorDataImpl>, Integer, Void> {
		int type;
		public StoreTask(int type) {
			this.type = type;
		}

//		@Override
//		protected Void doInBackground(ArrayList<SensorDataImpl> params) {
//
//			if (params != null && params.length > 0) {
//
//				// for (int i = 0; i < params.length; i++) {
//				// Log.d(LOG_TAG, "doInBackground (params[i] = " + params[i] +
//				// ")");
//				storeSensor(type, Arrays.asList(params));
//				// }
//			}
//			return null;
//		}

		@Override
		protected Void doInBackground(ArrayList<SensorDataImpl>... params) {
			// TODO Auto-generated method stub
			storeSensor(type, params[0]);
			return null;
		}

	}

	public synchronized boolean storeSensor(int type, ArrayList sensorDataList) {
		NNLog.d(LOG_TAG, "Inside storeSensor ");

		if (sensorDataList == null) {
			Log.e(LOG_TAG, "sensorDataList is null. please check it");
			return false;
		}
		NNLog.d(LOG_TAG, "sensorDataList (Type = " + type + ")"); // ,
																				// Timestamp
																				// =
																				// "+sensorData.getTimeStamp()+",
																				// Volatility
																				// =
																				// "+sensorData.getVolatility());

		switch (type) {
		case LibConstants.SENSOR_ACCELEROMETER:
			NNLog.d(LOG_TAG, "ACCEL_DATA table count = " + accDao.count());
			accDao.insertInTx(sensorDataList);
			
			return true;

		case LibConstants.SENSOR_BATTERY:
			NNLog.d(LOG_TAG, "BATT_DATA table count = " + battDao.count());

			battDao.insertInTx(sensorDataList);
			return true;

		case LibConstants.DEVICE_INFO:
			return true;

		case LibConstants.SENSOR_LOCATION:
			NNLog.d(LOG_TAG, "LOCATION_DATA table count = " + locDao.count());
			locDao.insertInTx(sensorDataList);
			return true;

		case LibConstants.SENSOR_BLEBEACON:
			return true;

		case LibConstants.SENSOR_CONNECTIVITY:
			NNLog.d(LOG_TAG, "Connectivity_DATA table count = " + connDao.count());

			connDao.insertInTx(sensorDataList);
			return true;
		case LibConstants.SENSOR_GYROSCOPE:
			NNLog.d(LOG_TAG, "GYRO_DATA table count = " + gyroDao.count());
			gyroDao.insertInTx(sensorDataList);
			return true;
		case LibConstants.SENSOR_HUMIDITY:
			return true;
		case LibConstants.SENSOR_LIGHT:
			NNLog.d(LOG_TAG, "LIGHT_DATA table count = " + lightDao.count());
			lightDao.insertInTx(sensorDataList);
			return true;

		case LibConstants.SENSOR_MAGNETIC:
			return true;
		case LibConstants.SENSOR_NOISE:
			NNLog.d(LOG_TAG, "NoiseData table count = " + noiseDao.count());
			noiseDao.insertInTx(sensorDataList);
			return true;
		case LibConstants.SENSOR_PRESSURE:
			NNLog.d(LOG_TAG, "PressureData table count = " + pressureDao.count());
			pressureDao.insertInTx(sensorDataList);
			return true;
		case LibConstants.SENSOR_PROXIMITY:
			return true;
		case LibConstants.SENSOR_TEMPERATURE:
			return true;

		}
		return false;
	}

	public synchronized void getSensorReadings(int type, long startTime, long endTime, ArrayList<SensorReading> list) {
		QueryBuilder<?> qb = null;

		switch (type) {
		case LibConstants.SENSOR_ACCELEROMETER:
			qb = accDao.queryBuilder();
			qb.where(AccelDataDao.Properties.TimeStamp.between(startTime, endTime));
			@SuppressWarnings("unchecked")
			ArrayList<SensorDataImpl> aList = (ArrayList<SensorDataImpl>) qb.list();
			Iterator<SensorDataImpl> iterator = aList.iterator();
			while (iterator.hasNext()) {
				list.add(convertSensorDataToSensorReading(iterator.next()));
			}

			NNLog.d(LOG_TAG, "List size = " + list.size());

			return;
		case LibConstants.SENSOR_BATTERY:
			qb = battDao.queryBuilder();
			qb.where(BatteryDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.DEVICE_INFO:

			// TODO
			return;
		case LibConstants.SENSOR_LOCATION:
			qb = locDao.queryBuilder();
			qb.where(LocationDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_BLEBEACON:
			// TODO
			return;
		case LibConstants.SENSOR_CONNECTIVITY:
			qb = connDao.queryBuilder();
			qb.where(ConnectivityDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_GYROSCOPE:
			qb = gyroDao.queryBuilder();
			qb.where(GyroDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_HUMIDITY:
			// TODO
			return;
		case LibConstants.SENSOR_LIGHT:
			qb = lightDao.queryBuilder();
			qb.where(LightDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_MAGNETIC:
			// TODO
			return;
		case LibConstants.SENSOR_NOISE:
			qb = noiseDao.queryBuilder();
			qb.where(NoiseDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_PRESSURE:
			qb = pressureDao.queryBuilder();
			qb.where(PressureDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_PROXIMITY:
			// TODO
			return;
		case LibConstants.SENSOR_TEMPERATURE:
			// TODO
			return;

		}

	}

	private synchronized SensorReading convertSensorDataToSensorReading(SensorDataImpl data) {
		NNLog.d(LOG_TAG, "convertSensorDataToSensorReading reading Type = " + data.getType());
		SensorReading reading = null;

		switch (data.getType()) {
		case LibConstants.SENSOR_ACCELEROMETER:
			AccelData adata = (AccelData) data;
			reading = new AccelerometerReading(adata.getTimeStamp(),
					new float[] { adata.getX(), adata.getY(), adata.getZ() });
			reading.type = LibConstants.SENSOR_ACCELEROMETER;

			return reading;

		case LibConstants.SENSOR_BATTERY:

			// BatteryData bdata = (BatteryData) data;
			// reading = new BatteryReading(bdata.getTimeStamp(),
			// bdata.getPercent(), false, false, false, state, state, state,
			// null});
			// reading.type = LibConstants.SENSOR_BATTERY;

			return reading;

		case LibConstants.SENSOR_GYROSCOPE:

			return reading;

		case LibConstants.SENSOR_CONNECTIVITY:

			return reading;
		case LibConstants.SENSOR_LIGHT:
			return reading;

		case LibConstants.SENSOR_LOCATION:
			return reading;

		default:
			return null;

		}
	}

	@Override
	public void sensorDataReady(SensorReading reading) {
		if(reading != null)
		convertSensorReadingToSensorData(reading);
	}

	private synchronized void convertSensorReadingToSensorData(SensorReading reading) {
		NNLog.d(LOG_TAG, "convertSensorReadingToSensorData reading Type = " + reading.type);
		SensorDataImpl sensorData;

		switch (reading.type) {
		case LibConstants.SENSOR_ACCELEROMETER:
			AccelerometerReading areading = (AccelerometerReading) reading;
			sensorData = new AccelData(null, reading.timestamp, areading.getX(), areading.getY(), areading.getZ(), 0l,
					true);
			sensorData.setType(LibConstants.SENSOR_ACCELEROMETER);
			accelDataArrList.add((SensorDataImpl)sensorData);
			
			if(accelDataArrList.size() > 100){
				storeSensorAsync(LibConstants.SENSOR_ACCELEROMETER,  new ArrayList<SensorDataImpl>(accelDataArrList));
				accelDataArrList.clear();
			}
			
			break;
		case LibConstants.SENSOR_BATTERY:
			BatteryReading breading = (BatteryReading) reading;
			sensorData = new BatteryData(null, reading.timestamp, breading.getPercent(), breading.getCharging_type(),
					breading.getHealth(), breading.getTemp(), breading.getVolt(), breading.volatility,
					breading.isShare);
			sensorData.setType(LibConstants.SENSOR_BATTERY);
			
			battDataArrList.add((SensorDataImpl)sensorData);
			
			if(battDataArrList.size() > 10){
				storeSensorAsync(LibConstants.SENSOR_BATTERY,  new ArrayList<SensorDataImpl>(battDataArrList));
				battDataArrList.clear();
			}
			break;
		case LibConstants.SENSOR_GYROSCOPE:
			GyroReading greading = (GyroReading) reading;
			sensorData = new GyroData(null, reading.timestamp, greading.getGyroX(), greading.getGyroY(),
					greading.getGyroZ(), 0l, true);
			sensorData.setType(LibConstants.SENSOR_GYROSCOPE);
			gyroDataArrList.add((SensorDataImpl)sensorData);
			
			if(gyroDataArrList.size() > 100){
				storeSensorAsync(LibConstants.SENSOR_GYROSCOPE, new ArrayList<SensorDataImpl>(gyroDataArrList));
				gyroDataArrList.clear();
			}
			break;
		case LibConstants.SENSOR_CONNECTIVITY:
			ConnectivityReading connReading = (ConnectivityReading) reading;
			sensorData = new ConnectivityData(null, reading.timestamp, connReading.isConnected(),
					connReading.getNetworkType(), connReading.isRoaming(), connReading.getWifiHashId(),
					connReading.getWifiStrength(), connReading.getMobileHashId(), connReading.volatility,
					connReading.isShare);
			sensorData.setType(LibConstants.SENSOR_CONNECTIVITY);
			connDataArrList.add((SensorDataImpl)sensorData);
			
			if(connDataArrList.size() > 10){
				storeSensorAsync(LibConstants.SENSOR_CONNECTIVITY, new ArrayList<SensorDataImpl>(connDataArrList));
				connDataArrList.clear();
			}
			break;

		case LibConstants.SENSOR_LIGHT:
			LightReading lreading = (LightReading) reading;
			sensorData = new LightData(null, reading.timestamp, lreading.getLuxValue(), lreading.volatility,
					lreading.isShare);
			sensorData.setType(LibConstants.SENSOR_LIGHT);
			lightDataArrList.add((SensorDataImpl)sensorData);
			
			if(lightDataArrList.size() > 100){
				storeSensorAsync(LibConstants.SENSOR_LIGHT, new ArrayList<SensorDataImpl>(lightDataArrList));
				lightDataArrList.clear();
			}
			break;
		case LibConstants.SENSOR_LOCATION:
			LocationReading locReading = (LocationReading) reading;
			sensorData = new LocationData(null, reading.timestamp, locReading.getLatnLong()[0],
					locReading.getLatnLong()[1], 0.0, 0l, true);
			sensorData.setType(LibConstants.SENSOR_LOCATION);
			locDataArrList.add((SensorDataImpl)sensorData);
			if(locDataArrList.size() > 100){
				storeSensorAsync(LibConstants.SENSOR_LOCATION, new ArrayList<SensorDataImpl>(locDataArrList));
				locDataArrList.clear();
			}
			
			break;
		case LibConstants.SENSOR_NOISE:
			NoiseReading noiseReading = (NoiseReading) reading;
			sensorData = new NoiseData( null, reading.timestamp, noiseReading.getdbValue(), 0l, true);
			sensorData.setType(LibConstants.SENSOR_NOISE);
			noiseDataArrList.add((SensorDataImpl)sensorData);
			if(noiseDataArrList.size() > 100){
				storeSensorAsync(LibConstants.SENSOR_NOISE, new ArrayList<SensorDataImpl>(noiseDataArrList));
				noiseDataArrList.clear();
			}
			
			break;
			
		default:
			break;

		}
	}

}