package app.cui.ro.db;

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity;
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey;
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity(tableName = "hidratacion")
data class HidratacionEntity(
        @PrimaryKey val id: Int = 0, // Siempre 0 porque solo manejamos un progreso
        val cantidadMl: Int // cantidad de agua en mililitros
)


@Dao
interface HidratacionDao {
        @Query("SELECT * FROM hidratacion WHERE id = 0")
        suspend fun getProgreso(): HidratacionEntity?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertarProgreso(hidratacion: HidratacionEntity)
}

@Database(entities = [HidratacionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
        abstract fun hidratacionDao(): HidratacionDao
}