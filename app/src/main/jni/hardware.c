//
// Created by ywkim on 2020-04-05.
//
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <linux/types.h>
#include <linux/spi/spidev.h>
#include <fcntl.h>
#include <string.h>
#include <errno.h>
#include <stdio.h>
#include <jni.h>
#include "hardware.h"
#include "android/log.h"



#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

static const char *TAG="Hardware";


static const char *pGpios[9] = {
    "/sys/devices/platform/pinctrl/gpio/gpio35/value",

    "/sys/devices/platform/pinctrl/gpio/gpio55/value",
    "/sys/devices/platform/pinctrl/gpio/gpio56/value",

    "/sys/devices/platform/pinctrl/gpio/gpio2/value",
    "/sys/devices/platform/pinctrl/gpio/gpio66/value",

    "/sys/devices/platform/pinctrl/gpio/gpio67/value",
    "/sys/devices/platform/pinctrl/gpio/gpio68/value",
    "/sys/devices/platform/pinctrl/gpio/gpio69/value",
    "/sys/devices/platform/pinctrl/gpio/gpio70/value",
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

//        LOGD("setGpio : %s=%d ",pGpios[gpio], out);

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

//        LOGD("getGpio : %s=%d ",pGpios[gpio], val);

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
 * Method:    open
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_friendlyarm_FriendlyThings_HardwareController_open
  (JNIEnv *env, jclass thiz)
{
    int nRet = 0;
    int nFd = -1;
    unsigned char mode = 0;

    nFd = open("/dev/spidev32766.0", O_RDWR);
//    LOGD("spi open : /dev/spidev32766.0 ,nFd=%d", nFd);

    mode = SPI_CPHA | SPI_CPOL;
    nRet = ioctl(nFd, SPI_IOC_WR_MODE, &mode);
    LOGD("iotcl mode=%x, nRet=%d", mode, nRet);


    return nFd;
}

/*
 * Class:     com_friendlyarm_FriendlyThings_HardwareController
 * Method:    close
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_friendlyarm_FriendlyThings_HardwareController_close
  (JNIEnv *env, jclass thiz, jint fd)
{
    LOGD("spi close : nFd=%d", fd);

    close(fd);
}


/*
 * Class:     com_friendlyarm_FriendlyThings_HardwareController
 * Method:    SPItransferBytes
 * Signature: (I[B[BIII)I
 */
JNIEXPORT jint JNICALL Java_com_friendlyarm_FriendlyThings_HardwareController_SPItransferBytes
  (JNIEnv *env, jclass thiz, jint fd, jbyteArray jtx, jbyteArray jrx, jint delay, jint speed, jint bits)
{
    int i = 0;
    int nRet = 0;
    unsigned char tx[16] = { 0,};
    unsigned char rx[16] = { 0,};
    size_t len = (*env)->GetArrayLength(env, jtx);

    struct spi_ioc_transfer tr = {
        .tx_buf = (unsigned long)tx,
        .rx_buf = (unsigned long)rx,
        .len = len, //ARRAY_SIZE(tx),
        .delay_usecs = delay,
        .speed_hz = speed,
        .bits_per_word = bits,
    };


    jbyte *nativeBytes = (*env)->GetByteArrayElements(env, jtx, 0);

    LOGD("fd=%d, delay=%d, speed=%d, bits=%d", fd, delay, speed, bits);

    memcpy(tx, nativeBytes, len);
//    for(i=0;i<len;i++) {
//        LOGD("tx[%d]=%x", i, nativeBytes[i]);
//    }

    nRet = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
    LOGD("iotcl tr, nRet=%d", nRet);

//    for(i=0;i<len;i++) {
//        LOGD("rx[%d]=%x", i, rx[i]);
//    }

    (*env)->SetByteArrayRegion(env, jrx, 0, len, (jbyte*)rx);
    (*env)->ReleaseByteArrayElements( env, jtx, nativeBytes, JNI_ABORT);

    return len;
}