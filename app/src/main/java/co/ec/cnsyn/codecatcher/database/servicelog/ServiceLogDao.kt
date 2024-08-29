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

    @Query("UPDATE servicelog SET heartbeatCount=heartbeatCount+1 WHERE receiverId = :serviceId")
    fun heartBeat(serviceId: String)

    companion object {
        fun addNew(receiverId:String){
            async({
                DB.get().service().insert(
                    ServiceLog(
                        receiverId = receiverId,
                        date = unix().dateString("dd-MM-yyyy HH.mm")
                    )
                )
            })
        }


        fun beat(receiverId:String){
            async({
                DB.get().service().heartBeat(receiverId)
            })
        }
    }

}