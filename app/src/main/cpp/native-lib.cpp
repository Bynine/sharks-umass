#include <jni.h>
#include <string>

extern "C"
jstring
Java_sharks_1umass_scanit_CameraViewActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
