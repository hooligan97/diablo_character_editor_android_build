#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_d1editor_MainActivity_stringFromJNI(JNIEnv* env, jobject /* obj */) {
    std::string message = "Diablo Character Editor native library loaded successfully for modern Android devices";
    return env->NewStringUTF(message.c_str());
}
