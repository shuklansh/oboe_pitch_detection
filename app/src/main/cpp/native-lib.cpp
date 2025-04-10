#include <jni.h>
#include <android/log.h>
#include <oboe/Oboe.h>
#include <cmath>
#include <chrono>

using Clock = std::chrono::steady_clock;
auto lastPitchTime = Clock::now(); // Initialize with current time

#define LOG_TAG "PitchNative"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

// Pointer to the JavaVM for callbacks
JavaVM *javaVM = nullptr;
jobject globalCallback = nullptr;

float detectPitch(const float *buffer, int32_t numFrames) {
    // Naive zero-crossing pitch detection
    int zeroCrossings = 0;
    for (int i = 1; i < numFrames; ++i) {
        if ((buffer[i - 1] > 0 && buffer[i] <= 0) || (buffer[i - 1] < 0 && buffer[i] >= 0)) {
            zeroCrossings++;
        }
    }
    float pitch = zeroCrossings * 44100.0f / (2.0f * numFrames);
    return pitch;
}

class PitchProcessor : public oboe::AudioStreamCallback {
public:
    oboe::AudioStream *stream = nullptr;

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *audioStream,
                                          void *audioData,
                                          int32_t numFrames) override {
        float *floatData = static_cast<float *>(audioData);
        float pitch = detectPitch(floatData, numFrames);

        if (pitch > 10.0f && pitch < 20000.0f) {
            auto now = Clock::now();
            std::chrono::duration<float> timeElapsed = now - lastPitchTime;

            if (timeElapsed.count() >= 0.5f) { // Only send pitch every 2 seconds
                lastPitchTime = now;

                JNIEnv *env = nullptr;
                javaVM->AttachCurrentThread(&env, nullptr);
                jclass clazz = env->GetObjectClass(globalCallback);
                jmethodID method = env->GetMethodID(clazz, "onPitchDetected", "(F)V");
                if (method != nullptr) {
                    env->CallVoidMethod(globalCallback, method, pitch);
                }
            }
        }

        return oboe::DataCallbackResult::Continue;
    }
};


PitchProcessor *processor = nullptr;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oboepitch_MainActivity_stopListening(JNIEnv *env, jobject thiz) {
    if (processor && processor->stream) {
        processor->stream->stop();
        processor->stream->close();
        processor->stream = nullptr;
        LOGD("Audio stream stopped.");
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_oboepitch_MainActivity_startListening(JNIEnv *env, jobject thiz) {
    if (processor == nullptr) processor = new PitchProcessor();

    oboe::AudioStreamBuilder builder;
    builder.setCallback(processor)
            ->setFormat(oboe::AudioFormat::Float)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Exclusive)
            ->setDirection(oboe::Direction::Input)
            ->setSampleRate(44100)
            ->setChannelCount(1);

    oboe::Result result = builder.openStream(&(processor->stream));
    if (result == oboe::Result::OK) {
        processor->stream->requestStart();
        LOGD("Audio stream started.");
    } else {
        LOGD("Failed to start stream.");
    }

    // Save Java callback reference
    globalCallback = env->NewGlobalRef(thiz);
}

jint JNI_OnLoad(JavaVM *vm, void *) {
    javaVM = vm;
    return JNI_VERSION_1_6;
}
