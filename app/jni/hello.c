#include <string.h>
#include <jni.h>
#include <stdio.h>

void Java_com_example_calljava_MainActivity_callJava(JNIEnv* env, jobject thiz){
    jclass jCallJava = (*env)->FindClass(env, "com/example/calljava/MainActivity");
    //jclass jCallJava = (*env)->GetObjectClass(env, thiz);

    jmethodID testToast = (*env)->GetStaticMethodID(env, jCallJava, "testToast", "()V");
    (*env)->CallStaticVoidMethod(env, jCallJava, testToast);
}

jstring Java_com_example_calljava_MainActivity_stringFromJNI(JNIEnv* env, jobject thiz){
    return (*env)->NewStringUTF(env, "Hello, JNI World!");
}
