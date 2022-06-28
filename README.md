# MongoDB-for-Android



### Steps to be  followed in the backend:

1. create an account on [MongoDb](http://mongodb.com) official website

  <br>
2. Fill this: as per your choice..
<img src="images/01.png"  width="40%"/>

  <br>
3. Select a plan, for now, select free one (shared)
<img src="images/02.png"  width="40%"/>

  <br>
4. will see as follow, continue with default settings:
<img src="images/03.png" width="40%"/>

<br>
5. provide a new username and password to create an user. Also add current ip address, then finish and close
<table>
  <tr>
    <td><img src="images/04.png"/></td>
    <td><img src="images/05.png"/></td>
  </tr>
  <tr>
</table>

<br>
6. Your account is ready, it will take a while to create a cluster. Finally, you will see following screen. <br>
    -> where you can add your teammate to collaborate. <br>
    -> create new database [with "browse collections" button] (see further instructions) <br>
    -> create an app in "App services" tab <br>
 <img src="images/20.png"  width="40%"/>
 
 <br>
 7. Go to "Browse Collections", will get following page
 <img src="images/08.png"  width="40%"/>
 
   <br>
 8. Go to "Add My own Data" option, which will ask you database name and collection name, fill that and create a collection
 <img src="images/09.png"  width="40%"/>
 
 <br>
 9. Now go to the "App services" tab, select "build your own app"
 <img src="images/18.png"  width="40%"/> 
 
  <br>
 10. Continue with default settings
  <img src="images/11.png"  width="40%"/>
  
 <br>
 11. App is created, you can see this page <br>
 Now you have to add rule for data access and user authentication method
 <img src="images/21.png"  width="40%"/>

  <br>
 12. Go to "rule", Yo can add customized rule for your data, for testing purpose, i'm giving access to everyone to read and write
  <img src="images/22.png"  width="40%"/>
 
 <br>
 13. Save your changes, Note that, saving and deploying is different thing so make sure you deploy your changes as shown below
  <img src="images/24.png"  width="40%"/>
  
  <br>
  14. Now go to "Authentication", turn on the way you want to authenticate your users, for now i'm using "anonymous login" <br>
  then save and deploy..
  <img src="images/26.png"  width="40%"/>
  
  <br>
  
## Now your backend is ready, go to android studio

### Project Level Gradle file:

```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "io.realm:realm-gradle-plugin:10.11.0"
    }
}

plugins {
    id 'com.android.application' version '7.2.0' apply false
    id 'com.android.library' version '7.2.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.6.21' apply false
    id "org.jetbrains.kotlin.kapt" version "1.6.20" apply false
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

### app levele gradle file
```
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'org.jetbrains.kotlin.kapt'
}


apply plugin: "realm-android"

android {
    compileSdk 32

    defaultConfig {
        applicationId "com.ihrsachin.mongodbrealmforandroid"
        minSdk 21
        targetSdk 32
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

realm {
    syncEnabled = true
}

dependencies {
    implementation 'io.realm:realm-gradle-plugin:10.11.0'
    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'androidx.appcompat:appcompat:1.4.2'
    implementation 'com.google.android.material:material:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
}
```

Follow the project to perform CRUD operations


  
 
 


