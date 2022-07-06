#include <jni.h>
#include <iostream>
#include <android/sensor.h>
#include <android/log.h>
#include <android/looper.h>
#include <dlfcn.h>
#include "mongodbrealmforandroid.h"


#define  LOG_TAG    "accelerometer"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static int looperCallback(int fd, int events, void* data);

static int looperCallback(int fd, int events, void* data){
    if(fd >= 1000) return 0;
    return 1;
}

const char*  kPackageName = "com.ihrsachin.mongodbrealmforandroid";
int LOOPER_ID_USER = 3;
const int SENSOR_REFRESH_RATE_HZ = 100;
constexpr int32_t SENSOR_REFRESH_PERIOD_US = int32_t(1000000 / SENSOR_REFRESH_RATE_HZ);

ASensorManager* AcquireASensorManagerInstance(void) {
    typedef ASensorManager *(*PF_GETINSTANCEFORPACKAGE)(const char *name);
    void* androidHandle = dlopen("libandroid.so", RTLD_NOW);
    PF_GETINSTANCEFORPACKAGE getInstanceForPackageFunc = (PF_GETINSTANCEFORPACKAGE)
            dlsym(androidHandle, "ASensorManager_getInstanceForPackage");
    if (getInstanceForPackageFunc) {
        return getInstanceForPackageFunc(kPackageName);
    }

    typedef ASensorManager *(*PF_GETINSTANCE)();
    PF_GETINSTANCE getInstanceFunc = (PF_GETINSTANCE)
            dlsym(androidHandle, "ASensorManager_getInstance");
    // by all means at this point, ASensorManager_getInstance should be available
    assert(getInstanceFunc);
    return getInstanceFunc();
}


////    sensorEventQueue1 = ASensorManager_createEventQueue(sensorManager, looper, 3, looperCallback, sensor_data);
////
////    ASensorEventQueue_enableSensor(sensorEventQueue1, accSensor);
////
////    ASensorEventQueue_setEventRate(sensorEventQueue1, accSensor, 100000);
////    if(ASensorEventQueue_getEvents(sensorEventQueue1, &event1, 1)){
////        LOGI("Acceleration X: ", event1.acceleration.x);
////        LOGI("Acceleration Y: ", event1.acceleration.y);
////        LOGI("Acceleration Z: ", event1.acceleration.z);
////    }

extern "C"
JNIEXPORT jstring JNICALL
Java_com_ihrsachin_mongodbrealmforandroid_AccelerometerJNI_accelerometer(JNIEnv *env,
                                                                         jobject thiz) {
    ASensorManager *sensorManager;
    const ASensor *accelerometer;
    ASensorEventQueue *accelerometerEventQueue;
    ALooper *looper;

    sensorManager = AcquireASensorManagerInstance();
    accelerometer = ASensorManager_getDefaultSensor(sensorManager, ASENSOR_TYPE_ACCELEROMETER);
    looper = ALooper_prepare(ALOOPER_PREPARE_ALLOW_NON_CALLBACKS);

    accelerometerEventQueue = ASensorManager_createEventQueue(sensorManager, looper,
                                                              LOOPER_ID_USER, NULL, NULL);
    auto status = ASensorEventQueue_enableSensor(accelerometerEventQueue,
                                                 accelerometer);
    status = ASensorEventQueue_setEventRate(accelerometerEventQueue,
                                            accelerometer,
                                            SENSOR_REFRESH_PERIOD_US);
    (void)status;

    ALooper_pollAll(0, NULL, NULL, NULL);
    ASensorEvent event;
    if (ASensorEventQueue_getEvents(accelerometerEventQueue, &event, 1) > 0) {
        LOGI("ACC X", event.acceleration.x);
        LOGI("ACC Y", event.acceleration.y);
        LOGI("ACC Z", event.acceleration.z);
    }

    std::string result = ("x: " + std::to_string(event.acceleration.x) + "\ny: " + std::to_string(event.acceleration.y) + "\nz: " + std::to_string(event.acceleration.z));
    return env->NewStringUTF(result.c_str());
}
