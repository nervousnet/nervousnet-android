package ch.ethz.coss.nervousnet.vm.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import ch.ethz.coss.nervousnet.vm.storage.BeaconData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BEACON_DATA".
*/
public class BeaconDataDao extends AbstractDao<BeaconData, Long> {

    public static final String TABLENAME = "BEACON_DATA";

    /**
     * Properties of entity BeaconData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TimeStamp = new Property(1, Long.class, "TimeStamp", false, "TIME_STAMP");
        public final static Property Rssi = new Property(2, Integer.class, "rssi", false, "RSSI");
        public final static Property Mac = new Property(3, Long.class, "mac", false, "MAC");
        public final static Property AdvertisementMSB = new Property(4, Long.class, "advertisementMSB", false, "ADVERTISEMENT_MSB");
        public final static Property AdvertisementLSB = new Property(5, Long.class, "advertisementLSB", false, "ADVERTISEMENT_LSB");
        public final static Property BleuuidMSB = new Property(6, Long.class, "bleuuidMSB", false, "BLEUUID_MSB");
        public final static Property BleuuidLSB = new Property(7, Long.class, "bleuuidLSB", false, "BLEUUID_LSB");
        public final static Property Major = new Property(8, Integer.class, "major", false, "MAJOR");
        public final static Property Minor = new Property(9, Integer.class, "minor", false, "MINOR");
        public final static Property Txpower = new Property(10, Integer.class, "txpower", false, "TXPOWER");
        public final static Property Volatility = new Property(11, long.class, "Volatility", false, "VOLATILITY");
        public final static Property ShareFlag = new Property(12, Boolean.class, "ShareFlag", false, "SHARE_FLAG");
    };


    public BeaconDataDao(DaoConfig config) {
        super(config);
    }
    
    public BeaconDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BEACON_DATA\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TIME_STAMP\" INTEGER," + // 1: TimeStamp
                "\"RSSI\" INTEGER," + // 2: rssi
                "\"MAC\" INTEGER," + // 3: mac
                "\"ADVERTISEMENT_MSB\" INTEGER," + // 4: advertisementMSB
                "\"ADVERTISEMENT_LSB\" INTEGER," + // 5: advertisementLSB
                "\"BLEUUID_MSB\" INTEGER," + // 6: bleuuidMSB
                "\"BLEUUID_LSB\" INTEGER," + // 7: bleuuidLSB
                "\"MAJOR\" INTEGER," + // 8: major
                "\"MINOR\" INTEGER," + // 9: minor
                "\"TXPOWER\" INTEGER," + // 10: txpower
                "\"VOLATILITY\" INTEGER NOT NULL ," + // 11: Volatility
                "\"SHARE_FLAG\" INTEGER);"); // 12: ShareFlag
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BEACON_DATA\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, BeaconData entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long TimeStamp = entity.getTimeStamp();
        if (TimeStamp != null) {
            stmt.bindLong(2, TimeStamp);
        }
 
        Integer rssi = entity.getRssi();
        if (rssi != null) {
            stmt.bindLong(3, rssi);
        }
 
        Long mac = entity.getMac();
        if (mac != null) {
            stmt.bindLong(4, mac);
        }
 
        Long advertisementMSB = entity.getAdvertisementMSB();
        if (advertisementMSB != null) {
            stmt.bindLong(5, advertisementMSB);
        }
 
        Long advertisementLSB = entity.getAdvertisementLSB();
        if (advertisementLSB != null) {
            stmt.bindLong(6, advertisementLSB);
        }
 
        Long bleuuidMSB = entity.getBleuuidMSB();
        if (bleuuidMSB != null) {
            stmt.bindLong(7, bleuuidMSB);
        }
 
        Long bleuuidLSB = entity.getBleuuidLSB();
        if (bleuuidLSB != null) {
            stmt.bindLong(8, bleuuidLSB);
        }
 
        Integer major = entity.getMajor();
        if (major != null) {
            stmt.bindLong(9, major);
        }
 
        Integer minor = entity.getMinor();
        if (minor != null) {
            stmt.bindLong(10, minor);
        }
 
        Integer txpower = entity.getTxpower();
        if (txpower != null) {
            stmt.bindLong(11, txpower);
        }
        stmt.bindLong(12, entity.getVolatility());
 
        Boolean ShareFlag = entity.getShareFlag();
        if (ShareFlag != null) {
            stmt.bindLong(13, ShareFlag ? 1L: 0L);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public BeaconData readEntity(Cursor cursor, int offset) {
        BeaconData entity = new BeaconData( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // TimeStamp
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // rssi
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // mac
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // advertisementMSB
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // advertisementLSB
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // bleuuidMSB
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // bleuuidLSB
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // major
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // minor
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // txpower
            cursor.getLong(offset + 11), // Volatility
            cursor.isNull(offset + 12) ? null : cursor.getShort(offset + 12) != 0 // ShareFlag
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, BeaconData entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTimeStamp(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setRssi(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setMac(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setAdvertisementMSB(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setAdvertisementLSB(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setBleuuidMSB(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setBleuuidLSB(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setMajor(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setMinor(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setTxpower(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setVolatility(cursor.getLong(offset + 11));
        entity.setShareFlag(cursor.isNull(offset + 12) ? null : cursor.getShort(offset + 12) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(BeaconData entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(BeaconData entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
