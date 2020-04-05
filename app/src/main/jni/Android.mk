LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := friendlyarm-things
LOCAL_SRC_FILES := hardware.c
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
