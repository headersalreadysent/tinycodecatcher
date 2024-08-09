package co.ec.cnsyn.codecatcher.sms

data class SmsData(var sender: String, var body: String, var date: Long = 0L)