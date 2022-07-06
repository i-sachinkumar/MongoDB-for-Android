package com.ihrsachin.mongodbrealmforandroid

/**
 * Backend Instructions:
 * refer to readme file
 *
 * Android Studio Setup instructions:
 * https://www.mongodb.com/docs/realm/sdk/java/examples/mongodb-remote-access/
 *
 * Database Query:
 * Docs : https://www.mongodb.com/docs/realm/sdk/java/examples/mongodb-remote-access/
 */

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoCollection
import io.realm.mongodb.mongo.MongoDatabase
import io.realm.mongodb.mongo.options.InsertManyResult
import org.bson.Document
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object{
        init {
            System.loadLibrary("mongodbrealmforandroid")
        }
    }

    //unique app id
    private var appId = "application-0-wjyfy"


    //MongoDb fields
    private var app: App? = null
    private var mongoDatabase: MongoDatabase? = null
    private var mongoClient: MongoClient? = null
    private var mongoCollection : MongoCollection<Document>? = null
    private var user : User? = null


    //Views
    lateinit var containerView : LinearLayout
    lateinit var runTestBtn : Button
    lateinit var readBtn : Button
    lateinit var progressBar: ProgressBar
    lateinit var accMeterReading : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Important: should be initialized only once
        Realm.init(this)



        containerView = findViewById(R.id.container)
        runTestBtn = findViewById(R.id.test_btn)
        readBtn = findViewById(R.id.read_btn)
        progressBar = findViewById(R.id.progress_bar)
        accMeterReading = findViewById(R.id.acc_meter_reading)


//        Handler(Looper.getMainLooper()).postDelayed({
//            accMeterReading.text = AccelerometerJNI.updateText()
//        }, 100)

        accMeterReading.text = AccelerometerJNI.accelerometer()


        //building app with app id
        app = App(AppConfiguration.Builder(appId).build())

        //logging in anonymously, can try another method as well
        val credential = Credentials.anonymous()

        app!!.loginAsync(credential
        ) { result ->
            if (result.isSuccess) {
                showStatus("User Logged In Successfully")

                //current user
                user = app!!.currentUser()


                mongoClient = user!!.getMongoClient("mongodb-atlas")    //same for everyone

                //database name and collection name which we have created in mongoDB account
                mongoDatabase = mongoClient!!.getDatabase("TestDatabase")
                mongoCollection = mongoDatabase!!.getCollection("TestCollection")


                /** Uncomment to perform any query */
//                insertData()          // 10000 data
//                readData()            // filtered data, sample6
//                updateData()          // update, sample6 -> new sample
//                countData()           //count new sample data
//                deleteData()          //delete all
            } else {
                showStatus("User Failed to Login: ${result.error}")
            }
        }


        runTestBtn.setOnClickListener {
            insertData()
        }
        readBtn.setOnClickListener {
            readData()
        }

        //val viewTreeObserver: ViewTreeObserver = main_layout.viewTreeObserver


        /**
         * Login with credentials
         */
        /*
        val credentials = Credentials.emailPassword("abc@gmail.com", "12345")
        app!!.loginAsync(Credentials.anonymous()
        ) { result ->
            if (result.isSuccess) {
                Log.v("User", "Logged In Successfully")
            } else {
                Log.v("User", "Failed to Login")
            }
        }

        */

    }

    private fun insertData() {
        progressBar.visibility = VISIBLE
        runTestBtn.isClickable = false
        readBtn.isClickable = false


        //inserting data as documents: (Key - Value) pair
        val sampleData : ArrayList<String> = arrayListOf(
            "sample1",
            "sample2",
            "sample3",
            "sample4",
            "sample5",
            "sample6",
            "sample7",
            "sample8",
            "sample9",
            "sample10"
        )

        val list = mutableListOf<Document>()

        val count = 10000
        for (i in 1..count){
            sampleData.shuffle()
            list.add(Document("userid", user!!.id).append("data",
                sampleData[0]))
        }

        // insert query: start time
        val start = System.nanoTime()
        showStatus("Insert Query started for 10000 sample data")

        mongoCollection?.insertMany(list)
            ?.getAsync { r: App.Result<InsertManyResult?> ->
                if (r.isSuccess) {
                    showStatus("$count Data Inserted Successfully")

                    // insert query: end time
                    val end = System.nanoTime()
                    showStatus("Time taken to insert $count data: " + (end-start).toString() + " nanoseconds")
                    progressBar.visibility = GONE
                    runTestBtn.isClickable = true
                    readBtn.isClickable = true
                } else {
                    showStatus("Error in inserting data: " + r.error.toString())
                    progressBar.visibility = GONE
                    runTestBtn.isClickable = true
                    readBtn.isClickable = true
                }
            }
    }



    // reading with condition: data == sample6
    private fun readData(){
        progressBar.visibility = VISIBLE
        runTestBtn.isClickable = false
        readBtn.isClickable = false
        showStatus("started read query for data : sample6")

        val start = System.nanoTime()

        val queryFilter = Document("data", "sample6")
        val findTask = mongoCollection?.find(queryFilter)?.iterator()
        findTask?.getAsync { task ->
            if (task.isSuccess) {
                val results = task.get()
                var count = 0
                while (results.hasNext()) {
                    results.next()
                    count++
                }
                showStatus("successfully found all sample6 data, $count occurrence")
                showStatus("Get Query: Time taken = " + (System.nanoTime()-start).toString() + " nanoseconds for $count data")
                progressBar.visibility = GONE
                runTestBtn.isClickable = true
                readBtn.isClickable = true
            } else {
                showStatus("Error in finding: ${task.error}")
                progressBar.visibility = GONE
                runTestBtn.isClickable = true
                readBtn.isClickable = true
            }
        }
    }


    //delete all data of current user
    private fun deleteData(){
        showStatus("delete all query started")
        val queryFilter = Document("userid", "62b91eec822c2bddbd44229c")
        mongoCollection?.deleteMany(queryFilter)?.getAsync { task ->
            if (task.isSuccess) {
                val count = task.get().deletedCount
                if (count != 0L) {
                    showStatus("successfully deleted $count documents.")
                } else {
                    showStatus("did not delete any documents.")
                }
            } else {
                "failed to delete documents with error: ${task.error}"
            }
        }
    }

    //count data with condition: data == sample2
    private fun countData(){
        showStatus("counting started for sample2 data")
        val queryFilter = Document("data", "sample2")
        mongoCollection?.count(queryFilter)?.getAsync { task ->
            if (task.isSuccess) {
                val count = task.get()
                showStatus("successfully counted, number of documents in the collection: $count")
            } else {
                showStatus("failed to count documents with: ${task.error}")
            }
        }
    }

    //update query: sample7 -> new sample
    private fun updateData(){
        showStatus("update query started: sample7 -> new sample")
        progressBar.visibility = VISIBLE

        val queryFilter = Document("data", "sample7")
        val updateDocument = Document("\$set", Document("data", "new sample"))

        val start = System.nanoTime()

        mongoCollection?.updateMany(queryFilter, updateDocument)?.getAsync { task ->
            if (task.isSuccess) {
                progressBar.visibility = GONE
                val count = task.get().modifiedCount
                if (count != 0L) {
                    showStatus("successfully updated $count documents.")
                    showStatus("Time Taken: ${System.nanoTime() - start} nanoseconds for $count data")
                } else {
                    showStatus("did not update any documents.")
                }
            } else {
                showStatus("failed to update documents with error: ${task.error}")
            }
        }
    }


    private fun showStatus(text: String) {
        Log.i("Status", text)
        val textView = TextView(this)
        textView.setPadding(10,10,10,10)
        textView.text = text
        containerView.addView(textView)
    }


/*
    private fun basicCRUD(realm: Realm) {
        showStatus("Perform basic Create/Read/Update/Delete (CRUD) operations...")

        // All writes must be wrapped in a transaction to facilitate safe multi threading
        realm.executeTransaction { r ->
            // Add a person.
            // RealmObjects with primary keys created with `createObject()` must specify the primary key value as an argument.
            val data: DataClass = r.createObject(DataClass::class.java, 1)
            data.name = "Young Person"
            data.age = 20

            // Even young people have at least one phone in this day and age.
            // Please note that this is a RealmList that contains primitive values.
            //person.getPhoneNumbers().add("+1 123 4567")
        }

        // Find the first person (no query conditions) and read a field
        val data: DataClass? = realm.where(DataClass::class.java).findFirst()
        showStatus(data!!.name + ":" + data.age)


        // Update person in a transaction
        realm.executeTransaction { r ->
            // Managed objects can be modified inside transactions.
            data.name = ("Senior Person")
            data.age = (99)
            showStatus(data.name+ " got older: " + data.age)
        }

        // Delete all persons
        showStatus("Deleting all persons")
        realm.executeTransaction { r -> r.delete(DataClass::class.java) }
    }

    /**
     * Coroutines to make CRUD request on another thread
     */

    private fun launchCoroutine(){
        Toast.makeText(this,"another thread", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch { crudRequest(realm?: Realm.getDefaultInstance()) }
    }

    private suspend fun crudRequest(realm: Realm) {
        Looper.prepare()
        basicCRUD(realm)
        updateStatusOnMainThread()
    }

    private suspend fun updateStatusOnMainThread() {
        withContext (Dispatchers.Main) {
            Toast.makeText(this@MainActivity, "basic crud operation successful", Toast.LENGTH_SHORT).show()
        }
    }

 */

}
