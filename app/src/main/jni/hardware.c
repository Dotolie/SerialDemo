//
// Created by ywkim on 2020-04-05.
//
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <stdio.h>
#include <jni.h>
#include "hardware.h"
#include "android/log.h"



#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

static const char *TAG="Hardware";


static const char *pGpios[7] = {
    "/sys/class/gpio/gpio35/value",
    "/sys/class/gpio/gpio35/value",
    "/sys/class/gpio/gpio66/value",
    "/sys/class/gpio/gpio67/value",
    "/sys/class/gpio/gpio68/value",
    "/sys/class/gpio/gpio69/value",
    "/sys/class/gpio/gpio70/value",
};

/*
 * Class:     com_friendlyarm_FriendlyThings_HardwareController
 * Method:    setGPIOValue
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_friendlyarm_FriendlyThings_HardwareController_setGPIOValue
  (JNIEnv *env, jclass thiz, jint gpio, jint out)
{
       	int fd, len;
    	char buf[60];

        LOGD("setGpio : %s=%d ",pGpios[gpio], out);

    	len = snprintf(buf, sizeof(buf), "%s", pGpios[gpio]);

    	fd = open(buf, O_WRONLY);
    	if (fd < 0) {
    		return -1;
    	}

    	if (out){ write(fd, "1", 1); }
       	else {	write(fd, "0", 1); }

    	close(fd);


        return fd;
}

/*
 * Class:     com_friendlyarm_FriendlyThings_HardwareController
 * Method:    getGPIOValue
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_friendlyarm_FriendlyThings_HardwareController_getGPIOValue
  (JNIEnv *env, jclass thiz, jint gpio)
{
       	int fd, len;
       	int val;
    	char buf[4] = {0,};

        LOGD("getGpio : %s=%d ",pGpios[gpio], val);

    	len = snprintf(buf, sizeof(buf), "%s", pGpios[gpio]);

    	fd = open(buf, O_RDONLY);
    	if (fd < 0) {
    		return -1;
    	}

    	read(fd, buf, 1);
        val = atoi(buf);

    	close(fd);

        return val;
}

/*
 * Class:     com_friendlyarm_FriendlyThings_HardwareController
 * Method:    setTxMode
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_friendlyarm_FriendlyThings_HardwareController_setTxMode
  (JNIEnv *env, jclass thiz, jint pin)
 {

    return 0;
 }

/*
 * Class:     com_friendlyarm_FriendlyThings_HardwareController
 * Method:    setRxMode
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_friendlyarm_FriendlyThings_HardwareController_setRxMode
  (JNIEnv *env, jclass thiz, jint pin)
{
    return 0;
}
