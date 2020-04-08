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
static const char *pDevice[2] = {
    "/dev/ttyS0",
    "/dev/tty",
};

static speed_t getBaudrate(jint baudrate)
{
	switch(baudrate) {
	case 0: return B0;
	case 50: return B50;
	case 75: return B75;
	case 110: return B110;
	case 134: return B134;
	case 150: return B150;
	case 200: return B200;
	case 300: return B300;
	case 600: return B600;
	case 1200: return B1200;
	case 1800: return B1800;
	case 2400: return B2400;
	case 4800: return B4800;
	case 9600: return B9600;
	case 19200: return B19200;
	case 38400: return B38400;
	case 57600: return B57600;
	case 115200: return B115200;
	case 230400: return B230400;
	case 460800: return B460800;
	case 500000: return B500000;
	case 576000: return B576000;
	case 921600: return B921600;
	case 1000000: return B1000000;
	case 1152000: return B1152000;
	case 1500000: return B1500000;
	case 2000000: return B2000000;
	case 2500000: return B2500000;
	case 3000000: return B3000000;
	case 3500000: return B3500000;
	case 4000000: return B4000000;
	default: return -1;
	}
}

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
  (JNIEnv *, jclass, jint)
 {

    return 0;
 }

/*
 * Class:     com_friendlyarm_FriendlyThings_HardwareController
 * Method:    setRxMode
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_friendlyarm_FriendlyThings_HardwareController_setRxMode
  (JNIEnv *, jclass, jint)
{
    return 0;
}
