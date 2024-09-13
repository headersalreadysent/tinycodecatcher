package co.ec.cnsyn.codecatcher.database.servicelog


import androidx.room.Dao
import androidx.room.Query
import co.ec.cnsyn.codecatcher.database.BaseDao
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.helpers.unix

@Dao
interface ServiceLogDao : BaseDao<ServiceLog> {

    @Query("SELECT * FROM servicelog")
    fun getAllItems(): List<ServiceLog>


    @Query("SELECT * FROM servicelog WHERE receiverId = :serviceId LIMIT 1")
    fun getById(serviceId: String): ServiceLog?

    @Query("UPDATE servicelog SET heartbeatCount=heartbeatCount+:beatCount WHERE receiverId = :serviceId")
    fun heartBeat(serviceId: String, beatCount: Int = 1)

    @Query("DELETE from servicelog WHERE id > 0")
    fun clean()

    companion object {
        /**
         * add receiver to database
         */
        fun addNew(receiverId: String) {
            async({
                val log = ServiceLog(
                    receiverId = receiverId,
                    date = unix().dateString("dd-MM-yyyy HH.mm"),
                    heartbeatCount = 0
                )
                DB.get().service().insert(log)
            })
        }


        /**
         * beat run count
         */
        fun beat(receiverId: String, count: Int = 1) {
            async({
                val record = DB.get().service().getById(receiverId)
                if (record == null) {
                    //if null add new
                    val log = ServiceLog(
                        receiverId = receiverId,
                        date = unix().dateString("dd-MM-yyyy HH.mm"),
                        heartbeatCount = 0
                    )
                    DB.get().service().insert(log)
                }
                DB.get().service().heartBeat(receiverId, count)
            })
        }
    }

}